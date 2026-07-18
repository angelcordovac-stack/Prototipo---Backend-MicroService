package dsw.ms.reportes.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ms-incidencias, ms-usuarios y ms-equipos validan JWT localmente y exigen
 * autenticacion en casi todos sus endpoints. Este interceptor reenvia el
 * Authorization del Jefe/usuario que origino la peticion hacia
 * ms-reportes, para que las llamadas Feign lleguen autenticadas con la
 * identidad real de ese usuario.
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
