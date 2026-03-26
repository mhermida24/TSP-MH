import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MutacionInversion {
    private static final Random rand = new Random();

    public MutacionInversion(){
    }

    public void mutar(Individuo individuo, double probMutacion, int cantCiudades){
        double prob = rand.nextDouble();
        if (prob < probMutacion) {
            int p1 = rand.nextInt(cantCiudades);
            int p2 = rand.nextInt(cantCiudades);
            while (p1 == p2){
                p2 = rand.nextInt(cantCiudades);
            }
            int inicio = min(p1, p2);
            int fin = max(p1, p2);
            ArrayList<Integer> permutacion = individuo.getPermutacion();
            while (inicio < fin) {
                int ciudadInicio = permutacion.get(inicio);
                int ciudadFin = permutacion.get(fin);
                permutacion.set(inicio, ciudadFin);
                permutacion.set(fin, ciudadInicio);
                inicio++;
                fin--;
            }
        }
    }
}
