import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {

            // Leer archivo ATSP
            int[][] distancias = ATSPReader.leerATSP("p43.atsp");

            // Parámetros del algoritmo evolutivo
            int tamanoPoblacion = 100;
            double probCruce = 0.9;
            double probMutacion = 0.05;
            int numGeneraciones = 1000;

            // Crear algoritmo
            AlgEvol alg = new AlgEvol(TipoSeleccion.TORNEO,
                    TipoCruce.OX,
                    TipoMutacion.SWAP,
                    TipoReemplazo.ELITISMO,
                    tamanoPoblacion,
                    probCruce,
                    probMutacion,
                    numGeneraciones,
                    distancias
            );

            // Ejecutar algoritmo
            Individuo mejor = alg.ejecutar();

            // Mostrar resultado
            System.out.println("Mejor recorrido encontrado:");
            ArrayList<Integer> perm = mejor.getPermutacion();
            for (Integer ciudad : perm) {
                System.out.print(ciudad + " ");
            }
            System.out.println("\nFitness: " + mejor.getFitness());
            System.out.println("Distancia: " + 1.0/mejor.getFitness());

        } catch (IOException e) {
            System.out.println("Error leyendo el archivo ATSP");
            e.printStackTrace();
        }
    }
}