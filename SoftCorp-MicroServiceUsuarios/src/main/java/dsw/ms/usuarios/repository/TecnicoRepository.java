package dsw.ms.usuarios.repository;

import dsw.ms.usuarios.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {

    List<Tecnico> findByDisponibilidad(Boolean disponibilidad);
}
