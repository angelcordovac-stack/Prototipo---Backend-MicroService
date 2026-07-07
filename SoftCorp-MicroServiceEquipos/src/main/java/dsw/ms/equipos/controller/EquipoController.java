package dsw.ms.equipos.controller;

import dsw.ms.equipos.model.Equipo;
import dsw.ms.equipos.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Se conserva EXACTAMENTE el contrato del monolito: GET /api/equipos.
 * El monolito no tenia capa de servicio para Equipos (acceso directo al
 * repositorio), asi que se replica el mismo comportamiento. La ruta y el
 * verbo se mantienen intactos para que el frontend no requiera cambios.
 */
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoRepository repository;

    @GetMapping
    public List<Equipo> getAll() {
        return repository.findAll();
    }
}
