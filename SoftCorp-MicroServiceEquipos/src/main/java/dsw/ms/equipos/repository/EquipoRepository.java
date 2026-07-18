package dsw.ms.equipos.repository;

import dsw.ms.equipos.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepository extends JpaRepository<Equipo, String> {
}
