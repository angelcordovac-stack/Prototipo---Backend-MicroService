package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representacion local de la respuesta de ms-usuarios. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoDTO {
    private Integer idUsuario;
    private String nombre;
    private String especialidad;
    private Integer maxIncidencias;
    private Boolean disponibilidad;
}
