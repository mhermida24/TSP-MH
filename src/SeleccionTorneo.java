import java.util.ArrayList;
import java.util.Random;

public class SeleccionTorneo implements EstrategiaSeleccion {
    private int tamanoTorneo = 3;
    private static final Random rand = new Random();

    public SeleccionTorneo() {
    }

    @Override
    public Individuo seleccionar(Poblacion poblacion) {
        Individuo padre = null;
        ArrayList<Individuo> individuos = poblacion.getIndividuos();
        for (int i = 0; i < 3; i++) {
            int indice = rand.nextInt(individuos.size());
            Individuo candidato = individuos.get(indice);
            if (padre == null || candidato.getFitness() > padre.getFitness()) {
                padre = candidato;
            }
        }
        return padre;
    }
}
