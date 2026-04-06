import java.io.*;
import java.nio.charset.StandardCharsets;

public class ATSPReader {

    public static int[][] leerMatrizATSP(String nombreArchivo) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(nombreArchivo), StandardCharsets.UTF_8)
        );

        String linea;
        int dimension = 0;
        int[][] matriz = null;

        while ((linea = br.readLine()) != null) {

            if (linea.startsWith("DIMENSION")) {
                String[] partes = linea.split(":");
                dimension = Integer.parseInt(partes[1].trim());
                matriz = new int[dimension][dimension];
            }

            if (linea.startsWith("EDGE_WEIGHT_SECTION")) {
                for (int i = 0; i < dimension; i++) {
                    linea = br.readLine();

                    if (linea == null) {
                        throw new IOException("Archivo incompleto: faltan filas en EDGE_WEIGHT_SECTION.");
                    }

                    String[] valores = linea.trim().split("\\s+");

                    for (int j = 0; j < dimension; j++) {
                        matriz[i][j] = Integer.parseInt(valores[j]);
                    }
                }
            }
        }

        br.close();
        return matriz;
    }
}