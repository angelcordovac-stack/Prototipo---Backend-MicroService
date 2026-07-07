package dsw.ms.incidencias.repository;

import dsw.ms.incidencias.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {

    List<Incidencia> findByIdTecnicoAsignado(Integer idTecnico);

    List<Incidencia> findByEstado(String estado);

    List<Incidencia> findByCodigoEquipoOrderByFechaRegistroDesc(String codigoEquipo);
}
