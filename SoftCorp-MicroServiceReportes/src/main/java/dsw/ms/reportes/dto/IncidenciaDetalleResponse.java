package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Detalle completo de una incidencia, agregando datos de los 3 microservicios. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaDetalleResponse {
    private IncidenciaDTO incidencia;
    private EquipoDTO equipo;
    private TecnicoDTO tecnicoAsignado;
    private List<RepuestoDTO> repuestos;
    private List<InformeTecnicoDTO> informes;
}
