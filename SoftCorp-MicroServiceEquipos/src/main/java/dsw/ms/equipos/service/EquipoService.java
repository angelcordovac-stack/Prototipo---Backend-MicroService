package dsw.ms.equipos.service;

import dsw.ms.equipos.model.Equipo;
import dsw.ms.equipos.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository repo;

    public List<Equipo> listar() {
        return repo.findAll();
    }

    public Equipo buscar(String codigoEquipo) {
        return repo.findById(codigoEquipo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Equipo no encontrado con codigo: " + codigoEquipo));
    }

    /**
     * Consultado via Feign por ms-incidencias para validar que el equipo
     * exista antes de registrar una incidencia sobre el.
     */
    public boolean existe(String codigoEquipo) {
        return repo.existsById(codigoEquipo);
    }

    public Equipo guardar(Equipo equipo) {
        if (repo.existsById(equipo.getCodigoEquipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ya existe un equipo con el codigo " + equipo.getCodigoEquipo());
        }
        return repo.save(equipo);
    }

    public Equipo actualizar(String codigoEquipo, Equipo equipo) {
        buscar(codigoEquipo);
        equipo.setCodigoEquipo(codigoEquipo);
        return repo.save(equipo);
    }

    public void eliminar(String codigoEquipo) {
        if (!repo.existsById(codigoEquipo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe el equipo con codigo: " + codigoEquipo);
        }
        repo.deleteById(codigoEquipo);
    }
}
