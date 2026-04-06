import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        try {

            String[] archivos = {"br17.atsp", "p43.atsp"};
            int[][] distancias;

            int[] tamanosPoblacion = {50, 100};
            double[] probsCruce = {0.7, 0.9};
            double[] probsMutacion = {0.01, 0.05};
            int numGeneraciones = 1000;

            ArrayList<EstrategiaSeleccion> selecciones = new ArrayList<>();
            EstrategiaSeleccion seleccionRuleta = new SeleccionRuleta();
            EstrategiaSeleccion seleccionTorneo = new SeleccionTorneo(3);
            selecciones.add(seleccionRuleta);
            selecciones.add(seleccionTorneo);

            ArrayList<EstrategiaCruce> cruces = new ArrayList<>();
            EstrategiaCruce cruceOX = new CruceOX();
            EstrategiaCruce crucePMX = new CrucePMX();
            cruces.add(cruceOX);
            cruces.add(crucePMX);

            ArrayList<EstrategiaMutacion> mutaciones = new ArrayList<>();
            EstrategiaMutacion mutacionSwap = new MutacionSwap();
            EstrategiaMutacion mutacionInversion = new MutacionInversion();
            mutaciones.add(mutacionSwap);
            mutaciones.add(mutacionInversion);

            ArrayList<EstrategiaReemplazo> reemplazos = new ArrayList<>();
            EstrategiaReemplazo reemplazoGeneracional = new ReemplazoGeneracional();
            EstrategiaReemplazo reemplazoElitista = new ReemplazoElitismo();
            reemplazos.add(reemplazoGeneracional);
            reemplazos.add(reemplazoElitista);

            PrintWriter writerCSV = new PrintWriter(new FileWriter("resultados.csv"));
            PrintWriter writerTiempo = new PrintWriter(new FileWriter("tiempos.txt"));

            writerCSV.println("Archivo;Configuracion;Poblacion;ProbCruce;ProbMutacion;PromedioFitness;MejorFitness;Desvio;CostoMejor;Recorrido");

            int totalExperimentos = 192;
            int experimentoActual = 0;

            long tiempoInicioTotal = System.currentTimeMillis();

            for (String archivo : archivos) {

                long inicioArchivo = System.currentTimeMillis();

                distancias = ATSPReader.leerMatrizATSP(archivo);
                EvaluadorFitness evaluador = new EvaluadorFitness(distancias);
                double mejorFitnessArchivo = Double.NEGATIVE_INFINITY;

                for (int config = 0; config < 2; config++) {

                    int tamanoPoblacion = tamanosPoblacion[config];
                    double probCruce = probsCruce[config];
                    double probMutacion = probsMutacion[config];

                    for (EstrategiaSeleccion estrategiaSeleccion : selecciones) {
                        for (EstrategiaCruce estrategiaCruce : cruces) {
                            for (EstrategiaMutacion estrategiaMutacion : mutaciones) {
                                for (EstrategiaReemplazo estrategiaReemplazo : reemplazos) {

                                    double[] fitnessCorridas = new double[3];

                                    double mejorFitness = Double.NEGATIVE_INFINITY;
                                    Individuo mejorIndividuo = null;

                                    for (int repeticion = 0; repeticion < 3; repeticion++) {

                                        AlgoritmoEvolutivo algoritmo = new AlgoritmoEvolutivo(
                                                estrategiaSeleccion,
                                                estrategiaCruce,
                                                estrategiaMutacion,
                                                estrategiaReemplazo,
                                                evaluador,
                                                tamanoPoblacion,
                                                probCruce,
                                                probMutacion,
                                                numGeneraciones,
                                                distancias
                                        );

                                        System.out.println(
                                                "[" + experimentoActual + "/" + totalExperimentos + "] " +
                                                        archivo + " " +
                                                        estrategiaSeleccion.getNombre() + "-" + estrategiaCruce.getNombre() + "-" + estrategiaMutacion.getNombre() + "-" + estrategiaReemplazo.getNombre() +
                                                        " config " + config
                                        );

                                        Individuo mejor = algoritmo.ejecutar();

                                        fitnessCorridas[repeticion] = mejor.getFitness();

                                        if (mejor.getFitness() > mejorFitness) {
                                            mejorFitness = mejor.getFitness();
                                            mejorIndividuo = new Individuo(mejor);
                                        }

                                        if (mejor.getFitness() > mejorFitnessArchivo) {
                                            mejorFitnessArchivo = mejor.getFitness();
                                        }
                                        experimentoActual++;
                                    }

                                    double promedio = getPromedio(fitnessCorridas);

                                    double desvio = getDesvio(fitnessCorridas, promedio);

                                    int costoMejor = -1;
                                    String recorrido = "";

                                    if (mejorIndividuo != null) {
                                        costoMejor = mejorIndividuo.getCosto();
                                        recorrido = mejorIndividuo.getRecorridoComoTexto();
                                    }

                                    String configuracion = estrategiaSeleccion.getNombre() + "-" + estrategiaCruce.getNombre() + "-" + estrategiaMutacion.getNombre() + "-" + estrategiaReemplazo.getNombre();

                                    writerCSV.println(
                                            archivo + ";" +
                                                    configuracion + ";" +
                                                    tamanoPoblacion + ";" +
                                                    probCruce + ";" +
                                                    probMutacion + ";" +
                                                    promedio + ";" +
                                                    mejorFitness + ";" +
                                                    desvio + ";" +
                                                    costoMejor + ";" +
                                                    recorrido
                                    );
                                }
                            }
                        }
                    }
                }

                long finArchivo = System.currentTimeMillis();
                double tiempoArchivo = (finArchivo - inicioArchivo) / 1000.0;

                writerTiempo.println("Archivo: " + archivo + " Tiempo: " + tiempoArchivo + " segundos");
            }

            long tiempoFinTotal = System.currentTimeMillis();
            double tiempoTotal = (tiempoFinTotal - tiempoInicioTotal) / 1000.0;

            writerTiempo.println("Tiempo total ejecución: " + tiempoTotal + " segundos");

            writerCSV.close();
            writerTiempo.close();

        } catch (IOException e) {
            System.out.println("Error leyendo archivo ATSP");
            e.printStackTrace();
        }
    }

    private static double getDesvio(double[] fitnessCorridas, double promedio) {
        double sumaVar = 0;
        for (double f : fitnessCorridas) {
            sumaVar += Math.pow(f - promedio, 2);
        }

        double desvio = Math.sqrt(sumaVar / fitnessCorridas.length);
        return desvio;
    }

    private static double getPromedio(double[] fitnessCorridas) {
        double suma = 0;
        for (double f : fitnessCorridas) {
            suma += f;
        }

        double promedio = suma / fitnessCorridas.length;
        return promedio;
    }
}