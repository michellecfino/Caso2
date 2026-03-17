import ClasesOpcionesEjecucion.Opcion1GeneradorReferencias;
import ClasesOpcionesEjecucion.Opcion2SimulacionEjecucion;

class SimuladorUsoMemoria {
    public static void main(String[] args) {
        int[] tamaniosMatriz = { 8, 16, 128 };
        int[] tamaniosPagina = { 64, 256, 1024 };
        int[] numMarcos = { 4, 8, 16 };
        String[] politicas = { "LRU", "FIFO", "FIFOModified" };

        System.out.println("----------------------------------");
        System.out.println("Matriz;TamanioPagina;Marcos;Politica;Fallas;Hits;TasaFallas");

        for (int n : tamaniosMatriz) {
            for (int tp : tamaniosPagina) {

                String nombreArchivo = "referencias_" + n + "x" + n + "_" + tp + ".txt";
                Opcion1GeneradorReferencias.generarReferencia(1, tp, n, n, n, n, nombreArchivo);

                for (int m : numMarcos) {
                    for (String pol : politicas) {

                        try {
                            Opcion2SimulacionEjecucion.simularEjecucion(1, m, pol, n, tp, nombreArchivo);
                        } catch (Exception e) {
                            // Esto nos dirá qué está fallando realmente (la línea y el tipo de error)
                            System.err.println("Error en caso: M=" + n + ", TP=" + tp + ", Pol=" + pol);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        System.out.println("----------------------------------");
        System.out.println("PROCESO TERMINADO. Copia los datos de arriba al Excel.");
    }
}