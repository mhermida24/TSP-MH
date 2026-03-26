import java.util.ArrayList;
import java.util.Random;

public class SeleccionRuleta {
    private static final Random rand = new Random();

    public SeleccionRuleta(){

    }

    public Individuo seleccionar(Poblacion poblacion){
        Individuo padre = null;
        ArrayList<Individuo> individuos = poblacion.getIndividuos();
        double totalFitness = 0;
        for(Individuo i: individuos){
            totalFitness += i.getFitness();
        }
        double valor = rand.nextDouble() * totalFitness;
        double acumulado = 0;
        for(Individuo i: individuos){
            acumulado += i.getFitness();
            if(acumulado >= valor){
                padre = i;
                break;
            }
        }
        if (padre == null) {
            padre = individuos.get(individuos.size() - 1);
        }
        return padre;
    }
}
