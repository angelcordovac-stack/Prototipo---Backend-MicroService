package dsw.ms.diccionariofallas.service;

import dsw.ms.diccionariofallas.client.UsuarioClient;
import dsw.ms.diccionariofallas.dto.UsuarioDTO;
import dsw.ms.diccionariofallas.model.DiccionarioFallas;
import dsw.ms.diccionariofallas.repository.DiccionarioFallasRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiccionarioFallasService {

    @Autowired
    private DiccionarioFallasRepository repo;

    @Autowired
    private UsuarioClient usuarioClient;

    public DiccionarioFallas registrar(DiccionarioFallas falla) {
        falla.setFechaRegistro(LocalDateTime.now());
        return enriquecer(repo.save(falla));
    }

    public List<DiccionarioFallas> listar() {
        return repo.findAll().stream().map(this::enriquecer).toList();
    }

    /**
     * Busqueda simple por palabra clave sobre el problema comun o la
     * solucion sugerida (case-insensitive).
     */
    public List<DiccionarioFallas> buscarPorPalabraClave(String keyword) {
        String k = keyword == null ? "" : keyword.toLowerCase();
        return repo.findAll().stream()
                .filter(f ->
                        (f.getProblemaComun() != null && f.getProblemaComun().toLowerCase().contains(k)) ||
                        (f.getSolucionSugerida() != null && f.getSolucionSugerida().toLowerCase().contains(k)))
                .map(this::enriquecer)
                .toList();
    }

    public DiccionarioFallas buscar(Integer id) {
        DiccionarioFallas falla = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entrada del diccionario no encontrada con id: " + id));
        return enriquecer(falla);
    }

    public DiccionarioFallas actualizar(Integer id, DiccionarioFallas falla) {
        buscar(id);
        falla.setIdFalla(id);
        return enriquecer(repo.save(falla));
    }

    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe la entrada con id: " + id);
        }
        repo.deleteById(id);
    }

    /**
     * Completa el nombre del autor consultando a ms-usuarios via Feign.
     * Si el servicio no responde, se degrada a "Desconocido" en vez de
     * romper toda la respuesta (mismo criterio que el monolito original).
     */
    private DiccionarioFallas enriquecer(DiccionarioFallas falla) {
        if (falla.getIdAutor() != null) {
            try {
                UsuarioDTO autor = usuarioClient.buscarPorId(falla.getIdAutor());
                falla.setNombreAutor(autor.getNombreCompleto());
            } catch (FeignException e) {
                falla.setNombreAutor("Desconocido");
            }
        }
        return falla;
    }
}
