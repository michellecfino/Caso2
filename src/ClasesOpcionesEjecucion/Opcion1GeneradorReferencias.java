package ClasesOpcionesEjecucion;

import java.io.*;

public class Opcion1GeneradorReferencias {
    public static void generarReferencia(int numProc, int tp, int nf1, int nc1, int nf2, int nc2,
            String nombreArchivo) {
        try {
            String path = "./referencias_procs/" + nombreArchivo;
            File dir = new File("./referencias_procs/");
            if (!dir.exists())
                dir.mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            int nf3 = nf1;
            int nc3 = nc2;
            int totalDatos = (nf1 * nc1) + (nf2 * nc2) + (nf3 * nc3);
            int numPaginas = (int) Math.ceil((double) (totalDatos * 4) / tp);
            int totalRefs = (nf1 * nc2 * nc1 * 2) + (nf1 * nc2);

            // Encabeszado :D
            bw.write("TP=" + tp);
            bw.newLine();
            bw.write("NF1=" + nf1);
            bw.newLine();
            bw.write("NC1=" + nc1);
            bw.newLine();
            bw.write("NF2=" + nf2);
            bw.newLine();
            bw.write("NC2=" + nc2);
            bw.newLine();
            bw.write("NR=" + totalRefs);
            bw.newLine();
            bw.write("NP=" + numPaginas);
            bw.newLine();

            int offM2 = (nf1 * nc1) * 4;
            int offM3 = ((nf1 * nc1) + (nf2 * nc2)) * 4;

            // --- Multiplicación de Matrices ---
            for (int i = 0; i < nf1; i++) {
                for (int j = 0; j < nc2; j++) {
                    for (int k = 0; k < nc1; k++) {

                        escribir(bw, "M1", i, k, (i * nc1 + k) * 4, tp);
                        escribir(bw, "M2", k, j, offM2 + (k * nc2 + j) * 4, tp);
                    }
                    escribir(bw, "M3", i, j, offM3 + (i * nc2 + j) * 4, tp);
                }

            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escribir(BufferedWriter bw, String m, int f, int c, int dv, int tp)
            throws IOException {
        int pagina = dv / tp;
        int desplazamiento = dv % tp;
        bw.write("[" + m + "-" + f + "-" + c + "]," + pagina + "," + desplazamiento);
        bw.newLine();
    }
}