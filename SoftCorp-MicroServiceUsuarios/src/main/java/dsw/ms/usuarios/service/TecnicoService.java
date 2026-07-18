package dsw.ms.usuarios.service;

import dsw.ms.usuarios.dto.TecnicoDTO;
import dsw.ms.usuarios.model.Tecnico;
import dsw.ms.usuarios.model.Usuario;
import dsw.ms.usuarios.repository.TecnicoRepository;
import dsw.ms.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TecnicoService {

    @Autowired
    private TecnicoRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    public List<TecnicoDTO> listarDisponibles() {
        return repo.findByDisponibilidad(true).stream()
                .map(this::enriquecer)
                .toList();
    }

    public List<TecnicoDTO> listarTodosConNombre() {
        return repo.findAll().stream()
                .map(this::enriquecer)
                .toList();
    }

    /**
     * Consultado via Feign por ms-incidencias para validar que el tecnico
     * exista y este disponible antes de asignarle una incidencia.
     */
    public TecnicoDTO buscarPorId(Integer idUsuario) {
        Tecnico t = repo.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tecnico no encontrado con id: " + idUsuario));
        return enriquecer(t);
    }

    private TecnicoDTO enriquecer(Tecnico t) {
        Usuario u = usuarioRepo.findById(t.getIdUsuario()).orElse(null);
        return TecnicoDTO.builder()
                .idUsuario(t.getIdUsuario())
                .nombre(u != null ? u.getNombreCompleto() : "Desconocido")
                .especialidad(t.getEspecialidad())
                .maxIncidencias(t.getMaxIncidencias())
                .disponibilidad(t.getDisponibilidad())
                .build();
    }
}
