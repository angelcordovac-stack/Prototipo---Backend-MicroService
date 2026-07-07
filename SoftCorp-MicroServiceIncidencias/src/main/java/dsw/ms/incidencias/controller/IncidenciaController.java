package dsw.ms.incidencias.controller;

import dsw.ms.incidencias.model.Incidencia;
import dsw.ms.incidencias.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    @Autowired
    private IncidenciaService service;

    @PostMapping
    public Incidencia registrar(@Valid @RequestBody Incidencia incidencia) {
        return service.registrar(incidencia);
    }

    @GetMapping
    public List<Incidencia> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Incidencia buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @GetMapping("/tecnico/{idTecnico}")
    public List<Incidencia> tareasDeTecnico(@PathVariable Integer idTecnico) {
        return service.tareasDeTecnico(idTecnico);
    }

    @GetMapping("/equipo/{codigoEquipo}")
    public List<Incidencia> historialEquipo(@PathVariable String codigoEquipo) {
        return service.historialEquipo(codigoEquipo);
    }

    // ASIGNAR TECNICO - solo JEFE. Se reenvia el header Authorization al MS de Identidad.
    @PreAuthorize("hasRole('JEFE')")
    @PutMapping("/{id}/asignar")
    public Incidencia asignarTecnico(@PathVariable Integer id,
                                     @RequestBody Map<String, Integer> body,
                                     @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        Integer idTecnico = body.get("idTecnico");
        return service.asignarTecnico(id, idTecnico, auth);
    }

    // SOLUCIONAR INCIDENCIA - solo TECNICO o JEFE
    @PreAuthorize("hasAnyRole('TECNICO','JEFE')")
    @PutMapping("/{id}/solucionar")
    public Incidencia solucionar(@PathVariable Integer id,
                                 @RequestBody Map<String, String> body) {
        String tipoSolucion = body != null ? body.get("tipoSolucion") : null;
        return service.solucionar(id, tipoSolucion);
    }

    @GetMapping("/pendientes")
    public List<Incidencia> pendientes() {
        return service.pendientes();
    }

    @GetMapping("/solucionadas")
    public List<Incidencia> solucionadas() {
        return service.solucionadas();
    }
}
