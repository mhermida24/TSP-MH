public class ReemplazoElitismo implements EstrategiaReemplazo{

    @Override
    public Poblacion reemplazar(Poblacion poblacionActual) {
        Poblacion poblacionReemplazada = new Poblacion();
        poblacionReemplazada.addIndividuo(new Individuo(poblacionActual.getMejor()));
        return poblacionReemplazada;
    }
}
