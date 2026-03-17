import ClasesOpcionesEjecucion.Opcion2SimulacionEjecucion;

public class PrincipalEjecucion {
    public static void main(String[] args) {
        int[] tamanosPagina = {64, 256, 1024};
        int[] tamanosMatriz = {8, 16, 128};
        int[] marcos = {4, 8, 16};
        String[] politicas = {"FIFO", "FIFOModified", "LRU"};
        int numProcesos = 1;

        for (int tamMatriz : tamanosMatriz) {
            for (int tamPagina : tamanosPagina) {
                String nombreArchivo = "referencias_" + tamMatriz + "x" + tamMatriz + "_" + tamPagina + ".txt";
                for (int marco : marcos) {
                    for (String politica : politicas) {
                        System.out.println("Ejecutando: Matriz=" + tamMatriz + "x" + tamMatriz + ", Pagina=" + tamPagina + ", Marcos=" + marco + ", Politica=" + politica);
                        Opcion2SimulacionEjecucion.simularEjecucion(numProcesos, marco, politica, tamMatriz, tamPagina, nombreArchivo);
                    }
                }
            }
        }
    }
}
