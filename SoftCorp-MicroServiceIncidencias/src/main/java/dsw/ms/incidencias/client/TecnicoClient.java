package dsw.ms.incidencias.client;

import dsw.ms.incidencias.model.TecnicoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Cliente REST hacia el microservicio de Identidad.
 * Reemplaza el acceso directo a la tabla 'tecnicos' que tenia el monolito.
 *
 * La URL base es configurable (IDENTIDAD_URI): en Render apunta a la URL publica
 * del servicio de Identidad; en local, por defecto http://localhost:8081.
 */
@Component
public class TecnicoClient {

    private final RestTemplate restTemplate;
    private final String identidadBaseUrl;

    public TecnicoClient(RestTemplate restTemplate,
                         @Value("${identidad.uri:http://localhost:8081}") String identidadBaseUrl) {
        this.restTemplate = restTemplate;
        // normaliza para evitar dobles barras
        this.identidadBaseUrl = identidadBaseUrl.replaceAll("/+$", "");
    }

    /**
     * Obtiene un tecnico por id. Propaga el header Authorization del llamador
     * para que Identidad valide el JWT igual que en cualquier otra peticion.
     */
    public TecnicoDTO obtener(Integer idTecnico, String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = identidadBaseUrl + "/api/tecnicos/" + idTecnico;
        try {
            ResponseEntity<TecnicoDTO> resp = restTemplate.exchange(
                    url, HttpMethod.GET, entity, TecnicoDTO.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Tecnico no encontrado con id: " + idTecnico);
        }
    }
}
