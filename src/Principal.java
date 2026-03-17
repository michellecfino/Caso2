import ClasesOpcionesEjecucion.Opcion1GeneradorReferencias;

public class Principal {
    public static void main(String[] args){
        int[] tamanosPagina = {64, 256, 1024};
        int[] tamanosMatriz = {8, 16, 128};

        for (int tamMatriz : tamanosMatriz) {
            for (int tamPagina : tamanosPagina) {
                String nombreArchivo = "referencias_" + tamMatriz + "x" + tamMatriz + "_" + tamPagina + ".txt";
                Opcion1GeneradorReferencias.generarReferencia(1, tamPagina, tamMatriz, tamMatriz, tamMatriz, tamMatriz, nombreArchivo);
                System.out.println("Archivo generado: " + nombreArchivo + " listo.");
            }
        }
    }
}