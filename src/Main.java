import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {

            int[][] distancias = ATSPReader.leerATSP("p43.atsp");

            int tamanoPoblacion = 100;
            double probCruce = 0.9;
            double probMutacion = 0.05;
            int numGeneraciones = 1000;

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

            Individuo mejor = alg.ejecutar();

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