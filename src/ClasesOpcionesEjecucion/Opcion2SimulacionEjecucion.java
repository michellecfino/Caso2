package ClasesOpcionesEjecucion;

import java.util.*;

public class Opcion2SimulacionEjecucion {

    public static void simularEjecucion(int numProcesos, int totalMarcos, String politica, int n, int tp,
            String nombreArchivo) {
        List<Proceso> procesos = new ArrayList<>();
        int marcosPorProceso = totalMarcos / numProcesos;

        for (int i = 0; i < numProcesos; i++) {
            Proceso p = new Proceso(i, marcosPorProceso);

            p.leerArchivoConfiguracion("referencias_procs/" + nombreArchivo);
            for (int j = 0; j < marcosPorProceso; j++) {
                p.asignarMarco(i * marcosPorProceso + j);
            }
            procesos.add(p);
        }

        Map<Integer, Long> tiemposAcceso = new HashMap<>();

        simularColas(procesos, tiemposAcceso, totalMarcos, politica);

        for (Proceso p : procesos) {
            double tasaFallas = (double) p.getFallosPagina() / p.getTotalReferencias();

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
        if (ref == null || !ref.contains(","))
            return false;

        try {
            String[] partes = ref.split(",");
            int pagV = Integer.parseInt(partes[1].trim());

            if (p.getTablaPaginas().containsKey(pagV)) {
                int marco = p.getTablaPaginas().get(pagV);
                tiempos.put(marco, t);
                p.registrarHit();

                if (pol.equalsIgnoreCase("FIFOModified")) {
                    p.getMarcosAsignados().remove((Integer) marco);
                    p.getMarcosAsignados().add(marco);
                }
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
                } else if (pol.equalsIgnoreCase("FIFOModified")) {
                    marcoLibre = buscarFIFOModified(p, pagV);
                } else {
                    marcoLibre = buscarFIFO(p);
                }
                p.removerPaginaPorMarco(marcoLibre);
                p.registrarFallo(true);
            } else {
                p.registrarFallo(false);
                if (pol.startsWith("FIFO")) {
                    p.getMarcosAsignados().remove((Integer) marcoLibre);
                    p.getMarcosAsignados().add(marcoLibre);
                }
            }

            p.getTablaPaginas().put(pagV, marcoLibre);
            tiempos.put(marcoLibre, t);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // FIFO modificado: cuando una página es referenciada, se mueve al final de la
    // cola de reemplazo
    private static int buscarFIFOModified(Proceso p, int paginaNueva) {
        // Si la página ya está en memoria, mueve su marco al final (uso reciente)
        Integer marcoExistente = null;
        for (Map.Entry<Integer, Integer> entry : p.getTablaPaginas().entrySet()) {
            if (entry.getKey() == paginaNueva) {
                marcoExistente = entry.getValue();
                break;
            }
        }
        if (marcoExistente != null && p.getMarcosAsignados().contains(marcoExistente)) {
            p.getMarcosAsignados().remove(marcoExistente);
            p.getMarcosAsignados().add(marcoExistente);
            return marcoExistente;
        }
        // Si no está, aplica FIFO clásico
        if (!p.getMarcosAsignados().isEmpty()) {
            int marco = p.getMarcosAsignados().get(0);
            p.getMarcosAsignados().remove(0);
            p.getMarcosAsignados().add(marco);
            return marco;
        }
        // Fallback: si no hay marcos, retorna -1 (debería ser imposible)
        return -1;
    }

    // FIFO clásico: reemplaza el marco más antiguo
    private static int buscarFIFO(Proceso p) {
        int marco = p.getMarcosAsignados().get(0);
        p.getMarcosAsignados().remove(0);
        p.getMarcosAsignados().add(marco);
        return marco;
    }

    // LRU: reemplaza el marco menos recientemente usado
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