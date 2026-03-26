import java.util.ArrayList;

public interface EstrategiaCruce {
    ArrayList<Individuo> cruzar(Individuo padre1, Individuo padre2, double probCruce, int cantCiudades);
}
