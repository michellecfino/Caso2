import ClasesOpcionesEjecucion.Opcion1GeneradorReferencias;

public class Principal {
    public static void main(String[] args){
        Opcion1GeneradorReferencias.generarReferencia(1, 64, 128, 128, 128, 128);
        System.out.println("Archivo generado: proc1.txt listo.");
    }
}