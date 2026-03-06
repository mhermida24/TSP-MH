import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {

        try {

            String[] archivos = {"br17.atsp", "p43.atsp"};
            int[][] distancias;

            int[] tamanosPoblacion = {50, 100, 200};
            double[] probsCruce = {0.7, 0.9};
            double[] probsMutacion = {0.01, 0.05, 0.1};
            int numGeneraciones = 1000;

            TipoSeleccion[] selecciones = {TipoSeleccion.TORNEO, TipoSeleccion.RULETA};
            TipoCruce[] cruces = {TipoCruce.OX, TipoCruce.PMX};
            TipoMutacion[] mutaciones = {TipoMutacion.SWAP, TipoMutacion.INVERSION};
            TipoReemplazo[] reemplazos = {TipoReemplazo.GENERACIONAL, TipoReemplazo.ELITISMO};

            PrintWriter writerCSV = new PrintWriter(new FileWriter("resultados.csv"));
            PrintWriter writerTiempo = new PrintWriter(new FileWriter("tiempos.txt"));

            writerCSV.println("Archivo;Poblacion;ProbCruce;ProbMutacion;Seleccion;Cruce;Mutacion;Reemplazo;PromedioFitness;MejorFitness;Desvio;CostoMejor;Recorrido");

            long tiempoInicioTotal = System.currentTimeMillis();

            for (String archivo : archivos) {

                long inicioArchivo = System.currentTimeMillis();

                System.out.println("\n=== Procesando archivo: " + archivo + " ===");

                distancias = ATSPReader.leerATSP(archivo);

                Individuo mejorArchivo = null;
                double mejorFitnessArchivo = Double.NEGATIVE_INFINITY;

                for (int tamanoPoblacion : tamanosPoblacion) {
                    for (double probCruce : probsCruce) {
                        for (double probMutacion : probsMutacion) {

                            System.out.println("\nConfiguración:");
                            System.out.println("Población: " + tamanoPoblacion +
                                    " Cruce: " + probCruce +
                                    " Mutación: " + probMutacion);

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

                                            double costoMejor = 1.0 / mejorFitness;

                                            StringBuilder recorrido = new StringBuilder();

                                            for (int i = 0; i < mejorIndividuo.getPermutacion().size(); i++) {
                                                recorrido.append(mejorIndividuo.getPermutacion().get(i));
                                                if (i < mejorIndividuo.getPermutacion().size() - 1) {
                                                    recorrido.append("-");
                                                }
                                            }

                                            writerCSV.println(
                                                    archivo + ";" +
                                                            tamanoPoblacion + ";" +
                                                            probCruce + ";" +
                                                            probMutacion + ";" +
                                                            ts + ";" +
                                                            tc + ";" +
                                                            tm + ";" +
                                                            tr + ";" +
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
                    }
                }

                long finArchivo = System.currentTimeMillis();
                double tiempoArchivo = (finArchivo - inicioArchivo) / 1000.0;

                writerTiempo.println("Archivo: " + archivo + " Tiempo: " + tiempoArchivo + " segundos");

                System.out.println("\nMejor solución para " + archivo);
                System.out.println("Fitness: " + mejorArchivo.getFitness());

                System.out.print("Recorrido: ");
                for (Integer ciudad : mejorArchivo.getPermutacion()) {
                    System.out.print(ciudad + " ");
                }

                System.out.println("\nTiempo: " + tiempoArchivo + " segundos");
            }

            long tiempoFinTotal = System.currentTimeMillis();
            double tiempoTotal = (tiempoFinTotal - tiempoInicioTotal) / 1000.0;

            writerTiempo.println("Tiempo total ejecución: " + tiempoTotal + " segundos");

            writerCSV.close();
            writerTiempo.close();

            System.out.println("\nResultados guardados en resultados.csv");
            System.out.println("Tiempos guardados en tiempos.txt");

        } catch (IOException e) {
            System.out.println("Error leyendo archivo ATSP");
            e.printStackTrace();
        }
    }

}