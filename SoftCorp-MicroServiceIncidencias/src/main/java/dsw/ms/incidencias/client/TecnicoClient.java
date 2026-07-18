package dsw.ms.incidencias.client;

import dsw.ms.incidencias.dto.TecnicoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-usuarios. El name debe coincidir con
 * spring.application.name registrado en Eureka por ese microservicio.
 */
@FeignClient(name = "SoftCorp-MicroServiceUsuarios")
public interface TecnicoClient {

    @GetMapping("/api/tecnicos/{idUsuario}")
    TecnicoDTO buscarPorId(@PathVariable Integer idUsuario);
}
