import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AlgEvol {

    private EstrategiaSeleccion estrategiaSeleccion;
    private EstrategiaCruce estrategiaCruce;
    private EstrategiaMutacion estrategiaMutacion;
    private TipoReemplazo tipoReemplazo;
    private int tamanoPoblacion;
    private double probCruce;
    private double probMutacion;
    private int numGeneraciones;
    private int[][] distancias;
    private Poblacion poblacion;
    private int cantCiudades;
    private Random rand;

    public AlgEvol(EstrategiaSeleccion estrategiaSeleccion, EstrategiaCruce estrategiaCruce, EstrategiaMutacion estrategiaMutacion, TipoReemplazo tipoReemplazo, int tamanoPoblacion, double probCruce, double probMutacion, int numGeneraciones, int[][] distancias){
        this.estrategiaSeleccion = estrategiaSeleccion;
        this.estrategiaCruce = estrategiaCruce;
        this.estrategiaMutacion = estrategiaMutacion;
        this.tipoReemplazo = tipoReemplazo;
        this.tamanoPoblacion = tamanoPoblacion;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.numGeneraciones = numGeneraciones;
        this.distancias = distancias;
        cantCiudades = distancias.length;
        this.rand = new Random();
    }


    public Individuo ejecutar(){
        int it = 0;
        poblacion = new Poblacion(tamanoPoblacion, distancias.length);
        while(it < numGeneraciones){
            Poblacion nuevaPoblacion = new Poblacion();
            evaluarPoblacion();
            if (TipoReemplazo.ELITISMO.equals(tipoReemplazo)) {
                nuevaPoblacion.addIndividuo(new Individuo(poblacion.getMejor()));
            }
            while (nuevaPoblacion.size() < tamanoPoblacion){
                Individuo padre1 = estrategiaSeleccion.seleccionar(poblacion);
                Individuo padre2 = estrategiaSeleccion.seleccionar(poblacion);
                ArrayList<Individuo> hijos;
                hijos = estrategiaCruce.cruzar(padre1, padre2,probCruce, cantCiudades);
                estrategiaMutacion.mutar(hijos.get(0),probMutacion);
                estrategiaMutacion.mutar(hijos.get(1),probMutacion);
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