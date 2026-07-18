package dsw.ms.usuarios.repository;

import dsw.ms.usuarios.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
}
