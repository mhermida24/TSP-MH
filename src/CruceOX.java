import java.util.ArrayList;
import java.util.Random;

public class CruceOX implements EstrategiaCruce {
    private static final Random rand = new Random();

    public CruceOX() {
    }

    @Override
    public ArrayList<Individuo> cruzar(Individuo padre1, Individuo padre2, double probCruce, int cantCiudades){
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

    @Override
    public String getNombre() {
        return "OX";
    }
}
