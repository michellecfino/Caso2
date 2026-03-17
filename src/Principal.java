import ClasesOpcionesEjecucion.Opcion1GeneradorReferencias;
import ClasesOpcionesEjecucion.Opcion2SimulacionEjecucion;

public class Principal {
    public static void main(String[] args) {
        int[] tamanosPagina = {64, 256, 1024};
        int[] tamanosMatriz = {8, 16, 128};
        int[] marcosTotales = {4, 8, 16};
        String[] politicas = {"FIFO", "FIFOModified", "LRU"};

        // Encabezado para copiar directo a Excel
        System.out.println("Matriz;Pagina;Marcos;Politica;Fallas;Hits;TasaFallas");

        for (int n : tamanosMatriz) {
            for (int tp : tamanosPagina) {
                // 1. Generar el archivo de referencias
                String nombreArchivo = "referencias_" + n + "x" + n + "_" + tp + ".txt";
                Opcion1GeneradorReferencias.generarReferencia(1, tp, n, n, n, n, nombreArchivo);
                
                // 2. Simular cada escenario con este archivo
                for (int m : marcosTotales) {
                    for (String pol : politicas) {
                        Opcion2SimulacionEjecucion.simularEjecucion(1, m, pol, n, tp, nombreArchivo);
                    }
                }
            }
        }
    }
}