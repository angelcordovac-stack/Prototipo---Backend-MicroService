package dsw.ms.reportes.client;

import dsw.ms.reportes.dto.EquipoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SoftCorp-MicroServiceEquipos")
public interface EquipoClient {

    @GetMapping("/api/equipos/{codigoEquipo}")
    EquipoDTO buscarPorCodigo(@PathVariable("codigoEquipo") String codigoEquipo);
}
