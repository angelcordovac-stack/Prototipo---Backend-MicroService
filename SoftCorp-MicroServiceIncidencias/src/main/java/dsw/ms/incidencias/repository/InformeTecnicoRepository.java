package dsw.ms.incidencias.repository;

import dsw.ms.incidencias.model.InformeTecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InformeTecnicoRepository extends JpaRepository<InformeTecnico, Integer> {

    List<InformeTecnico> findByIdIncidencia(Integer idIncidencia);

    Optional<InformeTecnico> findFirstByIdIncidencia(Integer idIncidencia);
}
