package dsw.ms.incidencias.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Los microservicios destino (ms-usuarios, ms-equipos) validan JWT
 * localmente y exigen autenticacion en casi todos sus endpoints. Este
 * interceptor reenvia el Authorization del usuario que origino la peticion
 * hacia ms-incidencias, para que la llamada Feign llegue autenticada con la
 * identidad real de ese usuario (no con un token generico de servicio).
 *
 * Ademas se mantiene el header X-Gateway-Request como segunda capa, ya que
 * el GatewayRequestFilter de los servicios destino sigue exigiendolo para
 * todo trafico a /api/**.
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Gateway-Request", "true");

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
