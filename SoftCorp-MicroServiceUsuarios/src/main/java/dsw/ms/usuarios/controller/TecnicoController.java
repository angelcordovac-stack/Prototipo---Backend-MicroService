package dsw.ms.usuarios.controller;

import dsw.ms.usuarios.dto.TecnicoDTO;
import dsw.ms.usuarios.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService service;

    // Para que el jefe asigne tecnicos a incidencias
    @GetMapping("/disponibles")
    public List<TecnicoDTO> listarDisponibles() {
        return service.listarDisponibles();
    }

    @GetMapping
    public List<TecnicoDTO> listarTodos() {
        return service.listarTodosConNombre();
    }

    /**
     * Consultado via Feign por ms-incidencias para validar disponibilidad
     * y carga maxima de un tecnico antes de asignarle una incidencia.
     */
    @GetMapping("/{idUsuario}")
    public TecnicoDTO buscarPorId(@PathVariable Integer idUsuario) {
        return service.buscarPorId(idUsuario);
    }
}
