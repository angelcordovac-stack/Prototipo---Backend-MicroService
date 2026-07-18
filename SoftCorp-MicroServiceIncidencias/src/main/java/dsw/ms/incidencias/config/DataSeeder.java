package dsw.ms.incidencias.config;

import dsw.ms.incidencias.model.InformeTecnico;
import dsw.ms.incidencias.model.Repuesto;
import dsw.ms.incidencias.repository.IncidenciaRepository;
import dsw.ms.incidencias.repository.InformeTecnicoRepository;
import dsw.ms.incidencias.repository.RepuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Siembra las incidencias, repuestos e informes tecnicos reales
 * rescatados del backup de Supabase del proyecto original en monolito
 * (solo si las tablas estan vacias).
 *
 * SQL Server exige "SET IDENTITY_INSERT" para poder insertar valores
 * explicitos en una columna identity (a diferencia de Postgres, donde el
 * insert explicito simplemente funciona). Se preserva el id_incidencia
 * exacto de cada fila porque repuestos e informes_tecnicos los referencian.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private RepuestoRepository repuestoRepository;

    @Autowired
    private InformeTecnicoRepository informeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // id, codigoEquipo, descripcion, fechaRegistro, quienRegistra, idTecnico, estado,
    // repuestoSolicitado, tipoSolucion, requiereRepuesto, fechaAsignacion, fechaSolucion
    private static final Object[][] INCIDENCIAS_REALES = {
            {1, "PC-2022-055", "Reinstalación de Windows 11", "2026-01-20 09:00:00", "Sistemas", 2, "Solucionado", null, "en_sitio", false, null, "2026-01-20 11:00:00"},
            {2, "PC-2022-055", "Limpieza y cambio de pasta térmica", "2026-03-15 10:00:00", "Sistemas", 2, "Solucionado", null, "en_sitio", false, null, "2026-03-15 12:00:00"},
            {3, "PC-2022-055", "Cambio de disco duro SSD", "2026-04-28 14:30:00", "A. Coopa", 3, "Solucionado", null, "en_sitio", true, null, "2026-04-28 16:30:00"},
            {4, "PC-2022-055", "Eliminación de virus y lentitud", "2026-05-03 08:00:00", "A. Coopa", 2, "Solucionado", null, "en_sitio", false, null, "2026-05-03 10:00:00"},
            {5, "PC-2026-841", "PC no enciende (Posible fuente)", "2026-05-04 11:00:00", "M. Torres", 4, "Solucionado", null, null, true, "2026-05-04 11:15:00", "2026-05-12 06:12:09.618529"},
            {10, "PC-2022-055", "Daño en la placa", "2026-05-18 02:55:03.274249", "Jeanfranco Javier", 4, "Pendiente", null, null, false, "2026-05-18 03:05:29.737474", null},
            {11, "PC-2026-841", "Repuesto de pantalla", "2026-05-18 03:52:20.490167", "Jeanfranco Javier", 20, "Pendiente", null, null, false, "2026-05-18 03:55:40.14644", null},
            {12, "PC-2022-055", "falta ram", "2026-05-18 23:47:32.20633", "Jeanfranco Javier", 20, "Pendiente", null, null, false, "2026-05-18 23:48:51.024045", null},
            {13, "PC-2026-841", "No prende la pantalla", "2026-05-19 18:16:22.44179", "Jeanfranco Javier", 20, "Pendiente", null, null, false, "2026-05-19 18:18:23.760085", null},
            {14, "PC-2026-841", "Se encuentra muy lento la computadora", "2026-05-19 18:17:42.958533", "Jeanfranco Javier", null, "Pendiente", null, null, false, null, null},
            {15, "PC-2026-841", "Se encuentra muy lento la computadora", "2026-05-19 18:17:43.261013", "Jeanfranco Javier", null, "Pendiente", null, null, false, null, null},
            {16, "PC-2022-055", "Reinstalación de Windows 10", "2026-06-22 21:40:39.083071", "Jeanfranco Javier", null, "Pendiente", null, null, false, null, null},
            {17, "PC-2022-055", "Reinstalación de Windows 10", "2026-06-22 21:40:41.588763", "Jeanfranco Javier", 20, "Pendiente", null, null, false, "2026-06-22 21:43:07.503086", null},
            {18, "PC-2026-841", "El teclado no funciona", "2026-06-22 21:44:22.881471", "Jeanfranco Javier", 20, "Pendiente", null, null, false, "2026-06-22 21:44:41.881572", null},
            {19, "PC-2022-055", "No se escucha el audio en la PC", "2026-06-22 21:45:11.130079", "Jeanfranco Javier", 6, "Pendiente", null, null, false, "2026-06-22 21:49:33.082717", null},
    };

    @Override
    public void run(String... args) {
        if (incidenciaRepository.count() == 0) {
            jdbcTemplate.execute("SET IDENTITY_INSERT incidencias ON");

            for (Object[] i : INCIDENCIAS_REALES) {
                jdbcTemplate.update(
                        "INSERT INTO incidencias (id_incidencia, codigo_equipo, descripcion_problema, fecha_registro, " +
                        "quien_registra, id_tecnico_asignado, estado, repuesto_solicitado, tipo_solucion, " +
                        "requiere_repuesto, fecha_asignacion, fecha_solucion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        i[0], i[1], i[2], parseDT((String) i[3]), i[4], i[5], i[6], i[7], i[8], i[9],
                        parseDT((String) i[10]), parseDT((String) i[11])
                );
            }

            jdbcTemplate.execute("SET IDENTITY_INSERT incidencias OFF");

            // Reacomoda el contador identity para que la proxima incidencia
            // creada desde la app siga despues del id mas alto insertado.
            jdbcTemplate.execute("DBCC CHECKIDENT ('incidencias', RESEED, 19)");
        }

        if (repuestoRepository.count() == 0) {
            guardarRepuesto(3, "SSD Kingston A400 480GB", "2026-04-28 15:00:00", "2026-04-29 10:00:00", "Entregado");
            guardarRepuesto(5, "Fuente de poder 500W certificada", "2026-05-04 11:15:00", "2026-05-12 11:20:07.524275", "Entregado");
            guardarRepuesto(11, "Toshiba 14 pulgadas", "2026-05-18 03:57:56.950849", "2026-05-19 18:21:40.379838", "Entregado");
            guardarRepuesto(12, "ddr3", "2026-05-18 23:51:51.040161", "2026-05-19 18:28:22.885689", "Entregado");
        }

        if (informeRepository.count() == 0) {
            guardarInforme(1, "Sistema operativo corrupto por actualización fallida",
                    "Formateo e instalación limpia de Windows 11",
                    "Se realizó backup previo de documentos del usuario");
            guardarInforme(2, "Sobrecalentamiento por pasta térmica seca",
                    "Limpieza interna y aplicación de pasta térmica Arctic MX-4",
                    "Temperatura bajó de 85°C a 45°C en idle");
            guardarInforme(3, "Disco duro mecánico con sectores dañados",
                    "Clonación e instalación de SSD Kingston 480GB",
                    "Equipo quedó 5x más rápido en arranque");
            guardarInforme(4, "Infección por malware y archivos temporales acumulados",
                    "Análisis con Malwarebytes y limpieza con CCleaner",
                    "Se instaló antivirus corporativo y se capacitó al usuario");
        }
    }

    private void guardarRepuesto(Integer idIncidencia, String descripcion, String fechaSolicitud,
                                  String fechaEntrega, String estado) {
        Repuesto r = new Repuesto();
        r.setIdIncidencia(idIncidencia);
        r.setDescripcion(descripcion);
        r.setFechaSolicitud(parseDT(fechaSolicitud));
        r.setFechaEntrega(parseDT(fechaEntrega));
        r.setEstado(estado);
        repuestoRepository.save(r);
    }

    private void guardarInforme(Integer idIncidencia, String diagnostico, String procedimiento, String observaciones) {
        InformeTecnico inf = new InformeTecnico();
        inf.setIdIncidencia(idIncidencia);
        inf.setDiagnostico(diagnostico);
        inf.setProcedimientoRealizado(procedimiento);
        inf.setObservaciones(observaciones);
        inf.setFechaInforme(LocalDateTime.now());
        informeRepository.save(inf);
    }

    private LocalDateTime parseDT(String valor) {
        if (valor == null) {
            return null;
        }
        return LocalDateTime.parse(valor.replace(' ', 'T'));
    }
}
