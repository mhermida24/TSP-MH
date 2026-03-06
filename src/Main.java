import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            // Archivos a procesar
            String[] archivos = {"br17.atsp","p43.atsp"};
            int[][] distancias;

            // Dos configuraciones básicas de parámetros
            int[] tamanosPoblacion = {50, 100};
            double[] probsCruce = {0.7, 0.9};
            double[] probsMutacion = {0.01, 0.05};
            int numGeneraciones = 1000;

            // Arrays de operadores
            TipoSeleccion[] selecciones = {TipoSeleccion.TORNEO, TipoSeleccion.RULETA};
            TipoCruce[] cruces = {TipoCruce.OX, TipoCruce.PMX};
            TipoMutacion[] mutaciones = {TipoMutacion.SWAP, TipoMutacion.INVERSION};
            TipoReemplazo[] reemplazos = {TipoReemplazo.GENERACIONAL, TipoReemplazo.ELITISMO};

            long tiempoInicio = System.currentTimeMillis(); // inicio del cronómetro

            for (String archivo : archivos) {
                System.out.println("\n=== Procesando archivo: " + archivo + " ===");
                distancias = ATSPReader.leerATSP(archivo);

                // Mejor solución por archivo
                Individuo mejorArchivo = null;
                double mejorFitnessArchivo = Double.NEGATIVE_INFINITY;
                String parametrosMejorArchivo = "";

                // Recorremos las dos configuraciones básicas
                for (int config = 0; config < 2; config++) {

                    int tamanoPoblacion = tamanosPoblacion[config];
                    double probCruce = probsCruce[config];
                    double probMutacion = probsMutacion[config];

                    System.out.println("\n=== Configuración básica " + (config + 1) + " ===");
                    System.out.printf("Población: %d, Cruce: %.2f, Mutación: %.2f, Generaciones: %d%n",
                            tamanoPoblacion, probCruce, probMutacion, numGeneraciones);

                    // Probamos todas las combinaciones de operadores
                    for (TipoSeleccion ts : selecciones) {
                        for (TipoCruce tc : cruces) {
                            for (TipoMutacion tm : mutaciones) {
                                for (TipoReemplazo tr : reemplazos) {

                                    // Ejecutar 3 veces la misma combinación
                                    for (int repeticion = 1; repeticion <= 3; repeticion++) {

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

                                        // Mostrar resultado de esta ejecución
                                        System.out.printf(
                                                "Rep %d | Selección: %s | Cruce: %s | Mutación: %s | Reemplazo: %s -> Fitness: %.5f%n",
                                                repeticion, ts, tc, tm, tr, mejor.getFitness()
                                        );

                                        // Mostrar recorrido
                                        System.out.print("Recorrido: ");
                                        for (Integer ciudad : mejor.getPermutacion()) {
                                            System.out.print(ciudad + " ");
                                        }
                                        System.out.println();

                                        // Actualizar mejor por archivo si corresponde
                                        if (mejor.getFitness() > mejorFitnessArchivo) {
                                            mejorArchivo = new Individuo(mejor);
                                            mejorFitnessArchivo = mejor.getFitness();
                                            parametrosMejorArchivo = String.format(
                                                    "Población: %d, Cruce: %.2f, Mutación: %.2f, Generaciones: %d, Selección: %s, CruceOperador: %s, MutaciónOperador: %s, Reemplazo: %s",
                                                    tamanoPoblacion, probCruce, probMutacion, numGeneraciones,
                                                    ts, tc, tm, tr
                                            );
                                        }
                                    }

                                }
                            }
                        }
                    }

                }

                long tiempoFin = System.currentTimeMillis();
                double tiempoSegundos = (tiempoFin - tiempoInicio) / 1000.0;

                // Mostrar mejor solución para este archivo
                System.out.println("\n=== Mejor solución lograda para " + archivo + " ===");
                System.out.println("Fitness: " + mejorArchivo.getFitness());
                System.out.println("Parámetros: " + parametrosMejorArchivo);
                System.out.print("Recorrido: ");
                for (Integer ciudad : mejorArchivo.getPermutacion()) {
                    System.out.print(ciudad + " ");
                }
                System.out.println("\nTiempo acumulado hasta ahora: " + tiempoSegundos + " segundos");
            }

        } catch (IOException e) {
            System.out.println("Error leyendo el archivo ATSP");
            e.printStackTrace();
        }

    }
}