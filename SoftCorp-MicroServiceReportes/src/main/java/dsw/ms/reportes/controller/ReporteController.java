package dsw.ms.reportes.controller;

import dsw.ms.reportes.dto.DashboardResponse;
import dsw.ms.reportes.dto.IncidenciaDTO;
import dsw.ms.reportes.dto.IncidenciaDetalleResponse;
import dsw.ms.reportes.dto.TecnicoRendimientoResponse;
import dsw.ms.reportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService service;

    /**
     * HU: "Como Jefe de area, quiero ver un panel con el estado general
     * de incidencias."
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(service.dashboard());
    }

    /**
     * HU: "Como Jefe de area, quiero ver cuantas incidencias resuelve
     * cada tecnico."
     */
    @GetMapping("/tecnicos/rendimiento")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<List<TecnicoRendimientoResponse>> rendimientoTecnicos() {
        return ResponseEntity.ok(service.rendimientoTecnicos());
    }

    /**
     * HU: "Como usuario del sistema, quiero filtrar incidencias por
     * estado, fecha y tecnico."
     */
    @GetMapping("/incidencias/filtrar")
    public ResponseEntity<List<IncidenciaDTO>> filtrarIncidencias(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer idTecnico) {
        return ResponseEntity.ok(service.filtrar(estado, desde, hasta, idTecnico));
    }

    /**
     * HU: "Como usuario del sistema, quiero ver el detalle completo de
     * una incidencia registrada."
     */
    @GetMapping("/incidencias/{id}/detalle")
    public ResponseEntity<IncidenciaDetalleResponse> detalleIncidencia(@PathVariable Integer id) {
        return ResponseEntity.ok(service.detalle(id));
    }

    /**
     * Historial de auditoria: cuando y quien genero cada dashboard o
     * reporte de rendimiento.
     */
    @GetMapping("/historial")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<List<dsw.ms.reportes.model.ReporteGenerado>> historial() {
        return ResponseEntity.ok(service.historial());
    }
}
