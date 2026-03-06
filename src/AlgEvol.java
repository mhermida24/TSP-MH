import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AlgEvol {

    private TipoSeleccion tipoSeleccion;
    private TipoCruce tipoCruce;
    private TipoMutacion tipoMutacion;
    private TipoReemplazo tipoReemplazo;
    private int tamanoPoblacion;
    private double probCruce;
    private double probMutacion;
    private int numGeneraciones;
    private int[][] distancias;
    private Poblacion poblacion;
    private int cantCiudades;
    private Random rand;

    public AlgEvol(TipoSeleccion tipoSeleccion, TipoCruce tipoCruce, TipoMutacion tipoMutacion, TipoReemplazo tipoReemplazo, int tamanoPoblacion, double probCruce, double probMutacion, int numGeneraciones, int[][] distancias){
        this.tipoSeleccion = tipoSeleccion;
        this.tipoCruce = tipoCruce;
        this.tipoMutacion = tipoMutacion;
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
                Individuo padre1 = null;
                Individuo padre2 = null;
                if(tipoSeleccion.TORNEO.equals(tipoSeleccion)) {
                    padre1 = selecccionarPadreTorneo();
                    padre2 = selecccionarPadreTorneo();
                }else if(tipoSeleccion.RULETA.equals(tipoSeleccion)){
                    padre1 = seleccionarPadreRuleta();
                    padre2 = seleccionarPadreRuleta();
                }
                ArrayList<Individuo> hijos = null;
                if(TipoCruce.OX.equals(tipoCruce)){
                    hijos= cruzarOX(padre1, padre2);
                }else if(TipoCruce.PMX.equals(tipoCruce)) {
                    hijos = cruzarPMX(padre1, padre2);
                }
                if(tipoMutacion.SWAP.equals(tipoMutacion)){
                    mutarSwap(hijos.get(0));
                    mutarSwap(hijos.get(1));
                }else if(tipoMutacion.INVERSION.equals(tipoMutacion)) {
                    mutarInversion(hijos.get(0));
                    mutarInversion(hijos.get(1));
                }
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

    private Individuo selecccionarPadreTorneo(){
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

    private Individuo seleccionarPadreRuleta(){
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
        return padre;
    }

    private ArrayList<Individuo> cruzarOX(Individuo padre1, Individuo padre2){
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

    private ArrayList<Individuo> cruzarPMX(Individuo padre1, Individuo padre2) {
        ArrayList<Individuo> hijos = new ArrayList<>();
        Individuo hijo1;
        Individuo hijo2;
        if (rand.nextDouble() < probCruce) {
            int c1 = rand.nextInt(cantCiudades);
            int c2 = rand.nextInt(cantCiudades);
            while (c1 == c2) {
                c2 = rand.nextInt(cantCiudades);
            }
            int corte1 = Math.min(c1, c2);
            int corte2 = Math.max(c1, c2);
            ArrayList<Integer> h1 = new ArrayList<>();
            ArrayList<Integer> h2 = new ArrayList<>();
            for (int i = 0; i < cantCiudades; i++) {
                h1.add(null);
                h2.add(null);
            }
            for (int i = corte1; i <= corte2; i++) {
                h1.set(i, padre2.getPermutacion().get(i));
                h2.set(i, padre1.getPermutacion().get(i));
            }
            for (int i = 0; i < cantCiudades; i++) {
                if (i >= corte1 && i <= corte2)
                    continue;
                int ciudad = padre1.getPermutacion().get(i);
                while (h1.contains(ciudad)) {
                    int index = padre2.getPermutacion().indexOf(ciudad);
                    ciudad = padre1.getPermutacion().get(index);
                }
                h1.set(i, ciudad);
            }
            for (int i = 0; i < cantCiudades; i++) {
                if (i >= corte1 && i <= corte2)
                    continue;
                int ciudad = padre2.getPermutacion().get(i);
                while (h2.contains(ciudad)) {
                    int index = padre1.getPermutacion().indexOf(ciudad);
                    ciudad = padre2.getPermutacion().get(index);
                }
                h2.set(i, ciudad);
            }
            hijo1 = new Individuo(h1);
            hijo2 = new Individuo(h2);
        } else {
            hijo1 = new Individuo(padre1);
            hijo2 = new Individuo(padre2);
        }
        hijos.add(hijo1);
        hijos.add(hijo2);
        return hijos;
    }

    private void mutarSwap(Individuo ind) {
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

    private void mutarInversion(Individuo ind) {
        double prob = rand.nextDouble();
        if (prob < probMutacion) {
            int p1 = rand.nextInt(cantCiudades);
            int p2 = rand.nextInt(cantCiudades);
            while (p1 == p2){
                p2 = rand.nextInt(cantCiudades);
            }
            int inicio = min(p1, p2);
            int fin = max(p1, p2);
            ArrayList<Integer> permutacion = ind.getPermutacion();
            while (inicio < fin) {
                int ciudadInicio = permutacion.get(inicio);
                int ciudadFin = permutacion.get(fin);
                permutacion.set(inicio, ciudadFin);
                permutacion.set(fin, ciudadInicio);
                inicio++;
                fin--;
            }
        }
    }
}
