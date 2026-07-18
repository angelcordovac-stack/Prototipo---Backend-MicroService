package dsw.ms.reportes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registro de auditoria: cada vez que un Jefe genera un reporte
 * (dashboard o rendimiento de tecnicos), queda una fila aca. Es el unico
 * dato propio de ms-reportes; el resto de la informacion que expone la
 * agrega en el momento desde ms-incidencias, ms-usuarios y ms-equipos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reportes_generados")
public class ReporteGenerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer idReporte;

    @Column(name = "tipo_reporte")
    private String tipoReporte;

    @Column(name = "id_usuario_solicitante")
    private Integer idUsuarioSolicitante;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;
}
