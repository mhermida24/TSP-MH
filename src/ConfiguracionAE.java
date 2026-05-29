public class ConfiguracionAE {

    private int tamanoPoblacion;
    private double probCruce;
    private double probMutacion;
    private int numGeneraciones;

    public ConfiguracionAE(int tamanoPoblacion,
                           double probCruce,
                           double probMutacion,
                           int numGeneraciones) {

        this.tamanoPoblacion = tamanoPoblacion;
        this.probCruce = probCruce;
        this.probMutacion = probMutacion;
        this.numGeneraciones = numGeneraciones;
    }

    public int getTamanoPoblacion() {
        return tamanoPoblacion;
    }

    public double getProbCruce() {
        return probCruce;
    }

    public double getProbMutacion() {
        return probMutacion;
    }

    public int getNumGeneraciones() {
        return numGeneraciones;
    }
}