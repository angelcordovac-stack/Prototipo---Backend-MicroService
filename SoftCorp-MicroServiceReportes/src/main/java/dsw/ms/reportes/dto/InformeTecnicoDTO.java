package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Representacion local de la respuesta de ms-incidencias. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformeTecnicoDTO {
    private Integer idInforme;
    private Integer idIncidencia;
    private String diagnostico;
    private String procedimientoRealizado;
    private String observaciones;
    private LocalDateTime fechaInforme;
}
