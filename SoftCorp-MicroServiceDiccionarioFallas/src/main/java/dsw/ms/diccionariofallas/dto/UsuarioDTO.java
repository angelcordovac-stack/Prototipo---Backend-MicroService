package dsw.ms.diccionariofallas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representacion local de la respuesta de ms-usuarios (GET /api/usuarios/{id}/dto).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private String nombreCompleto;
    private String correo;
}
