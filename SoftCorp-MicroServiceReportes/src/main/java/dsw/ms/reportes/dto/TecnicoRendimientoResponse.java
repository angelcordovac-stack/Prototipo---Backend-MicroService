package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuantas incidencias resuelve/tiene asignadas cada tecnico. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoRendimientoResponse {
    private Integer idTecnico;
    private String nombreTecnico;
    private long totalAsignadas;
    private long totalResueltas;
    private long totalPendientes;
}
