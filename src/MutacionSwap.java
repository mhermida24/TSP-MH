import java.util.ArrayList;
import java.util.Random;

public class MutacionSwap implements EstrategiaMutacion {
    private static final Random rand = new Random();
    public MutacionSwap(){
    }

    @Override
    public void mutar(Individuo individuo, double probMutacion, int cantCiudades){
        double prob = rand.nextDouble();
        if (prob < probMutacion) {
            int p1 = rand.nextInt(cantCiudades);
            int p2 = rand.nextInt(cantCiudades);
            while (p1 == p2){
                p2 = rand.nextInt(cantCiudades);
            }
            ArrayList<Integer> permutacion = individuo.getPermutacion();
            int ciudad1 = permutacion.get(p1);
            int ciudad2 = permutacion.get(p2);
            permutacion.set(p1, ciudad2);
            permutacion.set(p2, ciudad1);
        }
    }

    @Override
    public String getNombre() {
        return "SWAP";
    }
}
