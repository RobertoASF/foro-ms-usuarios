package cl.duoc.foro.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import cl.duoc.foro.usuarios.dto.LoginRequest;
import cl.duoc.foro.usuarios.dto.LoginResponse;
import cl.duoc.foro.usuarios.exception.CredencialesInvalidasException;
import cl.duoc.foro.usuarios.model.Rol;
import cl.duoc.foro.usuarios.model.Usuario;
import cl.duoc.foro.usuarios.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginEntregaTokenConCredencialesCorrectas() {
        Usuario usuario = usuario(true);
        when(usuarioRepository.findByNombreUsuario("camila")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Foro2026*", "hash")).thenReturn(true);
        when(tokenService.generar(usuario)).thenReturn("token");
        when(tokenService.segundosValidez()).thenReturn(3600L);

        LoginResponse respuesta = authService.login(new LoginRequest("camila", "Foro2026*"));

        assertThat(respuesta.token()).isEqualTo("token");
        assertThat(respuesta.tipo()).isEqualTo("Bearer");
        assertThat(respuesta.usuario().nombreUsuario()).isEqualTo("camila");
    }

    @Test
    void loginRechazaClaveIncorrecta() {
        when(usuarioRepository.findByNombreUsuario("camila")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("camila", "mala")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void loginRechazaUsuarioInactivo() {
        when(usuarioRepository.findByNombreUsuario("camila")).thenReturn(Optional.of(usuario(false)));

        assertThatThrownBy(() -> authService.login(new LoginRequest("camila", "Foro2026*")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void loginRechazaUsuarioInexistente() {
        when(usuarioRepository.findByNombreUsuario("nadie")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nadie", "Foro2026*")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    private Usuario usuario(boolean activo) {
        Rol rol = new Rol();
        rol.setNombre("USUARIO");

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("camila");
        usuario.setPasswordHash("hash");
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }
}
