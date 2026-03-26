import java.util.ArrayList;

public class EvaluadorFitness {
    private int[][] distancias;

    public EvaluadorFitness(int[][] distancias){
        this.distancias = distancias;
    }

    public void evaluarPoblacion(Poblacion poblacion){
        ArrayList<Individuo> individuos = poblacion.getIndividuos();
        int distancia;
        for(Individuo i: individuos){
            distancia = calcularDistancia(i.getPermutacion());
            i.setFitness(1.0/distancia);
            i.setCosto(distancia);
        }
    }

    public int calcularDistancia(ArrayList<Integer> permutacion){
        int dist = 0;
        for(int i=0; i<permutacion.size()-1;i++){
            dist = dist + distancias[permutacion.get(i)][permutacion.get(i+1)];
        }
        dist = dist + distancias[permutacion.get(permutacion.size()-1)][permutacion.get(0)];
        return dist;
    }
}
