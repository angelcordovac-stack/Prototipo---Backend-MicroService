package dsw.ms.identidad.service;

import dsw.ms.identidad.model.Tecnico;
import dsw.ms.identidad.model.Usuario;
import dsw.ms.identidad.repository.TecnicoRepository;
import dsw.ms.identidad.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TecnicoService {

    @Autowired
    private TecnicoRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    public List<Tecnico> listarDisponibles() {
        return repo.findByDisponibilidad(true);
    }

    /**
     * Devuelve un tecnico por su id. Usado por el microservicio de Incidencias
     * (via llamada REST con descubrimiento Eureka) para validar disponibilidad y carga.
     */
    public Tecnico buscar(Integer idUsuario) {
        return repo.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tecnico no encontrado con id: " + idUsuario));
    }

    public List<Map<String, Object>> listarTodosConNombre() {
        List<Tecnico> tecnicos = repo.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Tecnico t : tecnicos) {
            Usuario u = usuarioRepo.findById(t.getIdUsuario()).orElse(null);
            Map<String, Object> map = new HashMap<>();
            map.put("idUsuario", t.getIdUsuario());
            map.put("nombre", u != null ? u.getNombreCompleto() : "Desconocido");
            map.put("especialidad", t.getEspecialidad());
            map.put("disponibilidad", t.getDisponibilidad());
            map.put("maxIncidencias", t.getMaxIncidencias());
            resultado.add(map);
        }
        return resultado;
    }
}
