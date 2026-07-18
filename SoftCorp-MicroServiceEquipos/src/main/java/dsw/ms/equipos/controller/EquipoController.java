package dsw.ms.equipos.controller;

import dsw.ms.equipos.model.Equipo;
import dsw.ms.equipos.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoService service;

    @GetMapping
    public ResponseEntity<List<Equipo>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{codigoEquipo}")
    public ResponseEntity<Equipo> buscar(@PathVariable String codigoEquipo) {
        return ResponseEntity.ok(service.buscar(codigoEquipo));
    }

    /**
     * Consultado via Feign por ms-incidencias.
     */
    @GetMapping("/{codigoEquipo}/existe")
    public ResponseEntity<Boolean> existe(@PathVariable String codigoEquipo) {
        return ResponseEntity.ok(service.existe(codigoEquipo));
    }

    @PostMapping
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<Equipo> crear(@Valid @RequestBody Equipo equipo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(equipo));
    }

    @PutMapping("/{codigoEquipo}")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<Equipo> actualizar(@PathVariable String codigoEquipo,
                                              @Valid @RequestBody Equipo equipo) {
        return ResponseEntity.ok(service.actualizar(codigoEquipo, equipo));
    }

    @DeleteMapping("/{codigoEquipo}")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<String> eliminar(@PathVariable String codigoEquipo) {
        service.eliminar(codigoEquipo);
        return ResponseEntity.ok("Equipo eliminado correctamente");
    }
}
