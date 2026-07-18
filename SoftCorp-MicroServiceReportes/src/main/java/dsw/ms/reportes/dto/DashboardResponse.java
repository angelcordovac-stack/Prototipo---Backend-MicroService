package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** Panel general de incidencias para el Jefe de area. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalIncidencias;
    private long pendientes;
    private long solucionadas;
    private Map<String, Long> porEstado;
    private Map<String, Long> porTecnico;
}
