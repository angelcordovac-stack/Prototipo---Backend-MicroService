package dsw.ms.incidencias.controller;

import dsw.ms.incidencias.model.Repuesto;
import dsw.ms.incidencias.service.RepuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repuestos")
public class RepuestoController {

    @Autowired
    private RepuestoService service;

    @GetMapping
    public ResponseEntity<List<Repuesto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/solicitados")
    public ResponseEntity<List<Repuesto>> solicitados() {
        return ResponseEntity.ok(service.listarPorEstado("Solicitado"));
    }

    @GetMapping("/entregados")
    public ResponseEntity<List<Repuesto>> entregados() {
        return ResponseEntity.ok(service.listarPorEstado("Entregado"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('JEFE', 'TECNICO')")
    public ResponseEntity<Repuesto> solicitar(@RequestBody Repuesto repuesto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.solicitar(repuesto));
    }

    @GetMapping("/incidencia/{idIncidencia}")
    public ResponseEntity<List<Repuesto>> listarPorIncidencia(@PathVariable Integer idIncidencia) {
        return ResponseEntity.ok(service.listarPorIncidencia(idIncidencia));
    }

    @PutMapping("/{idRepuesto}/entregar")
    @PreAuthorize("hasAnyRole('JEFE', 'SISTEMAS')")
    public ResponseEntity<Repuesto> entregar(@PathVariable Integer idRepuesto) {
        return ResponseEntity.ok(service.entregar(idRepuesto));
    }
}
