package dsw.ms.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Representacion local de la respuesta de ms-incidencias. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaDTO {
    private Integer idIncidencia;
    private String codigoEquipo;
    private String descripcionProblema;
    private LocalDateTime fechaRegistro;
    private String quienRegistra;
    private Integer idTecnicoAsignado;
    private String estado;
    private String repuestoSolicitado;
    private String tipoSolucion;
    private Boolean requiereRepuesto;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaSolucion;
}
