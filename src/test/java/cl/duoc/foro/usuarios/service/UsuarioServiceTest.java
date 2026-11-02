package cl.duoc.foro.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.foro.usuarios.dto.UsuarioActualizarRequest;
import cl.duoc.foro.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.foro.usuarios.dto.UsuarioResponse;
import cl.duoc.foro.usuarios.exception.ConflictoException;
import cl.duoc.foro.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.foro.usuarios.model.Rol;
import cl.duoc.foro.usuarios.model.Usuario;
import cl.duoc.foro.usuarios.repository.RolRepository;
import cl.duoc.foro.usuarios.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearGuardaLaClaveCifradaYAsignaRolUsuario() {
        UsuarioCrearRequest datos = new UsuarioCrearRequest("pedro", "pedro@foro.cl", "Clave2026*");
        when(rolRepository.findByNombre("USUARIO")).thenReturn(Optional.of(rol("USUARIO")));
        when(passwordEncoder.encode("Clave2026*")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(llamada -> llamada.getArgument(0));

        UsuarioResponse creado = usuarioService.crear(datos);

        assertThat(creado.nombreUsuario()).isEqualTo("pedro");
        assertThat(creado.rol()).isEqualTo("USUARIO");
        verify(usuarioRepository).save(argThat(usuario -> usuario.getPasswordHash().equals("hash")));
    }

    @Test
    void crearRechazaNombreDeUsuarioRepetido() {
        when(usuarioRepository.existsByNombreUsuario("camila")).thenReturn(true);

        UsuarioCrearRequest datos = new UsuarioCrearRequest("camila", "otra@foro.cl", "Clave2026*");

        assertThatThrownBy(() -> usuarioService.crear(datos)).isInstanceOf(ConflictoException.class);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void obtenerFallaSiElUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtener(99L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizarSinClaveMantieneLaActual() {
        Usuario usuario = usuario("camila", "USUARIO");
        usuario.setPasswordHash("hash-actual");
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuario));

        usuarioService.actualizar(3L, new UsuarioActualizarRequest("nuevo@foro.cl", null));

        assertThat(usuario.getEmail()).isEqualTo("nuevo@foro.cl");
        assertThat(usuario.getPasswordHash()).isEqualTo("hash-actual");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void cambiarRolAceptaElNombreEnMinusculas() {
        Usuario usuario = usuario("diego", "USUARIO");
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombre("MODERADOR")).thenReturn(Optional.of(rol("MODERADOR")));

        UsuarioResponse actualizado = usuarioService.cambiarRol(4L, "moderador");

        assertThat(actualizado.rol()).isEqualTo("MODERADOR");
    }

    @Test
    void cambiarRolFallaSiElRolNoExiste() {
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuario("diego", "USUARIO")));
        when(rolRepository.findByNombre("JEFE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.cambiarRol(4L, "JEFE"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    private Rol rol(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rol;
    }

    private Usuario usuario(String nombre, String rol) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombre);
        usuario.setRol(rol(rol));
        return usuario;
    }
}
