package dsw.ms.equipos.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Utilidad JWT en modo solo-validacion: este servicio no emite tokens,
 * solo verifica los que emitio Identidad usando la MISMA clave secreta.
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Integer extractIdPerfil(String token) {
        return extractAllClaims(token).get("idPerfil", Integer.class);
    }

    public Integer extractIdUsuario(String token) {
        return extractAllClaims(token).get("idUsuario", Integer.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
