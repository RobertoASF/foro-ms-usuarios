package cl.duoc.foro.usuarios.service;

import cl.duoc.foro.usuarios.dto.LoginRequest;
import cl.duoc.foro.usuarios.dto.LoginResponse;
import cl.duoc.foro.usuarios.dto.UsuarioResponse;
import cl.duoc.foro.usuarios.exception.CredencialesInvalidasException;
import cl.duoc.foro.usuarios.model.Usuario;
import cl.duoc.foro.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(datos.nombreUsuario())
                .filter(Usuario::isActivo)
                .filter(encontrado -> passwordEncoder.matches(datos.password(), encontrado.getPasswordHash()))
                .orElseThrow(() -> {
                    log.warn("Intento de inicio de sesion rechazado");
                    return new CredencialesInvalidasException();
                });

        log.info("Inicio de sesion del usuario {}", usuario.getId());
        return new LoginResponse(tokenService.generar(usuario), "Bearer", tokenService.segundosValidez(),
                UsuarioResponse.desde(usuario));
    }
}
