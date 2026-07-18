package dsw.ms.incidencias.service;

import dsw.ms.incidencias.client.EquipoClient;
import dsw.ms.incidencias.client.TecnicoClient;
import dsw.ms.incidencias.dto.TecnicoDTO;
import dsw.ms.incidencias.model.Incidencia;
import dsw.ms.incidencias.repository.IncidenciaRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository repo;

    @Autowired
    private TecnicoClient tecnicoClient;

    @Autowired
    private EquipoClient equipoClient;

    /**
     * Registra una incidencia validando (via Feign, contra ms-equipos) que
     * el equipo referenciado exista.
     */
    public Incidencia registrar(Incidencia incidencia) {
        boolean existeEquipo;
        try {
            existeEquipo = Boolean.TRUE.equals(equipoClient.existe(incidencia.getCodigoEquipo()));
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo validar el equipo en ms-equipos: " + e.getMessage());
        }
        if (!existeEquipo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No existe un equipo con codigo: " + incidencia.getCodigoEquipo());
        }

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
     * Asigna un tecnico a una incidencia validando (via Feign, contra
     * ms-usuarios) que:
     *  - El tecnico exista y este disponible.
     *  - El tecnico no haya superado su limite de incidencias activas (Pendiente).
     */
    public Incidencia asignarTecnico(Integer idIncidencia, Integer idTecnico) {
        Incidencia incidencia = repo.findById(idIncidencia)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Incidencia no encontrada con id: " + idIncidencia));

        TecnicoDTO tecnico;
        try {
            tecnico = tecnicoClient.buscarPorId(idTecnico);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tecnico no encontrado con id: " + idTecnico);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo validar el tecnico en ms-usuarios: " + e.getMessage());
        }

        if (Boolean.FALSE.equals(tecnico.getDisponibilidad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tecnico no esta disponible.");
        }

        long carga = repo.findByIdTecnicoAsignado(idTecnico).stream()
                .filter(i -> "Pendiente".equalsIgnoreCase(i.getEstado()))
                .count();

        Integer max = tecnico.getMaxIncidencias() != null ? tecnico.getMaxIncidencias() : 5;
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

    /**
     * Filtrado avanzado combinando estado, rango de fechas de registro y
     * tecnico asignado. Todos los parametros son opcionales; los que vengan
     * en null se ignoran. Usado directamente y tambien consumido via Feign
     * por ms-reportes para armar el dashboard y los reportes de rendimiento.
     */
    public List<Incidencia> filtrar(String estado, LocalDate desde, LocalDate hasta, Integer idTecnico) {
        return repo.findAll().stream()
                .filter(i -> estado == null || estado.equalsIgnoreCase(i.getEstado()))
                .filter(i -> idTecnico == null || idTecnico.equals(i.getIdTecnicoAsignado()))
                .filter(i -> desde == null || i.getFechaRegistro() == null
                        || !i.getFechaRegistro().toLocalDate().isBefore(desde))
                .filter(i -> hasta == null || i.getFechaRegistro() == null
                        || !i.getFechaRegistro().toLocalDate().isAfter(hasta))
                .toList();
    }
}
