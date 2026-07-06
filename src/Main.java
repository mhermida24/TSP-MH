import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

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
                        new PrintWriter(new FileWriter("resultados.csv"))
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

            for (String archivo : ARCHIVOS) {

                ejecutarExperimentosArchivo(
                        archivo,
                        selecciones,
                        cruces,
                        mutaciones,
                        reemplazos,
                        writerCSV
                );
            }

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
            PrintWriter writerCSV
    ) throws IOException {

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

        int[] costosCorridas = new int[REPETICIONES];

        int mejorCosto = Integer.MAX_VALUE;

        Individuo mejorIndividuo = null;

        double tiempoTotal = 0;

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

            long inicio = System.nanoTime();

            Individuo mejor =
                    algoritmo.ejecutar();

            long fin = System.nanoTime();

            tiempoTotal += (fin - inicio) / 1_000_000_000.0;

            costosCorridas[repeticion] = mejor.getCosto();

            if (mejor.getCosto() < mejorCosto) {
                mejorCosto = mejor.getCosto();
                mejorIndividuo = new Individuo(mejor);
            }
        }

        double promedioCosto =
                calcularPromedio(costosCorridas);

        double desvioCosto =
                calcularDesvio(costosCorridas, promedioCosto);

        String nombreConfiguracion =
                seleccion.getNombre() + "-" +
                        cruce.getNombre() + "-" +
                        mutacion.getNombre() + "-" +
                        reemplazo.getNombre();

        return new ResultadoExperimento(
                nombreConfiguracion,
                promedioCosto,
                mejorCosto,
                desvioCosto,
                tiempoTotal,
                mejorIndividuo
        );
    }

    private static void escribirResultadoCSV(
            PrintWriter writerCSV,
            String archivo,
            ConfiguracionAE configuracion,
            ResultadoExperimento resultado
    ) {

        String recorrido = "";

        if (resultado.mejorIndividuo != null) {
            recorrido = resultado.mejorIndividuo.getRecorridoComoTexto();
        }

        String promedio = String.format(
                new Locale("es", "AR"),
                "%.4f",
                resultado.promedioCosto
        );

        String desvio = String.format(
                new Locale("es", "AR"),
                "%.4f",
                resultado.desvioCosto
        );

        String tiempo = String.format(
                new Locale("es", "AR"),
                "%.4f",
                resultado.tiempoTotal
        );

        writerCSV.println(
                archivo + ";" +
                        resultado.configuracion + ";" +
                        configuracion.getTamanoPoblacion() + ";" +
                        configuracion.getProbCruce() + ";" +
                        configuracion.getProbMutacion() + ";" +
                        resultado.mejorCosto + ";" +
                        promedio + ";" +
                        desvio + ";" +
                        tiempo + ";" +
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
                        " | Repeticion: " +
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
                        "MejorCosto;" +
                        "PromedioCosto;" +
                        "DesvioCosto;" +
                        "TiempoTotal;" +
                        "Recorrido"
        );
    }

    private static double calcularPromedio(int[] valores) {
        double suma = 0;

        for (int valor : valores) {
            suma += valor;
        }

        return suma / valores.length;
    }

    private static double calcularDesvio(int[] valores, double promedio) {
        double sumaVarianza = 0;

        for (int valor : valores) {
            sumaVarianza += Math.pow(valor - promedio, 2);
        }

        return Math.sqrt(sumaVarianza / valores.length);
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

        private double promedioCosto;

        private int mejorCosto;

        private double desvioCosto;

        private double tiempoTotal;

        private Individuo mejorIndividuo;


        public ResultadoExperimento(
                String configuracion,
                double promedioCosto,
                int mejorCosto,
                double desvioCosto,
                double tiempoTotal,
                Individuo mejorIndividuo
        ) {

            this.configuracion = configuracion;
            this.promedioCosto = promedioCosto;
            this.mejorCosto = mejorCosto;
            this.desvioCosto = desvioCosto;
            this.tiempoTotal = tiempoTotal;
            this.mejorIndividuo = mejorIndividuo;
        }
    }
}