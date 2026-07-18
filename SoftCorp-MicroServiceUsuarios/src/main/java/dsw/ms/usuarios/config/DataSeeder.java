package dsw.ms.usuarios.config;

import dsw.ms.usuarios.model.Perfil;
import dsw.ms.usuarios.model.Tecnico;
import dsw.ms.usuarios.model.Usuario;
import dsw.ms.usuarios.repository.PerfilRepository;
import dsw.ms.usuarios.repository.TecnicoRepository;
import dsw.ms.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Siembra datos iniciales al arrancar, independientemente del motor de BD
 * configurado. Solo inserta si las tablas estan vacias, para no duplicar
 * datos en reinicios.
 *
 * Incluye el usuario JEFE de prueba (admin@softcorp.com / admin123) y,
 * ademas, los usuarios/perfiles/tecnicos reales recuperados del backup de
 * Supabase del proyecto original en monolito. Los id_usuario de esos
 * registros reales se preservan tal cual (en vez de dejar que la secuencia
 * les asigne otros nuevos), porque las incidencias y el diccionario de
 * fallas los referencian por ese id exacto.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // idUsuario, nombreCompleto, correo, passwordHashOTexto, idPerfil, activo, telefono
    private static final Object[][] USUARIOS_REALES = {
            {2, "Ing. Alberto Ramos", "aramos@softcorp.pe", "$2a$10$QTtaETvvTC1L/kkJTJZLL.zGwRxw66jChjA0zbjpqe7xwgQXYzuHi", 1, true, null},
            {3, "Ricardo Quispe", "rquispe@softcorp.pe", "tech01", 2, true, null},
            {4, "Karla Flores", "kflores@softcorp.pe", "tech02", 2, false, null},
            {5, "Manuel Soto", "msoto@softcorp.pe", "tech03", 2, true, null},
            {6, "Jorge Valdivia", "jvaldivia@softcorp.pe", "tech04", 2, true, null},
            {7, "Daniela Espinoza", "despinoza@softcorp.pe", "tech05", 2, true, null},
            {8, "Lucía Méndez", "lmendez@softcorp.pe", "sys01", 3, true, null},
            {9, "Carlos Prado", "cprado@softcorp.pe", "sys02", 3, true, null},
            {10, "Sofía Rojas", "srojas@softcorp.pe", "sys03", 3, true, null},
            {11, "Esteban Bazán", "ebazan@softcorp.pe", "$2a$10$4KloUVVjxASa4.33IGcC5.rziOHBmjHg5hu.8UL3bA0UtO1Q.R1TS", 3, true, null},
            {14, "mario rojas", "mario@softcorp.pe", "$2a$10$oz28rw292ls9LBLPHALYBe/BwKiVLutlAlFuNqGSSxB8PDNdFEkt.", 2, true, "991016311"},
            {15, "mario rojas", "jos@softcorp.pe", "$2a$10$y6tR/P3U7YFVPzNz/8YMSeO5XRcfAJZH/xzBVqhXoiK0EJPtDBoNK", 2, true, "991016311"},
            {17, "Jeanfranco Javier", "jeanfrancos@softcorp.pe", "$2a$10$V1j9tHWWhCHO0ighMYRj1.KEr/pVy/Z2uEqGMhmyGlGRkISHOkyBW", 1, true, null},
            {20, "Luis Perez", "Luis@softcorp.pe", "$2a$10$A5hdJEMqHA8n4Hce7DfPou31wcCuWBCLgoG7uVjjI3xTSwtLi4fDK", 2, true, "991016319"},
            {22, "Oscar Huaman", "Oscar@softcorp.pe", "$2a$10$AtYnemq3P96zIalJcly.SOIR3JqORdIhEcftRfsRAT1LvX4vVUjCO", 3, true, "91016320"},
            {23, "Rafael Rojas", "Rafael@softcorp.pe", "$2a$10$erkGMroPNg4Vq3UKRvNLze3AIliaItYfcA.ceA.utht67JEzpup..", 2, true, "91016343"},
            {24, "Renato Morado", "renatomeza@softcorp.pe", "$2a$10$x8OWMnSj1CfRlWtU9wtF/en74b4t6cOoRp9q6ipy7AwWxaDu.OxGG", 2, true, "981033172"},
    };

    // idUsuario, especialidad, maxIncidencias, disponibilidad
    private static final Object[][] TECNICOS_REALES = {
            {2, "Hardware y Reparación", 5, true},
            {3, "Soporte de Software", 5, true},
            {4, "Mantenimiento General", 5, true},
            {5, "Redes y Conectividad", 4, true},
            {6, "Soporte Técnico Nivel 1", 5, true},
            {20, "Soporte General", 5, true},
    };

    @Override
    public void run(String... args) {
        if (perfilRepository.count() == 0) {
            guardarPerfil("JEFE_AREA");
            guardarPerfil("TECNICO");
            guardarPerfil("USUARIO_SISTEMAS");
        }

        if (usuarioRepository.count() == 0) {
            // Usuario de prueba (se mantiene con contrasena conocida para poder loguear de inmediato)
            Usuario admin = new Usuario();
            admin.setNombreCompleto("Administrador SoftCorp");
            admin.setCorreo("admin@softcorp.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setTelefono("999999999");
            admin.setIdPerfil(1);
            admin.setActivo(true);
            usuarioRepository.save(admin); // primer insert -> la secuencia le asigna id_usuario = 1

            // Usuarios reales rescatados del backup de Supabase, preservando su id_usuario exacto
            for (Object[] u : USUARIOS_REALES) {
                Integer id = (Integer) u[0];
                String nombre = (String) u[1];
                String correo = (String) u[2];
                String passwordOriginal = (String) u[3];
                Integer idPerfil = (Integer) u[4];
                Boolean activo = (Boolean) u[5];
                String telefono = (String) u[6];

                // Algunas filas del backup tenian la contrasena en texto plano
                // (nunca se llegaron a hashear). Si no parece un hash bcrypt, se
                // hashea recien aqui; si ya es un hash valido, se respeta tal cual.
                String passwordHash = pareceHashBcrypt(passwordOriginal)
                        ? passwordOriginal
                        : passwordEncoder.encode(passwordOriginal);

                jdbcTemplate.update(
                        "INSERT INTO usuarios (id_usuario, nombre_completo, correo, password_hash, telefono, id_perfil, activo) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        id, nombre, correo, passwordHash, telefono, idPerfil, activo
                );
            }

            // Reacomoda la secuencia para que el proximo usuario creado desde la
            // app (no desde este seeder) siga despues del id mas alto insertado.
            jdbcTemplate.execute(
                    "SELECT setval(pg_get_serial_sequence('usuarios', 'id_usuario'), " +
                    "(SELECT MAX(id_usuario) FROM usuarios))"
            );
        }

        if (tecnicoRepository.count() == 0) {
            for (Object[] t : TECNICOS_REALES) {
                Tecnico tecnico = new Tecnico();
                tecnico.setIdUsuario((Integer) t[0]);
                tecnico.setEspecialidad((String) t[1]);
                tecnico.setMaxIncidencias((Integer) t[2]);
                tecnico.setDisponibilidad((Boolean) t[3]);
                tecnicoRepository.save(tecnico);
            }
        }
    }

    private void guardarPerfil(String nombre) {
        Perfil perfil = new Perfil();
        perfil.setNombrePerfil(nombre);
        perfilRepository.save(perfil);
    }

    private boolean pareceHashBcrypt(String valor) {
        return valor != null && (valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$"));
    }
}
