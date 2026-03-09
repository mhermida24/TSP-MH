import java.util.ArrayList;

public class Poblacion {

    private ArrayList<Individuo> individuos;

    public Poblacion() {
        individuos = new ArrayList<>();
    }

    public Poblacion(int tamanoPoblacion, int cantidadCiudades) {
        individuos = new ArrayList<>();
        for (int i = 0; i < tamanoPoblacion; i++) {
            Individuo ind = new Individuo(cantidadCiudades);
            individuos.add(ind);
        }
    }

    public ArrayList<Individuo> getIndividuos() {
        return individuos;
    }

    public void addIndividuo(Individuo ind) {
        individuos.add(ind);
    }

    public int size() {
        return individuos.size();
    }

    public Individuo getMejor() {
        Individuo mejor = individuos.get(0);

        for (Individuo ind : individuos) {
            if (ind.getFitness() > mejor.getFitness()) {
                mejor = ind;
            }
        }
        return mejor;
    }
}