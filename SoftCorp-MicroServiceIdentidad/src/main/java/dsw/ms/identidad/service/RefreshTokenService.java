package dsw.ms.identidad.service;

import dsw.ms.identidad.model.RefreshToken;
import dsw.ms.identidad.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    @Autowired
    private RefreshTokenRepository repo;

    public RefreshToken crear(Integer idUsuario) {
        repo.deleteByIdUsuario(idUsuario);

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setIdUsuario(idUsuario);
        rt.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        rt.setRevocado(false);

        return repo.save(rt);
    }

    public RefreshToken validar(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Refresh token no encontrado o inválido"));

        if (Boolean.TRUE.equals(rt.getRevocado())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revocado");
        }

        if (rt.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(rt);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado");
        }

        return rt;
    }

    public void revocarPorUsuario(Integer idUsuario) {
        repo.deleteByIdUsuario(idUsuario);
    }
}
