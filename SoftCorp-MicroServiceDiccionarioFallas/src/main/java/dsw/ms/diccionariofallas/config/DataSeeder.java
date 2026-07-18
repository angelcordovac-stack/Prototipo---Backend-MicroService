package dsw.ms.diccionariofallas.config;

import dsw.ms.diccionariofallas.model.DiccionarioFallas;
import dsw.ms.diccionariofallas.repository.DiccionarioFallasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Siembra las entradas reales del diccionario de fallas rescatadas del
 * backup de Supabase del proyecto original en monolito (solo si la tabla
 * esta vacia). id_autor referencia usuarios ya sembrados por ms-usuarios
 * (2 = Alberto Ramos, 3 = Ricardo Quispe, 20 = Luis Perez, 22 = Oscar Huaman).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private DiccionarioFallasRepository repo;

    // problemaComun, solucionSugerida, idAutor, estado, fecha (puede ser null)
    private static final Object[][] FALLAS_REALES = {
            {"PC no enciende", "Verificar fuente de poder con multímetro.", 2, null, null},
            {"Outlook no abre", "Ejecutar outlook.exe/safe para modo seguro.", 3, null, null},
            {"La PC se sobrecalienta", "Revisar la cantidad de pasta térmica entre el CPU y el Cooler", 20, null, null},
            {"PC lenta", "Iniciar un diagnostico en búsqueda de virus en el sistema", 20, null, null},
            {"Pantallazo Azul al iniciar Windows", "Restaurar el sistema desde un punto de control", 22, "CRITICO", "2026-06-02"},
            {"La CPU se calienta rapido", "Revisión urgente de pasta térmica", 22, null, null},
            {"cscsc", "scscscsc", 22, null, null},
            {"cscsc", "scscsc", 22, null, null},
            {"htedhdhdfhd", "sfgsgssgg", 22, "EN_CURSO", "2026-06-03"},
            {"dwdwd", "wdwwdwd", 22, "EN_CURSO", "2026-06-13"},
            {"fwfwfegrehg", "srgrsdgegerg", 22, "CRITICO", "2026-06-01"},
    };

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            for (Object[] f : FALLAS_REALES) {
                DiccionarioFallas falla = new DiccionarioFallas();
                falla.setProblemaComun((String) f[0]);
                falla.setSolucionSugerida((String) f[1]);
                falla.setIdAutor((Integer) f[2]);
                falla.setEstado((String) f[3]);
                falla.setFecha(f[4] != null ? LocalDate.parse((String) f[4]) : null);
                falla.setFechaRegistro(LocalDateTime.now());
                repo.save(falla);
            }
        }
    }
}
