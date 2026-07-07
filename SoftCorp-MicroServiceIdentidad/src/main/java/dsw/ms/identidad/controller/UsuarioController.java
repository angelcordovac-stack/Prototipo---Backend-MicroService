package dsw.ms.identidad.controller;

import dsw.ms.identidad.model.RefreshToken;
import dsw.ms.identidad.model.Usuario;
import dsw.ms.identidad.repository.UsuarioRepository;
import dsw.ms.identidad.service.RefreshTokenService;
import dsw.ms.identidad.service.UsuarioService;
import dsw.ms.identidad.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario user) {
        Map<String, Object> resultado = service.login(user.getCorreo(), user.getPasswordHash());

        if (resultado != null) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshTokenStr = body.get("refreshToken");

        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El refreshToken es requerido"));
        }

        RefreshToken rt = refreshTokenService.validar(refreshTokenStr);

        Usuario usuario = usuarioRepository.findById(rt.getIdUsuario())
                .orElse(null);

        if (usuario == null || Boolean.FALSE.equals(usuario.getActivo())) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado o inactivo"));
        }

        String nuevoAccessToken = jwtUtil.generateToken(
                usuario.getCorreo(), usuario.getIdUsuario(), usuario.getIdPerfil());

        RefreshToken nuevoRefreshToken = refreshTokenService.crear(usuario.getIdUsuario());

        return ResponseEntity.ok(Map.of(
                "token", nuevoAccessToken,
                "refreshToken", nuevoRefreshToken.getToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String refreshTokenStr = body.get("refreshToken");
        if (refreshTokenStr != null && !refreshTokenStr.isBlank()) {
            try {
                RefreshToken rt = refreshTokenService.validar(refreshTokenStr);
                refreshTokenService.revocarPorUsuario(rt.getIdUsuario());
            } catch (Exception ignored) {
            }
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente"));
    }
}
