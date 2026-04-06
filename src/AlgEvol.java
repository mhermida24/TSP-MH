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
    private EvaluadorFitness evaluadorFitness;
    private int cantCiudades;

    public AlgEvol(EstrategiaSeleccion estrategiaSeleccion, EstrategiaCruce estrategiaCruce, EstrategiaMutacion estrategiaMutacion,
                   EstrategiaReemplazo estrategiaReemplazo, EvaluadorFitness evaluadorFitness, int tamanoPoblacion, double probCruce,
                   double probMutacion, int numGeneraciones, int[][] distancias) {
        this.estrategiaSeleccion = estrategiaSeleccion;
        this.estrategiaCruce = estrategiaCruce;
        this.estrategiaMutacion = estrategiaMutacion;
        this.estrategiaReemplazo = estrategiaReemplazo;
        this.tamanoPoblacion = tamanoPoblacion;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.numGeneraciones = numGeneraciones;
        this.evaluadorFitness = evaluadorFitness;
        cantCiudades = distancias.length;
    }


    public Individuo ejecutar() {
        int it = 0;
        Poblacion poblacion = new Poblacion(tamanoPoblacion, cantCiudades);
        while(it < numGeneraciones){
            evaluadorFitness.evaluarPoblacion(poblacion);
            Poblacion nuevaPoblacion = estrategiaReemplazo.reemplazar(poblacion);
            while (nuevaPoblacion.size() < tamanoPoblacion){
                Individuo padre1 = estrategiaSeleccion.seleccionar(poblacion);
                Individuo padre2 = estrategiaSeleccion.seleccionar(poblacion);
                ArrayList<Individuo> hijos;
                hijos = estrategiaCruce.cruzar(padre1, padre2, probCruce, cantCiudades);
                estrategiaMutacion.mutar(hijos.get(0), probMutacion, cantCiudades);
                estrategiaMutacion.mutar(hijos.get(1), probMutacion, cantCiudades);
                nuevaPoblacion.addIndividuo(hijos.get(0));
                if(nuevaPoblacion.size() < tamanoPoblacion){
                    nuevaPoblacion.addIndividuo(hijos.get(1));
                }
            }
            poblacion = nuevaPoblacion;
            it++;
        }
        evaluadorFitness.evaluarPoblacion(poblacion);
        return poblacion.getMejor();
    }
}