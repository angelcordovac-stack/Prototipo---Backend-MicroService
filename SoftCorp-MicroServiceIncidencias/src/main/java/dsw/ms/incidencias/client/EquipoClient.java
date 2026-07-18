package dsw.ms.incidencias.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-equipos, para validar que un equipo exista
 * antes de registrar una incidencia sobre el.
 */
@FeignClient(name = "SoftCorp-MicroServiceEquipos")
public interface EquipoClient {

    @GetMapping("/api/equipos/{codigoEquipo}/existe")
    Boolean existe(@PathVariable String codigoEquipo);
}
