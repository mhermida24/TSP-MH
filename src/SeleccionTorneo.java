import java.util.ArrayList;
import java.util.Random;

public class SeleccionTorneo implements EstrategiaSeleccion {
    private int tamanoTorneo;
    private static final Random rand = new Random();

    public SeleccionTorneo(int tamano) {
        this.tamanoTorneo = tamano;
    }

    @Override
    public Individuo seleccionar(Poblacion poblacion) {
        Individuo padre = null;
        ArrayList<Individuo> individuos = poblacion.getIndividuos();
        for (int i = 0; i < tamanoTorneo; i++) {
            int indice = rand.nextInt(individuos.size());
            Individuo candidato = individuos.get(indice);
            if (padre == null || candidato.getFitness() > padre.getFitness()) {
                padre = candidato;
            }
        }
        return padre;
    }

    @Override
    public String getNombre() {
        return "TORNEO";
    }
}
