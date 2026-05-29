import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

    private static final String[] ARCHIVOS = {
            "br17.atsp",
            "p43.atsp"
    };

    private static final int[] TAMANOS_POBLACION = {50, 100};

    private static final double[] PROBS_CRUCE = {0.7, 0.9};

    private static final double[] PROBS_MUTACION = {0.01, 0.05};

    private static final int NUM_GENERACIONES = 1000;

    private static final int REPETICIONES = 3;

    public static void main(String[] args) {

        try (
                PrintWriter writerCSV =
                        new PrintWriter(new FileWriter("resultados.csv"));

                PrintWriter writerTiempo =
                        new PrintWriter(new FileWriter("tiempos.txt"))
        ) {

            escribirCabeceraCSV(writerCSV);

            ArrayList<EstrategiaSeleccion> selecciones =
                    crearSelecciones();

            ArrayList<EstrategiaCruce> cruces =
                    crearCruces();

            ArrayList<EstrategiaMutacion> mutaciones =
                    crearMutaciones();

            ArrayList<EstrategiaReemplazo> reemplazos =
                    crearReemplazos();

            long inicioTotal = System.currentTimeMillis();

            for (String archivo : ARCHIVOS) {

                ejecutarExperimentosArchivo(
                        archivo,
                        selecciones,
                        cruces,
                        mutaciones,
                        reemplazos,
                        writerCSV,
                        writerTiempo
                );
            }

            double tiempoTotal =
                    calcularTiempoSegundos(inicioTotal);

            writerTiempo.println(
                    "Tiempo total ejecución: "
                            + tiempoTotal +
                            " segundos"
            );

        } catch (IOException e) {

            System.out.println("Error leyendo archivo");

            e.printStackTrace();
        }
    }

    private static void ejecutarExperimentosArchivo(
            String archivo,
            ArrayList<EstrategiaSeleccion> selecciones,
            ArrayList<EstrategiaCruce> cruces,
            ArrayList<EstrategiaMutacion> mutaciones,
            ArrayList<EstrategiaReemplazo> reemplazos,
            PrintWriter writerCSV,
            PrintWriter writerTiempo
    ) throws IOException {

        long inicioArchivo = System.currentTimeMillis();

        int[][] distancias =
                ATSPReader.leerMatrizATSP(archivo);

        EvaluadorFitness evaluador =
                new EvaluadorFitness(distancias);

        for (int config = 0; config < 2; config++) {

            ConfiguracionAE configuracion =
                    new ConfiguracionAE(
                            TAMANOS_POBLACION[config],
                            PROBS_CRUCE[config],
                            PROBS_MUTACION[config],
                            NUM_GENERACIONES
                    );

            ejecutarConfiguraciones(
                    archivo,
                    config,
                    distancias,
                    evaluador,
                    configuracion,
                    selecciones,
                    cruces,
                    mutaciones,
                    reemplazos,
                    writerCSV
            );
        }

        double tiempoArchivo =
                calcularTiempoSegundos(inicioArchivo);

        writerTiempo.println(
                "Archivo: " +
                        archivo +
                        " Tiempo: " +
                        tiempoArchivo +
                        " segundos"
        );
    }

    private static void ejecutarConfiguraciones(
            String archivo,
            int config,
            int[][] distancias,
            EvaluadorFitness evaluador,
            ConfiguracionAE configuracion,
            ArrayList<EstrategiaSeleccion> selecciones,
            ArrayList<EstrategiaCruce> cruces,
            ArrayList<EstrategiaMutacion> mutaciones,
            ArrayList<EstrategiaReemplazo> reemplazos,
            PrintWriter writerCSV
    ) {

        for (EstrategiaSeleccion seleccion : selecciones) {

            for (EstrategiaCruce cruce : cruces) {

                for (EstrategiaMutacion mutacion : mutaciones) {

                    for (EstrategiaReemplazo reemplazo : reemplazos) {

                        ResultadoExperimento resultado =
                                ejecutarExperimento(
                                        archivo,
                                        config,
                                        distancias,
                                        evaluador,
                                        configuracion,
                                        seleccion,
                                        cruce,
                                        mutacion,
                                        reemplazo
                                );

                        escribirResultadoCSV(
                                writerCSV,
                                archivo,
                                configuracion,
                                resultado
                        );
                    }
                }
            }
        }
    }

    private static ResultadoExperimento ejecutarExperimento(
            String archivo,
            int config,
            int[][] distancias,
            EvaluadorFitness evaluador,
            ConfiguracionAE configuracion,
            EstrategiaSeleccion seleccion,
            EstrategiaCruce cruce,
            EstrategiaMutacion mutacion,
            EstrategiaReemplazo reemplazo
    ) {

        double[] fitnessCorridas =
                new double[REPETICIONES];

        double mejorFitness =
                Double.NEGATIVE_INFINITY;

        Individuo mejorIndividuo = null;

        for (int repeticion = 0;
             repeticion < REPETICIONES;
             repeticion++) {

            mostrarEstado(
                    archivo,
                    config,
                    seleccion,
                    cruce,
                    mutacion,
                    reemplazo,
                    repeticion
            );

            AlgoritmoEvolutivo algoritmo =
                    new AlgoritmoEvolutivo(
                            seleccion,
                            cruce,
                            mutacion,
                            reemplazo,
                            evaluador,
                            configuracion,
                            distancias
                    );

            Individuo mejor =
                    algoritmo.ejecutar();

            fitnessCorridas[repeticion] =
                    mejor.getFitness();

            if (mejor.getFitness() > mejorFitness) {

                mejorFitness =
                        mejor.getFitness();

                mejorIndividuo =
                        new Individuo(mejor);
            }
        }

        double promedio =
                calcularPromedio(fitnessCorridas);

        double desvio =
                calcularDesvio(
                        fitnessCorridas,
                        promedio
                );

        String nombreConfiguracion =
                seleccion.getNombre() + "-" +
                        cruce.getNombre() + "-" +
                        mutacion.getNombre() + "-" +
                        reemplazo.getNombre();

        return new ResultadoExperimento(
                nombreConfiguracion,
                promedio,
                mejorFitness,
                desvio,
                mejorIndividuo
        );
    }

    private static void escribirResultadoCSV(
            PrintWriter writerCSV,
            String archivo,
            ConfiguracionAE configuracion,
            ResultadoExperimento resultado
    ) {

        int costo = -1;

        String recorrido = "";

        if (resultado.mejorIndividuo != null) {

            costo =
                    resultado.mejorIndividuo.getCosto();

            recorrido =
                    resultado.mejorIndividuo
                            .getRecorridoComoTexto();
        }

        writerCSV.println(
                archivo + ";" +
                        resultado.configuracion + ";" +
                        configuracion.getTamanoPoblacion() + ";" +
                        configuracion.getProbCruce() + ";" +
                        configuracion.getProbMutacion() + ";" +
                        resultado.promedio + ";" +
                        resultado.mejorFitness + ";" +
                        resultado.desvio + ";" +
                        costo + ";" +
                        recorrido
        );
    }

    private static void mostrarEstado(
            String archivo,
            int config,
            EstrategiaSeleccion seleccion,
            EstrategiaCruce cruce,
            EstrategiaMutacion mutacion,
            EstrategiaReemplazo reemplazo,
            int repeticion
    ) {

        System.out.println(
                archivo + " | " +
                        seleccion.getNombre() + "-" +
                        cruce.getNombre() + "-" +
                        mutacion.getNombre() + "-" +
                        reemplazo.getNombre() +
                        " | Config: " +
                        config +
                        " | Repetición: " +
                        (repeticion + 1)
        );
    }

    private static void escribirCabeceraCSV(
            PrintWriter writerCSV
    ) {

        writerCSV.println(
                "Archivo;Configuracion;" +
                        "Poblacion;ProbCruce;" +
                        "ProbMutacion;" +
                        "PromedioFitness;" +
                        "MejorFitness;" +
                        "Desvio;" +
                        "CostoMejor;" +
                        "Recorrido"
        );
    }

    private static double calcularPromedio(
            double[] valores
    ) {

        double suma = 0;

        for (double valor : valores) {

            suma += valor;
        }

        return suma / valores.length;
    }

    private static double calcularDesvio(
            double[] valores,
            double promedio
    ) {

        double sumaVarianza = 0;

        for (double valor : valores) {

            sumaVarianza +=
                    Math.pow(valor - promedio, 2);
        }

        return Math.sqrt(
                sumaVarianza / valores.length
        );
    }

    private static double calcularTiempoSegundos(
            long inicio
    ) {

        return (System.currentTimeMillis() - inicio)
                / 1000.0;
    }

    private static ArrayList<EstrategiaSeleccion>
    crearSelecciones() {

        ArrayList<EstrategiaSeleccion> lista =
                new ArrayList<>();

        lista.add(new SeleccionRuleta());

        lista.add(new SeleccionTorneo(3));

        return lista;
    }

    private static ArrayList<EstrategiaCruce>
    crearCruces() {

        ArrayList<EstrategiaCruce> lista =
                new ArrayList<>();

        lista.add(new CruceOX());

        lista.add(new CrucePMX());

        return lista;
    }

    private static ArrayList<EstrategiaMutacion>
    crearMutaciones() {

        ArrayList<EstrategiaMutacion> lista =
                new ArrayList<>();

        lista.add(new MutacionSwap());

        lista.add(new MutacionInversion());

        return lista;
    }

    private static ArrayList<EstrategiaReemplazo>
    crearReemplazos() {

        ArrayList<EstrategiaReemplazo> lista =
                new ArrayList<>();

        lista.add(new ReemplazoGeneracional());

        lista.add(new ReemplazoElitismo());

        return lista;
    }

    private static class ResultadoExperimento {

        private String configuracion;

        private double promedio;

        private double mejorFitness;

        private double desvio;

        private Individuo mejorIndividuo;

        public ResultadoExperimento(
                String configuracion,
                double promedio,
                double mejorFitness,
                double desvio,
                Individuo mejorIndividuo
        ) {

            this.configuracion = configuracion;
            this.promedio = promedio;
            this.mejorFitness = mejorFitness;
            this.desvio = desvio;
            this.mejorIndividuo = mejorIndividuo;
        }
    }
}