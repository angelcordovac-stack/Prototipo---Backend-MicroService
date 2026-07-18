package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Representacion local de la respuesta de ms-equipos. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private String codigoEquipo;
    private String marcaModelo;
    private String areaUbicacion;
    private String responsable;
}
