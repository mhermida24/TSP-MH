import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Individuo {

    private ArrayList<Integer> permutacion;
    private double fitness;
    private static Random rand = new Random();

    public Individuo(int cantidadCiudades) {
        permutacion = new ArrayList<>();
        for (int i = 0; i < cantidadCiudades; i++) {
            permutacion.add(i);
        }
        Collections.shuffle(permutacion, rand);
        fitness = 0;
    }

    public Individuo(ArrayList<Integer> permutacion) {
        this.permutacion = new ArrayList<>(permutacion);
        fitness = 0;
    }

    public Individuo(Individuo i){
        permutacion = new ArrayList<>(i.getPermutacion());
        fitness = i.getFitness();
    }

    public ArrayList<Integer> getPermutacion() {
        return permutacion;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}