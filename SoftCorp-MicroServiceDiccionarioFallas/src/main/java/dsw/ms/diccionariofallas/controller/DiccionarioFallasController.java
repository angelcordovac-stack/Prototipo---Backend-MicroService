package dsw.ms.diccionariofallas.controller;

import dsw.ms.diccionariofallas.model.DiccionarioFallas;
import dsw.ms.diccionariofallas.service.DiccionarioFallasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diccionario-fallas")
public class DiccionarioFallasController {

    @Autowired
    private DiccionarioFallasService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECNICO', 'SISTEMAS')")
    public ResponseEntity<DiccionarioFallas> registrar(@RequestBody DiccionarioFallas falla) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(falla));
    }

    @GetMapping
    public ResponseEntity<List<DiccionarioFallas>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<DiccionarioFallas>> buscarPorPalabraClave(@RequestParam String keyword) {
        return ResponseEntity.ok(service.buscarPorPalabraClave(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiccionarioFallas> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TECNICO', 'SISTEMAS')")
    public ResponseEntity<DiccionarioFallas> actualizar(@PathVariable Integer id,
                                                         @RequestBody DiccionarioFallas falla) {
        return ResponseEntity.ok(service.actualizar(id, falla));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok("Entrada eliminada correctamente");
    }
}
