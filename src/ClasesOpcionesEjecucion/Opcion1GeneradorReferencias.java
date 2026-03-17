package ClasesOpcionesEjecucion;

import java.io.*;

public class Opcion1GeneradorReferencias {
    public static void generarReferencia(int numProc, int tp, int nf1, int nc1, int nf2, int nc2) {
        try {
            String path = "./referencias_procs/proc" + numProc + ".txt";
            File dir = new File("./referencias_procs/");
            if (!dir.exists())
                dir.mkdirs();

            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            int nf3 = nf1;
            int nc3 = nc2;
            int totalDatos = (nf1 * nc1) + (nf2 * nc2) + (nf3 * nc3);
            int numPaginas = (int) Math.ceil((double) (totalDatos * 4) / tp);
            int totalRefs = (nf1 * nc2 * nc1 * 2) + (nf1 * nc2);

            // Encabezado según enunciado
            bw.write("TP=" + tp + "\nNF1=" + nf1 + "\nNC1=" + nc1 + "\nNF2=" + nf2 + "\nNC2=" + nc2);
            bw.newLine();
            bw.write("NR=" + totalRefs + "\nNP=" + numPaginas);
            bw.newLine();

            int offM2 = (nf1 * nc1) * 4;
            int offM3 = ((nf1 * nc1) + (nf2 * nc2)) * 4;

            // --- Multiplicación de Matrices ---
            for (int i = 0; i < nf1; i++) {
                for (int j = 0; j < nc2; j++) {
                    for (int k = 0; k < nc1; k++) {
                        escribir(bw, "M1", i, k, (i * nc1 + k) * 4, tp, "r");
                        escribir(bw, "M2", k, j, offM2 + (k * nc2 + j) * 4, tp, "r");
                    }
                    escribir(bw, "M3", i, j, offM3 + (i * nc2 + j) * 4, tp, "w");
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escribir(BufferedWriter bw, String m, int f, int c, int dv, int tp, String acc)
            throws IOException {
        bw.write(m + ":[" + f + "-" + c + "]," + (dv / tp) + "," + (dv % tp) + "," + acc);
        bw.newLine();
    }
}