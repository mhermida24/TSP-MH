import java.util.ArrayList;

public class AlgoritmoEvolutivo {

    private EstrategiaSeleccion estrategiaSeleccion;
    private EstrategiaCruce estrategiaCruce;
    private EstrategiaMutacion estrategiaMutacion;
    private EstrategiaReemplazo estrategiaReemplazo;

    private ConfiguracionAE configuracion;

    private EvaluadorFitness evaluadorFitness;

    private int cantCiudades;

    public AlgoritmoEvolutivo(
            EstrategiaSeleccion estrategiaSeleccion,
            EstrategiaCruce estrategiaCruce,
            EstrategiaMutacion estrategiaMutacion,
            EstrategiaReemplazo estrategiaReemplazo,
            EvaluadorFitness evaluadorFitness,
            ConfiguracionAE configuracion,
            int[][] distancias
    ) {

        this.estrategiaSeleccion = estrategiaSeleccion;
        this.estrategiaCruce = estrategiaCruce;
        this.estrategiaMutacion = estrategiaMutacion;
        this.estrategiaReemplazo = estrategiaReemplazo;

        this.evaluadorFitness = evaluadorFitness;

        this.configuracion = configuracion;

        this.cantCiudades = distancias.length;
    }

    public Individuo ejecutar() {

        int iteracion = 0;

        Poblacion poblacion = new Poblacion(
                configuracion.getTamanoPoblacion(),
                cantCiudades
        );

        while (iteracion < configuracion.getNumGeneraciones()) {

            evaluadorFitness.evaluarPoblacion(poblacion);

            Poblacion nuevaPoblacion =
                    estrategiaReemplazo.reemplazar(poblacion);

            while (nuevaPoblacion.size() <
                    configuracion.getTamanoPoblacion()) {

                Individuo padre1 =
                        estrategiaSeleccion.seleccionar(poblacion);

                Individuo padre2 =
                        estrategiaSeleccion.seleccionar(poblacion);

                ArrayList<Individuo> hijos =
                        estrategiaCruce.cruzar(
                                padre1,
                                padre2,
                                configuracion.getProbCruce(),
                                cantCiudades
                        );

                estrategiaMutacion.mutar(
                        hijos.get(0),
                        configuracion.getProbMutacion(),
                        cantCiudades
                );

                estrategiaMutacion.mutar(
                        hijos.get(1),
                        configuracion.getProbMutacion(),
                        cantCiudades
                );

                nuevaPoblacion.addIndividuo(hijos.get(0));

                if (nuevaPoblacion.size() <
                        configuracion.getTamanoPoblacion()) {

                    nuevaPoblacion.addIndividuo(hijos.get(1));
                }
            }

            poblacion = nuevaPoblacion;

            iteracion++;
        }

        evaluadorFitness.evaluarPoblacion(poblacion);

        return poblacion.getMejor();
    }
}