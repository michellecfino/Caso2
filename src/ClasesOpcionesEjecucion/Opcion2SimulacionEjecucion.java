package ClasesOpcionesEjecucion;

import java.util.*;

public class Opcion2SimulacionEjecucion {

    // Cambiado para recibir n y tp y así imprimirlos en la tabla final
    public static void simularEjecucion(int numProcesos, int totalMarcos, String politica, int n, int tp) {
        List<Proceso> procesos = new ArrayList<>();
        int marcosPorProceso = totalMarcos / numProcesos;

        for (int i = 0; i < numProcesos; i++) {
            Proceso p = new Proceso(i, marcosPorProceso);
            // El ID del proceso en el archivo suele ser 1
            p.leerArchivoConfiguracion("referencias_procs/proc" + (i + 1) + ".txt");
            for (int j = 0; j < marcosPorProceso; j++) {
                p.asignarMarco(i * marcosPorProceso + j);
            }
            procesos.add(p);
        }

        Map<Integer, Long> tiemposAcceso = new HashMap<>();

        simularColas(procesos, tiemposAcceso, totalMarcos, politica);

        // IMPRESIÓN FORMATO EXCEL
        for (Proceso p : procesos) {
            double tasaFallas = (double) p.getFallosPagina() / p.getTotalReferencias();
            // Formato: Matriz;Pagina;Marcos;Politica;Fallas;Hits;TasaFallas
            System.out.println(n + "x" + n + ";" + tp + ";" + totalMarcos + ";" + politica + ";" +
                    p.getFallosPagina() + ";" + p.getHits() + ";" + String.format("%.4f", tasaFallas));
        }
    }

    private static void simularColas(List<Proceso> procesos, Map<Integer, Long> tiempos, int totalMarcos,
            String politica) {
        Queue<Proceso> colaListos = new LinkedList<>(procesos);
        Queue<Proceso> colaDisco = new LinkedList<>();
        long globalTime = 0;

        while (!colaListos.isEmpty() || !colaDisco.isEmpty()) {
            Proceso p = !colaListos.isEmpty() ? colaListos.poll() : colaDisco.poll();
            if (p.terminado())
                continue;

            String ref = p.getReferencias().get(p.getIndiceActual());

            if (ref == null || !ref.contains(",")) {
                p.avanzarReferencia();
                colaListos.add(p);
            }

            boolean fallo = procesarPeticion(p, ref, tiempos, globalTime, politica);

            p.avanzarReferencia();
            if (fallo)
                colaDisco.add(p);
            else
                colaListos.add(p);

            globalTime++;
        }
    }

    private static boolean procesarPeticion(Proceso p, String ref, Map<Integer, Long> tiempos, long t, String pol) {
        // 1. Validar que la línea sea válida (debe tener una coma)
        if (ref == null || !ref.contains(",")) {
            return false; // Ignorar líneas inválidas o vacías
        }

        try {
            // 2. Intentar extraer la página virtual
            String[] partes = ref.split(",");
            int pagV = Integer.parseInt(partes[1].trim());

            // --- El resto del algoritmo sigue igual ---
            if (p.getTablaPaginas().containsKey(pagV)) {
                int marco = p.getTablaPaginas().get(pagV);
                tiempos.put(marco, t);
                p.registrarHit();
                return false;
            }

            int marcoLibre = -1;
            for (int m : p.getMarcosAsignados()) {
                if (!p.getTablaPaginas().containsValue(m)) {
                    marcoLibre = m;
                    break;
                }
            }

            if (marcoLibre == -1) {
                if (pol.equalsIgnoreCase("LRU")) {
                    marcoLibre = buscarLRU(p, tiempos);
                } else {
                    marcoLibre = buscarFIFO(p);
                }
                p.removerPaginaPorMarco(marcoLibre);
                p.registrarFallo(true);
            } else {
                p.registrarFallo(false);
            }

            p.getTablaPaginas().put(pagV, marcoLibre);
            tiempos.put(marcoLibre, t);
            p.incrementarAccesosSwap();
            return true;

        } catch (Exception e) {
            // Si algo sale mal parseando un número, simplemente lo ignoramos
            return false;
        }
    }

    private static int buscarFIFO(Proceso p) {
        int marco = p.getMarcosAsignados().get(0);
        p.getMarcosAsignados().remove(0);
        p.getMarcosAsignados().add(marco);
        return marco;
    }

    private static int buscarLRU(Proceso p, Map<Integer, Long> tiempos) {
        int marcoElegido = -1;
        long minTiempo = Long.MAX_VALUE;

        for (int marco : p.getMarcosAsignados()) {
            long t = tiempos.getOrDefault(marco, 0L);
            if (t < minTiempo) {
                minTiempo = t;
                marcoElegido = marco;
            }
        }
        return marcoElegido;
    }
}