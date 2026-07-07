package dsw.ms.identidad.controller;

import dsw.ms.identidad.model.Tecnico;
import dsw.ms.identidad.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService service;

    // LISTAR TECNICOS CON NOMBRE - para que el jefe asigne
    @GetMapping("/disponibles")
    public List<Map<String, Object>> listarDisponibles() {
        return service.listarTodosConNombre();
    }

    // OBTENER UN TECNICO POR ID
    // Lo consume el microservicio de Incidencias para validar disponibilidad/carga.
    @GetMapping("/{id}")
    public Tecnico obtener(@PathVariable Integer id) {
        return service.buscar(id);
    }
}
