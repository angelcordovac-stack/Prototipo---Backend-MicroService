package dsw.ms.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO publico minimo de usuario, usado por otros microservicios para
 * enriquecer respuestas (ej. ms-diccionario-fallas necesita el nombre
 * del autor a partir de un idUsuario).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private String nombreCompleto;
    private String correo;
}
