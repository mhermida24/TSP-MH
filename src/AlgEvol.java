import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AlgEvol {

    private int tamanoPoblacion;
    private double probCruce;
    private double probMutacion;
    private int numGeneraciones;
    private int[][] distancias;
    private Poblacion poblacion;
    private int cantCiudades;
    private Random rand;

    public AlgEvol(int tamanoPoblacion, double probCruce, double probMutacion, int numGeneraciones, int[][] distancias){
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
            nuevaPoblacion.addIndividuo(new Individuo(poblacion.getMejor()));
            while (nuevaPoblacion.size() < tamanoPoblacion){
                Individuo padre1 = selecccionarPadre();
                Individuo padre2 = selecccionarPadre();
                ArrayList<Individuo> hijos= cruzar(padre1, padre2);
                mutar(hijos.get(0));
                mutar(hijos.get(1));
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
        }
    }

    private Integer calcularDistancia(ArrayList<Integer> permutacion){
        int dist = 0;
        for(int i=0; i<permutacion.size()-1;i++){
            dist = dist + distancias[permutacion.get(i)][permutacion.get(i+1)];
        }
        dist = dist + distancias[permutacion.get(permutacion.size()-1)][permutacion.get(0)];
        return dist;
    }

    private Individuo selecccionarPadre(){
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

    private ArrayList<Individuo> cruzar(Individuo padre1, Individuo padre2){
        ArrayList<Individuo> hijos = new ArrayList<>();
        Individuo hijo1;
        Individuo hijo2;
        double prob = rand.nextDouble();
        if(prob<probCruce){
            int c1 = rand.nextInt(cantCiudades);
            int c2 = rand.nextInt(cantCiudades);
            while (c1 == c2){
                c2 = rand.nextInt(cantCiudades);
            }
            int corte1 = Math.min(c1,c2);
            int corte2 = Math.max(c1,c2);
            ArrayList<Integer> permutacionHijo1 = new ArrayList<>();
            ArrayList<Integer> permutacionHijo2 = new ArrayList<>();
            for(int i = 0; i < cantCiudades; i++){
                permutacionHijo1.add(null);
                permutacionHijo2.add(null);
            }
            int indice = corte1;
            while (indice <= corte2){
                permutacionHijo1.set(indice, padre1.getPermutacion().get(indice));
                permutacionHijo2.set(indice, padre2.getPermutacion().get(indice));
                indice++;
            }
            int pos = (corte2 + 1) % cantCiudades;
            for(Integer ciudad : padre2.getPermutacion()){
                if(!permutacionHijo1.contains(ciudad)){
                    permutacionHijo1.set(pos, ciudad);
                    pos = (pos + 1) % cantCiudades;
                }
            }
            pos = (corte2 + 1) % cantCiudades;
            for(Integer ciudad : padre1.getPermutacion()){
                if(!permutacionHijo2.contains(ciudad)){
                    permutacionHijo2.set(pos, ciudad);
                    pos = (pos + 1) % cantCiudades;
                }
            }
            hijo1 = new Individuo(permutacionHijo1);
            hijo2 = new Individuo(permutacionHijo2);
        }else{
            hijo1 = new Individuo(padre1);
            hijo2 = new Individuo(padre2);
        }
        hijos.add(hijo1);
        hijos.add(hijo2);
        return hijos;
    }

    private void mutar(Individuo ind) {
        double prob = rand.nextDouble();
        if (prob < probMutacion) {
            int p1 = rand.nextInt(cantCiudades);
            int p2 = rand.nextInt(cantCiudades);
            while (p1 == p2){
                p2 = rand.nextInt(cantCiudades);
            }
            ArrayList<Integer> permutacion = ind.getPermutacion();
            int ciudad1 = permutacion.get(p1);
            int ciudad2 = permutacion.get(p2);
            permutacion.set(p1, ciudad2);
            permutacion.set(p2, ciudad1);
        }
    }

}
