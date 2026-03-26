import java.util.ArrayList;

public class AlgEvol {

    private EstrategiaSeleccion estrategiaSeleccion;
    private EstrategiaCruce estrategiaCruce;
    private EstrategiaMutacion estrategiaMutacion;
    private EstrategiaReemplazo estrategiaReemplazo;
    private int tamanoPoblacion;
    private double probCruce;
    private double probMutacion;
    private int numGeneraciones;
    private int[][] distancias;
    private Poblacion poblacion;
    private int cantCiudades;

    public AlgEvol(EstrategiaSeleccion estrategiaSeleccion, EstrategiaCruce estrategiaCruce, EstrategiaMutacion estrategiaMutacion, EstrategiaReemplazo estrategiaReemplazo, int tamanoPoblacion, double probCruce, double probMutacion, int numGeneraciones, int[][] distancias){
        this.estrategiaSeleccion = estrategiaSeleccion;
        this.estrategiaCruce = estrategiaCruce;
        this.estrategiaMutacion = estrategiaMutacion;
        this.estrategiaReemplazo= estrategiaReemplazo;
        this.tamanoPoblacion = tamanoPoblacion;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.numGeneraciones = numGeneraciones;
        this.distancias = distancias;
        cantCiudades = distancias.length;
    }


    public Individuo ejecutar(){
        int it = 0;
        poblacion = new Poblacion(tamanoPoblacion, distancias.length);
        while(it < numGeneraciones){
            evaluarPoblacion();
            Poblacion nuevaPoblacion = estrategiaReemplazo.reemplazar(poblacion);
            while (nuevaPoblacion.size() < tamanoPoblacion){
                Individuo padre1 = estrategiaSeleccion.seleccionar(poblacion);
                Individuo padre2 = estrategiaSeleccion.seleccionar(poblacion);
                ArrayList<Individuo> hijos;
                hijos = estrategiaCruce.cruzar(padre1, padre2,probCruce, cantCiudades);
                estrategiaMutacion.mutar(hijos.get(0),probMutacion, cantCiudades);
                estrategiaMutacion.mutar(hijos.get(1),probMutacion, cantCiudades);
                nuevaPoblacion.addIndividuo(hijos.get(0));
                if(nuevaPoblacion.size() < tamanoPoblacion){
                    nuevaPoblacion.addIndividuo(hijos.get(1));
                }
            }
            poblacion = nuevaPoblacion;
            it++;
        }
        evaluarPoblacion();
        return poblacion.getMejor();
    }

    private void evaluarPoblacion(){
        ArrayList<Individuo> individuos = poblacion.getIndividuos();
        int distancia;
        for(Individuo i: individuos){
            distancia = calcularDistancia(i.getPermutacion());
            i.setFitness(1.0/distancia);
            i.setCosto(distancia);
        }
    }

    private int calcularDistancia(ArrayList<Integer> permutacion){
        int dist = 0;
        for(int i=0; i<permutacion.size()-1;i++){
            dist = dist + distancias[permutacion.get(i)][permutacion.get(i+1)];
        }
        dist = dist + distancias[permutacion.get(permutacion.size()-1)][permutacion.get(0)];
        return dist;
    }
}