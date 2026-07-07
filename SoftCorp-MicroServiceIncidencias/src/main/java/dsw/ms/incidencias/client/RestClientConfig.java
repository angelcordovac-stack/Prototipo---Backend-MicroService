package dsw.ms.incidencias.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    /**
     * RestTemplate directo (SIN @LoadBalanced/Eureka): en Render cada microservicio
     * es un web service independiente con su URL publica, asi que la llamada a
     * Identidad se hace por URL absoluta (ver IDENTIDAD_URI en TecnicoClient).
     *
     * Timeouts holgados para tolerar el "cold start" del free tier de Render
     * (un servicio dormido puede tardar 30-60s en responder la primera peticion).
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);   // 10 s para conectar
        factory.setReadTimeout(60_000);      // 60 s de lectura (aguanta cold start)
        return new RestTemplate(factory);
    }
}
