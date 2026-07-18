package dsw.ms.incidencias.service;

import dsw.ms.incidencias.model.InformeTecnico;
import dsw.ms.incidencias.repository.IncidenciaRepository;
import dsw.ms.incidencias.repository.InformeTecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InformeTecnicoService {

    @Autowired
    private InformeTecnicoRepository repo;

    @Autowired
    private IncidenciaRepository incidenciaRepo;

    public InformeTecnico registrar(InformeTecnico informe) {
        incidenciaRepo.findById(informe.getIdIncidencia())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Incidencia no encontrada con id: " + informe.getIdIncidencia()));

        informe.setFechaInforme(LocalDateTime.now());
        return repo.save(informe);
    }

    public List<InformeTecnico> listarPorIncidencia(Integer idIncidencia) {
        return repo.findByIdIncidencia(idIncidencia);
    }
}
