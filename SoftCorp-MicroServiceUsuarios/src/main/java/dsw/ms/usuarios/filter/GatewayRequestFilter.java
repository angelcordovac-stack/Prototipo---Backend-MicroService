package dsw.ms.usuarios.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Obliga a que todo el trafico hacia /api/** pase por el Gateway.
 * El Gateway agrega el header X-Gateway-Request; sin el, se rechaza
 * cualquier peticion directa al puerto interno del microservicio.
 */
@Component
public class GatewayRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.startsWith("/api/")) {
            String gatewayHeader = httpRequest.getHeader("X-Gateway-Request");
            if (gatewayHeader == null || !gatewayHeader.equals("true")) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Direct requests are not allowed.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
