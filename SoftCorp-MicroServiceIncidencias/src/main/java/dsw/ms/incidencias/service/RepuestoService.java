package dsw.ms.incidencias.service;

import dsw.ms.incidencias.model.Repuesto;
import dsw.ms.incidencias.repository.IncidenciaRepository;
import dsw.ms.incidencias.repository.RepuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepuestoService {

    @Autowired
    private RepuestoRepository repo;

    @Autowired
    private IncidenciaRepository incidenciaRepo;

    public Repuesto solicitar(Repuesto repuesto) {
        incidenciaRepo.findById(repuesto.getIdIncidencia())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Incidencia no encontrada con id: " + repuesto.getIdIncidencia()));

        repuesto.setFechaSolicitud(LocalDateTime.now());
        repuesto.setEstado("Solicitado");
        return repo.save(repuesto);
    }

    public List<Repuesto> listarPorIncidencia(Integer idIncidencia) {
        return repo.findByIdIncidencia(idIncidencia);
    }

    public List<Repuesto> listarTodos() {
        return repo.findAll();
    }

    public List<Repuesto> listarPorEstado(String estado) {
        return repo.findAll().stream()
                .filter(r -> estado.equalsIgnoreCase(r.getEstado()))
                .toList();
    }

    public Repuesto entregar(Integer idRepuesto) {
        Repuesto repuesto = repo.findById(idRepuesto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Repuesto no encontrado con id: " + idRepuesto));
        repuesto.setEstado("Entregado");
        repuesto.setFechaEntrega(LocalDateTime.now());
        return repo.save(repuesto);
    }
}
