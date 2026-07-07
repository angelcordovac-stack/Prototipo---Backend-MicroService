package dsw.ms.incidencias.service;

import dsw.ms.incidencias.client.TecnicoClient;
import dsw.ms.incidencias.model.Incidencia;
import dsw.ms.incidencias.model.TecnicoDTO;
import dsw.ms.incidencias.repository.IncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository repo;

    @Autowired
    private TecnicoClient tecnicoClient;

    public Incidencia registrar(Incidencia incidencia) {
        incidencia.setFechaRegistro(LocalDateTime.now());
        incidencia.setEstado("Pendiente");
        return repo.save(incidencia);
    }

    public List<Incidencia> listar() {
        return repo.findAll();
    }

    public Incidencia buscar(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Incidencia no encontrada con id: " + id));
    }

    public List<Incidencia> tareasDeTecnico(Integer idTecnico) {
        return repo.findByIdTecnicoAsignado(idTecnico);
    }

    public List<Incidencia> historialEquipo(String codigoEquipo) {
        return repo.findByCodigoEquipoOrderByFechaRegistroDesc(codigoEquipo);
    }

    /**
     * Asigna un tecnico validando que:
     *  - La incidencia exista (BD local).
     *  - El tecnico exista y este disponible (se consulta al MS de Identidad).
     *  - El tecnico no supere su limite de incidencias Pendientes (BD local).
     *
     * @param authHeader header Authorization del llamador, que se reenvia a Identidad.
     */
    public Incidencia asignarTecnico(Integer idIncidencia, Integer idTecnico, String authHeader) {
        Incidencia incidencia = repo.findById(idIncidencia)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Incidencia no encontrada con id: " + idIncidencia));

        // Datos del tecnico via microservicio de Identidad (Eureka + LoadBalancer)
        TecnicoDTO tecnico = tecnicoClient.obtener(idTecnico, authHeader);

        if (tecnico == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tecnico no encontrado con id: " + idTecnico);
        }

        if (Boolean.FALSE.equals(tecnico.disponibilidad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tecnico no esta disponible.");
        }

        long carga = repo.findByIdTecnicoAsignado(idTecnico).stream()
                .filter(i -> "Pendiente".equalsIgnoreCase(i.getEstado()))
                .count();

        Integer max = tecnico.maxIncidencias() != null ? tecnico.maxIncidencias() : 5;
        if (carga >= max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tecnico ya alcanzo su limite de " + max + " incidencias pendientes.");
        }

        incidencia.setIdTecnicoAsignado(idTecnico);
        incidencia.setFechaAsignacion(LocalDateTime.now());
        return repo.save(incidencia);
    }

    public Incidencia solucionar(Integer idIncidencia, String tipoSolucion) {
        Incidencia incidencia = repo.findById(idIncidencia)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Incidencia no encontrada con id: " + idIncidencia));

        incidencia.setEstado("Solucionado");
        incidencia.setTipoSolucion(tipoSolucion);
        incidencia.setFechaSolucion(LocalDateTime.now());
        return repo.save(incidencia);
    }

    public List<Incidencia> pendientes() {
        return repo.findByEstado("Pendiente");
    }

    public List<Incidencia> solucionadas() {
        return repo.findByEstado("Solucionado");
    }
}
