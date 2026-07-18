package dsw.ms.incidencias.controller;

import dsw.ms.incidencias.model.InformeTecnico;
import dsw.ms.incidencias.service.InformeTecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/informes")
public class InformeTecnicoController {

    @Autowired
    private InformeTecnicoService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('JEFE', 'TECNICO')")
    public ResponseEntity<InformeTecnico> registrar(@RequestBody InformeTecnico informe) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(informe));
    }

    @GetMapping("/incidencia/{idIncidencia}")
    public ResponseEntity<List<InformeTecnico>> listarPorIncidencia(@PathVariable Integer idIncidencia) {
        return ResponseEntity.ok(service.listarPorIncidencia(idIncidencia));
    }
}
