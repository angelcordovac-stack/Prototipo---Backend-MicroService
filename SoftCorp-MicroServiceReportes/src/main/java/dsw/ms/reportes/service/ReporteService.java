package dsw.ms.reportes.service;

import dsw.ms.reportes.client.EquipoClient;
import dsw.ms.reportes.client.IncidenciaClient;
import dsw.ms.reportes.client.TecnicoClient;
import dsw.ms.reportes.dto.*;
import dsw.ms.reportes.model.ReporteGenerado;
import dsw.ms.reportes.repository.ReporteGeneradoRepository;
import dsw.ms.reportes.security.JwtUtil;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private IncidenciaClient incidenciaClient;

    @Autowired
    private TecnicoClient tecnicoClient;

    @Autowired
    private EquipoClient equipoClient;

    @Autowired
    private ReporteGeneradoRepository reporteGeneradoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * HU: "Como Jefe de area, quiero ver un panel con el estado general
     * de incidencias." Se recalcula en cada llamada (los datos vienen de
     * ms-incidencias) y queda registrado en el historial propio.
     */
    public DashboardResponse dashboard() {
        List<IncidenciaDTO> todas = obtenerIncidencias();

        Map<String, Long> porEstado = todas.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getEstado() != null ? i.getEstado() : "Sin estado",
                        Collectors.counting()));

        Map<String, Long> porTecnico = todas.stream()
                .filter(i -> i.getIdTecnicoAsignado() != null)
                .collect(Collectors.groupingBy(
                        i -> String.valueOf(i.getIdTecnicoAsignado()),
                        Collectors.counting()));

        registrarGeneracion("DASHBOARD");

        return DashboardResponse.builder()
                .totalIncidencias(todas.size())
                .pendientes(porEstado.getOrDefault("Pendiente", 0L))
                .solucionadas(porEstado.getOrDefault("Solucionado", 0L))
                .porEstado(porEstado)
                .porTecnico(porTecnico)
                .build();
    }

    /**
     * HU: "Como Jefe de area, quiero ver cuantas incidencias resuelve
     * cada tecnico."
     */
    public List<TecnicoRendimientoResponse> rendimientoTecnicos() {
        List<IncidenciaDTO> todas = obtenerIncidencias();
        List<TecnicoDTO> tecnicos;
        try {
            tecnicos = tecnicoClient.listarTodos();
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo obtener la lista de tecnicos desde ms-usuarios: " + e.getMessage());
        }

        List<TecnicoRendimientoResponse> resultado = new ArrayList<>();
        for (TecnicoDTO tecnico : tecnicos) {
            List<IncidenciaDTO> asignadas = todas.stream()
                    .filter(i -> tecnico.getIdUsuario().equals(i.getIdTecnicoAsignado()))
                    .toList();

            long resueltas = asignadas.stream()
                    .filter(i -> "Solucionado".equalsIgnoreCase(i.getEstado()))
                    .count();
            long pendientes = asignadas.stream()
                    .filter(i -> "Pendiente".equalsIgnoreCase(i.getEstado()))
                    .count();

            resultado.add(TecnicoRendimientoResponse.builder()
                    .idTecnico(tecnico.getIdUsuario())
                    .nombreTecnico(tecnico.getNombre())
                    .totalAsignadas(asignadas.size())
                    .totalResueltas(resueltas)
                    .totalPendientes(pendientes)
                    .build());
        }

        registrarGeneracion("RENDIMIENTO_TECNICOS");
        return resultado;
    }

    /**
     * HU: "Como usuario del sistema, quiero filtrar incidencias por
     * estado, fecha y tecnico." Delega en ms-incidencias, que es el
     * dueno de los datos.
     */
    public List<IncidenciaDTO> filtrar(String estado, LocalDate desde, LocalDate hasta, Integer idTecnico) {
        try {
            return incidenciaClient.filtrar(estado, desde, hasta, idTecnico);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo filtrar incidencias en ms-incidencias: " + e.getMessage());
        }
    }

    /**
     * HU: "Como usuario del sistema, quiero ver el detalle completo de
     * una incidencia registrada." Agrega datos de ms-incidencias,
     * ms-equipos y ms-usuarios.
     */
    public IncidenciaDetalleResponse detalle(Integer idIncidencia) {
        IncidenciaDTO incidencia;
        try {
            incidencia = incidenciaClient.buscarPorId(idIncidencia);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Incidencia no encontrada con id: " + idIncidencia);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo obtener la incidencia desde ms-incidencias: " + e.getMessage());
        }

        EquipoDTO equipo = null;
        try {
            equipo = equipoClient.buscarPorCodigo(incidencia.getCodigoEquipo());
        } catch (FeignException ignored) {
            // Si ms-equipos no responde, se muestra el detalle igual sin el equipo enriquecido.
        }

        TecnicoDTO tecnico = null;
        if (incidencia.getIdTecnicoAsignado() != null) {
            try {
                tecnico = tecnicoClient.buscarPorId(incidencia.getIdTecnicoAsignado());
            } catch (FeignException ignored) {
                // Idem: se degrada sin romper el detalle completo.
            }
        }

        List<RepuestoDTO> repuestos = incidenciaClient.repuestosDeIncidencia(idIncidencia);
        List<InformeTecnicoDTO> informes = incidenciaClient.informesDeIncidencia(idIncidencia);

        return IncidenciaDetalleResponse.builder()
                .incidencia(incidencia)
                .equipo(equipo)
                .tecnicoAsignado(tecnico)
                .repuestos(repuestos)
                .informes(informes)
                .build();
    }

    /**
     * Historial propio de ms-reportes: cuando y quien genero cada
     * dashboard o reporte de rendimiento.
     */
    public List<ReporteGenerado> historial() {
        return reporteGeneradoRepository.findAllByOrderByFechaGeneracionDesc();
    }

    private List<IncidenciaDTO> obtenerIncidencias() {
        try {
            return incidenciaClient.listar();
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo obtener incidencias desde ms-incidencias: " + e.getMessage());
        }
    }

    private void registrarGeneracion(String tipoReporte) {
        ReporteGenerado registro = new ReporteGenerado();
        registro.setTipoReporte(tipoReporte);
        registro.setIdUsuarioSolicitante(idUsuarioActual());
        registro.setFechaGeneracion(LocalDateTime.now());
        reporteGeneradoRepository.save(registro);
    }

    /**
     * Extrae el idUsuario directamente del JWT de la request actual (no
     * hay tabla de usuarios en esta base de datos para resolverlo de otra
     * forma).
     */
    private Integer idUsuarioActual() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            return jwtUtil.extractIdUsuario(authHeader.substring(7));
        } catch (Exception e) {
            return null;
        }
    }
}
