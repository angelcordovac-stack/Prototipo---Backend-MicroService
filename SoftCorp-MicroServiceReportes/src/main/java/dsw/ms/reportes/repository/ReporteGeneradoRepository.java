package dsw.ms.reportes.repository;

import dsw.ms.reportes.model.ReporteGenerado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteGeneradoRepository extends JpaRepository<ReporteGenerado, Integer> {

    List<ReporteGenerado> findAllByOrderByFechaGeneracionDesc();
}
