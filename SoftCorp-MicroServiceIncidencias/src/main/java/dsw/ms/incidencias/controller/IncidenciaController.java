package dsw.ms.incidencias.controller;

import dsw.ms.incidencias.model.Incidencia;
import dsw.ms.incidencias.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    @Autowired
    private IncidenciaService service;

    @PostMapping
    public ResponseEntity<Incidencia> registrar(@Valid @RequestBody Incidencia incidencia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(incidencia));
    }

    @GetMapping
    public ResponseEntity<List<Incidencia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/tecnico/{idTecnico}")
    public ResponseEntity<List<Incidencia>> tareasDeTecnico(@PathVariable Integer idTecnico) {
        return ResponseEntity.ok(service.tareasDeTecnico(idTecnico));
    }

    @GetMapping("/equipo/{codigoEquipo}")
    public ResponseEntity<List<Incidencia>> historialEquipo(@PathVariable String codigoEquipo) {
        return ResponseEntity.ok(service.historialEquipo(codigoEquipo));
    }

    @PutMapping("/{id}/asignar")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<Incidencia> asignarTecnico(@PathVariable Integer id,
                                                      @RequestBody Map<String, Integer> body) {
        Integer idTecnico = body.get("idTecnico");
        return ResponseEntity.ok(service.asignarTecnico(id, idTecnico));
    }

    @PutMapping("/{id}/solucionar")
    @PreAuthorize("hasAnyRole('JEFE', 'TECNICO')")
    public ResponseEntity<Incidencia> solucionar(@PathVariable Integer id,
                                                  @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.solucionar(id, body.get("tipoSolucion")));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<Incidencia>> pendientes() {
        return ResponseEntity.ok(service.pendientes());
    }

    @GetMapping("/solucionadas")
    public ResponseEntity<List<Incidencia>> solucionadas() {
        return ResponseEntity.ok(service.solucionadas());
    }

    /**
     * Filtrado avanzado combinando estado, rango de fechas y tecnico
     * asignado. Todos los parametros son opcionales.
     * Consumido directamente y tambien via Feign por ms-reportes.
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<Incidencia>> filtrar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer idTecnico) {
        return ResponseEntity.ok(service.filtrar(estado, desde, hasta, idTecnico));
    }
}
