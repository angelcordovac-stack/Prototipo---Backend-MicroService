package dsw.ms.incidencias.repository;

import dsw.ms.incidencias.model.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepuestoRepository extends JpaRepository<Repuesto, Integer> {

    List<Repuesto> findByIdIncidencia(Integer idIncidencia);
}
