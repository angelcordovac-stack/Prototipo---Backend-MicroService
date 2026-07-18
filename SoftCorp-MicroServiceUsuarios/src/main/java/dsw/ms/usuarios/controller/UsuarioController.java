package dsw.ms.usuarios.controller;

import dsw.ms.usuarios.dto.UsuarioDTO;
import dsw.ms.usuarios.model.RefreshToken;
import dsw.ms.usuarios.model.Usuario;
import dsw.ms.usuarios.repository.UsuarioRepository;
import dsw.ms.usuarios.service.RefreshTokenService;
import dsw.ms.usuarios.service.UsuarioService;
import dsw.ms.usuarios.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ---- AUTENTICACION (publica, pero exige pasar por el Gateway) ----

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

        Usuario usuario = usuarioRepository.findById(rt.getIdUsuario()).orElse(null);

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
                // Si el token ya esta invalido, no es un error del cliente
            }
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesion cerrada correctamente"));
    }

    // ---- GESTION DE USUARIOS (requiere rol JEFE) ----

    @GetMapping
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<Usuario> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    /**
     * Endpoint interno consumido via Feign por otros microservicios
     * (ej. ms-diccionario-fallas) para resolver el nombre de un usuario.
     */
    @GetMapping("/{id}/dto")
    public ResponseEntity<UsuarioDTO> obtenerDTO(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarDTO(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        usuario.setIdUsuario(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id,
                                               @Valid @RequestBody Usuario usuario) {
        usuario.setIdUsuario(id);
        return ResponseEntity.ok(service.guardar(usuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('JEFE')")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}
