package dsw.ms.reportes.client;

import dsw.ms.reportes.dto.TecnicoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "SoftCorp-MicroServiceUsuarios")
public interface TecnicoClient {

    @GetMapping("/api/tecnicos")
    List<TecnicoDTO> listarTodos();

    @GetMapping("/api/tecnicos/{idUsuario}")
    TecnicoDTO buscarPorId(@PathVariable("idUsuario") Integer idUsuario);
}
