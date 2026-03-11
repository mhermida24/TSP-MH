import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {

        try {

            String[] archivos = {"br17.atsp", "p43.atsp"};
            int[][] distancias;

            int[] tamanosPoblacion = {50, 100};
            double[] probsCruce = {0.7, 0.9};
            double[] probsMutacion = {0.01, 0.05};
            int numGeneraciones = 1000;

            TipoSeleccion[] selecciones = {TipoSeleccion.TORNEO, TipoSeleccion.RULETA};
            TipoCruce[] cruces = {TipoCruce.OX, TipoCruce.PMX};
            TipoMutacion[] mutaciones = {TipoMutacion.SWAP, TipoMutacion.INVERSION};
            TipoReemplazo[] reemplazos = {TipoReemplazo.GENERACIONAL, TipoReemplazo.ELITISMO};

            PrintWriter writerCSV = new PrintWriter(new FileWriter("resultados.csv"));
            PrintWriter writerTiempo = new PrintWriter(new FileWriter("tiempos.txt"));

            writerCSV.println("Archivo;Configuracion;Poblacion;ProbCruce;ProbMutacion;PromedioFitness;MejorFitness;Desvio;CostoMejor;Recorrido");

            int totalExperimentos = 192;
            int experimentoActual = 0;

            long tiempoInicioTotal = System.currentTimeMillis();

            for (String archivo : archivos) {

                long inicioArchivo = System.currentTimeMillis();

                distancias = ATSPReader.leerATSP(archivo);

                Individuo mejorArchivo = null;
                double mejorFitnessArchivo = Double.NEGATIVE_INFINITY;

                for (int config = 0; config < 2; config++) {

                    int tamanoPoblacion = tamanosPoblacion[config];
                    double probCruce = probsCruce[config];
                    double probMutacion = probsMutacion[config];

                    for (TipoSeleccion ts : selecciones) {
                        for (TipoCruce tc : cruces) {
                            for (TipoMutacion tm : mutaciones) {
                                for (TipoReemplazo tr : reemplazos) {

                                    double[] fitnessCorridas = new double[3];

                                    double mejorFitness = Double.NEGATIVE_INFINITY;
                                    Individuo mejorIndividuo = null;

                                    for (int repeticion = 0; repeticion < 3; repeticion++) {

                                        AlgEvol alg = new AlgEvol(
                                                ts,
                                                tc,
                                                tm,
                                                tr,
                                                tamanoPoblacion,
                                                probCruce,
                                                probMutacion,
                                                numGeneraciones,
                                                distancias
                                        );

                                        System.out.println(
                                                "[" + experimentoActual + "/" + totalExperimentos + "] " +
                                                        archivo + " " +
                                                        ts + "-" + tc + "-" + tm + "-" + tr +
                                                        " config " + config
                                        );

                                        Individuo mejor = alg.ejecutar();

                                        fitnessCorridas[repeticion] = mejor.getFitness();

                                        if (mejor.getFitness() > mejorFitness) {
                                            mejorFitness = mejor.getFitness();
                                            mejorIndividuo = new Individuo(mejor);
                                        }

                                        if (mejor.getFitness() > mejorFitnessArchivo) {
                                            mejorFitnessArchivo = mejor.getFitness();
                                            mejorArchivo = new Individuo(mejor);
                                        }
                                        experimentoActual++;
                                    }

                                    double suma = 0;
                                    for (double f : fitnessCorridas) {
                                        suma += f;
                                    }

                                    double promedio = suma / fitnessCorridas.length;

                                    double sumaVar = 0;
                                    for (double f : fitnessCorridas) {
                                        sumaVar += Math.pow(f - promedio, 2);
                                    }

                                    double desvio = Math.sqrt(sumaVar / fitnessCorridas.length);

                                    int costoMejor = mejorIndividuo != null ? mejorIndividuo.getCosto() : -1;

                                    StringBuilder recorrido = new StringBuilder();
                                    for (int i = 0; i < mejorIndividuo.getPermutacion().size(); i++) {
                                        recorrido.append(mejorIndividuo.getPermutacion().get(i));
                                        if (i < mejorIndividuo.getPermutacion().size() - 1) {
                                            recorrido.append("-");
                                        }
                                    }

                                    String configuracion = ts + "-" + tc + "-" + tm + "-" + tr;

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
}