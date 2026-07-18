package dsw.ms.diccionariofallas.repository;

import dsw.ms.diccionariofallas.model.DiccionarioFallas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiccionarioFallasRepository extends JpaRepository<DiccionarioFallas, Integer> {
}
