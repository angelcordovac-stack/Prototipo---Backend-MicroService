package dsw.ms.reportes.client;

import dsw.ms.reportes.dto.IncidenciaDTO;
import dsw.ms.reportes.dto.InformeTecnicoDTO;
import dsw.ms.reportes.dto.RepuestoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "SoftCorp-MicroServiceIncidencias")
public interface IncidenciaClient {

    @GetMapping("/api/incidencias")
    List<IncidenciaDTO> listar();

    @GetMapping("/api/incidencias/{id}")
    IncidenciaDTO buscarPorId(@PathVariable("id") Integer id);

    @GetMapping("/api/incidencias/filtrar")
    List<IncidenciaDTO> filtrar(@RequestParam(required = false) String estado,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                 @RequestParam(required = false) Integer idTecnico);

    @GetMapping("/api/repuestos/incidencia/{idIncidencia}")
    List<RepuestoDTO> repuestosDeIncidencia(@PathVariable("idIncidencia") Integer idIncidencia);

    @GetMapping("/api/informes/incidencia/{idIncidencia}")
    List<InformeTecnicoDTO> informesDeIncidencia(@PathVariable("idIncidencia") Integer idIncidencia);
}
