package dsw.ms.identidad.service;

import dsw.ms.identidad.model.RefreshToken;
import dsw.ms.identidad.model.Tecnico;
import dsw.ms.identidad.model.Usuario;
import dsw.ms.identidad.repository.TecnicoRepository;
import dsw.ms.identidad.repository.UsuarioRepository;
import dsw.ms.identidad.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    private static final int PERFIL_TECNICO = 2;

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private TecnicoRepository tecnicoRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public Map<String, Object> login(String correo, String password) {
        Usuario u = repo.findByCorreo(correo).orElse(null);

        if (u == null || Boolean.FALSE.equals(u.getActivo())) {
            return null;
        }

        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            return null;
        }

        String accessToken = jwtUtil.generateToken(u.getCorreo(), u.getIdUsuario(), u.getIdPerfil());

        RefreshToken refreshToken = refreshTokenService.crear(u.getIdUsuario());

        Map<String, Object> response = new HashMap<>();
        response.put("token", accessToken);
        response.put("refreshToken", refreshToken.getToken());
        response.put("idUsuario", u.getIdUsuario());
        response.put("nombreCompleto", u.getNombreCompleto());
        response.put("correo", u.getCorreo());
        response.put("idPerfil", u.getIdPerfil());
        response.put("activo", u.getActivo());
        response.put("perfil", switch (u.getIdPerfil()) {
            case 1 -> "Jefe";
            case 2 -> "Tecnico";
            case 3 -> "Sistemas";
            default -> "Usuario";
        });

        return response;
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Usuario buscar(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado con id: " + id));
    }

    public Usuario buscarPorCorreo(String correo) {
        return repo.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario no encontrado con correo: " + correo));
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario.getIdUsuario() == null) {
            repo.findByCorreo(usuario.getCorreo()).ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Ya existe un usuario con el correo " + usuario.getCorreo());
            });
        } else {
            repo.findByCorreo(usuario.getCorreo()).ifPresent(existente -> {
                if (!existente.getIdUsuario().equals(usuario.getIdUsuario())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Ya existe otro usuario con el correo " + usuario.getCorreo());
                }
            });
        }

        String pwd = usuario.getPasswordHash();
        if (pwd != null && !pwd.isBlank() && !pwd.startsWith("$2")) {
            usuario.setPasswordHash(passwordEncoder.encode(pwd));
        } else if (pwd == null || pwd.isBlank()) {
            if (usuario.getIdUsuario() != null) {
                Usuario existente = repo.findById(usuario.getIdUsuario()).orElse(null);
                if (existente != null) {
                    usuario.setPasswordHash(existente.getPasswordHash());
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La contrasena es obligatoria al crear un usuario");
            }
        }

        Usuario guardado = repo.save(usuario);

        sincronizarTecnico(guardado);

        return guardado;
    }

    private void sincronizarTecnico(Usuario usuario) {
        Integer idUsuario = usuario.getIdUsuario();

        if (PERFIL_TECNICO == usuario.getIdPerfil()) {
            boolean yaTecnico = tecnicoRepo.existsById(idUsuario);
            if (!yaTecnico) {
                Tecnico nuevoTecnico = new Tecnico();
                nuevoTecnico.setIdUsuario(idUsuario);
                nuevoTecnico.setEspecialidad("General");
                nuevoTecnico.setMaxIncidencias(5);
                nuevoTecnico.setDisponibilidad(true);
                tecnicoRepo.save(nuevoTecnico);
            }
        } else {
            if (tecnicoRepo.existsById(idUsuario)) {
                tecnicoRepo.deleteById(idUsuario);
            }
        }
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe el usuario con id: " + id);
        }
        tecnicoRepo.deleteById(id);
        repo.deleteById(id);
    }
}
