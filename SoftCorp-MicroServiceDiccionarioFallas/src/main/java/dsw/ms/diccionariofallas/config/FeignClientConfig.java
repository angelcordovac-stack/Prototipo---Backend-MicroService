package dsw.ms.diccionariofallas.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ms-usuarios valida JWT localmente y exige autenticacion en casi todos
 * sus endpoints. Este interceptor reenvia el Authorization del usuario que
 * origino la peticion hacia ms-diccionario-fallas, para que la llamada
 * Feign llegue autenticada con la identidad real de ese usuario.
 *
 * Se mantiene ademas el header X-Gateway-Request como segunda capa, ya
 * que el GatewayRequestFilter de ms-usuarios sigue exigiendolo para todo
 * trafico a /api/**.
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
