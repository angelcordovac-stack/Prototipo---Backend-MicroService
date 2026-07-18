package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Representacion local de la respuesta de ms-incidencias. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepuestoDTO {
    private Integer idRepuesto;
    private Integer idIncidencia;
    private String descripcion;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEntrega;
    private String estado;
}
