package dsw.ms.diccionariofallas.client;

import dsw.ms.diccionariofallas.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia ms-usuarios, para resolver el nombre del autor
 * de una entrada del diccionario de fallas.
 */
@FeignClient(name = "SoftCorp-MicroServiceUsuarios")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}/dto")
    UsuarioDTO buscarPorId(@PathVariable Integer id);
}
