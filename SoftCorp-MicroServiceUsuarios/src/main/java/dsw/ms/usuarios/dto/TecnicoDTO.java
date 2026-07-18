package dsw.ms.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO publico para exponer datos de un tecnico a otros microservicios
 * (ej. ms-incidencias, via Feign) sin exponer la entidad JPA completa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TecnicoDTO {
    private Integer idUsuario;
    private String nombre;
    private String especialidad;
    private Integer maxIncidencias;
    private Boolean disponibilidad;
}
