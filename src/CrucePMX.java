import java.util.ArrayList;
import java.util.Random;

public class CrucePMX {
    private static final Random rand = new Random();

    public CrucePMX() {
    }

    ArrayList<Individuo> cruzar(Individuo padre1, Individuo padre2, double probCruce, int cantCiudades) {
        ArrayList<Individuo> hijos = new ArrayList<>();
        Individuo hijo1;
        Individuo hijo2;
        if (rand.nextDouble() < probCruce) {
            int c1 = rand.nextInt(cantCiudades);
            int c2 = rand.nextInt(cantCiudades);
            while (c1 == c2) {
                c2 = rand.nextInt(cantCiudades);
            }
            int corte1 = Math.min(c1, c2);
            int corte2 = Math.max(c1, c2);
            ArrayList<Integer> h1 = new ArrayList<>();
            ArrayList<Integer> h2 = new ArrayList<>();
            for (int i = 0; i < cantCiudades; i++) {
                h1.add(null);
                h2.add(null);
            }
            for (int i = corte1; i <= corte2; i++) {
                h1.set(i, padre2.getPermutacion().get(i));
                h2.set(i, padre1.getPermutacion().get(i));
            }
            for (int i = 0; i < cantCiudades; i++) {
                if (i >= corte1 && i <= corte2)
                    continue;
                int ciudad = padre1.getPermutacion().get(i);
                while (h1.contains(ciudad)) {
                    int index = padre2.getPermutacion().indexOf(ciudad);
                    ciudad = padre1.getPermutacion().get(index);
                }
                h1.set(i, ciudad);
            }
            for (int i = 0; i < cantCiudades; i++) {
                if (i >= corte1 && i <= corte2)
                    continue;
                int ciudad = padre2.getPermutacion().get(i);
                while (h2.contains(ciudad)) {
                    int index = padre1.getPermutacion().indexOf(ciudad);
                    ciudad = padre2.getPermutacion().get(index);
                }
                h2.set(i, ciudad);
            }
            hijo1 = new Individuo(h1);
            hijo2 = new Individuo(h2);
        } else {
            hijo1 = new Individuo(padre1);
            hijo2 = new Individuo(padre2);
        }
        hijos.add(hijo1);
        hijos.add(hijo2);
        return hijos;
    }
}
