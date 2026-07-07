package dsw.ms.incidencias.security;

import dsw.ms.incidencias.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT sin acceso a BD: la identidad y el rol se derivan de los claims
 * del token (subject + idPerfil). Es el patron stateless correcto para los
 * microservicios que solo validan (no tienen la tabla usuarios).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtUtil.isTokenValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String email = jwtUtil.extractEmail(token);
            Integer idPerfil = jwtUtil.extractIdPerfil(token);

            String rol = switch (idPerfil == null ? 0 : idPerfil) {
                case 1 -> "ROLE_JEFE";
                case 2 -> "ROLE_TECNICO";
                case 3 -> "ROLE_SISTEMAS";
                default -> "ROLE_USUARIO";
            };

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority(rol))
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
