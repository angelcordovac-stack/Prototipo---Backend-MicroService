package dsw.ms.equipos.config;

import dsw.ms.equipos.model.Equipo;
import dsw.ms.equipos.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Siembra los equipos reales rescatados del backup de Supabase del
 * proyecto original en monolito (solo si la tabla esta vacia).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private EquipoRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.save(new Equipo("PC-2022-055", "Dell OptiPlex 3090", "Contabilidad - Piso 2", "A. Coopa"));
            repo.save(new Equipo("PC-2026-841", "HP ProDesk 400 G9", "Recursos Humanos - Piso 1", "M. Torres"));
        }
    }
}
