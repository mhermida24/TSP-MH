public class ReemplazoGeneracional implements EstrategiaReemplazo{

    @Override
    public Poblacion reemplazar(Poblacion poblacionActual) {
        return new Poblacion();
    }

    @Override
    public String getNombre() {
        return "GENERACIONAL";
    }
}
