package cl.duoc.foro.usuarios.service;

import cl.duoc.foro.usuarios.dto.UsuarioActualizarRequest;
import cl.duoc.foro.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.foro.usuarios.dto.UsuarioResponse;
import cl.duoc.foro.usuarios.exception.ConflictoException;
import cl.duoc.foro.usuarios.exception.RecursoNoEncontradoException;
import cl.duoc.foro.usuarios.model.Rol;
import cl.duoc.foro.usuarios.model.Usuario;
import cl.duoc.foro.usuarios.repository.RolRepository;
import cl.duoc.foro.usuarios.repository.UsuarioRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String ROL_POR_DEFECTO = "USUARIO";

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse crear(UsuarioCrearRequest datos) {
        if (usuarioRepository.existsByNombreUsuario(datos.nombreUsuario())) {
            throw new ConflictoException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.existsByEmail(datos.email())) {
            throw new ConflictoException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(datos.nombreUsuario());
        usuario.setEmail(datos.email());
        usuario.setPasswordHash(passwordEncoder.encode(datos.password()));
        usuario.setRol(buscarRol(ROL_POR_DEFECTO));

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado con id {}", guardado.getId());
        return UsuarioResponse.desde(guardado);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(String rol) {
        List<Usuario> usuarios = rol == null
                ? usuarioRepository.findAllByOrderByIdAsc()
                : usuarioRepository.findByRolNombreOrderByIdAsc(rol.toUpperCase(Locale.ROOT));
        return usuarios.stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscarUsuario(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioActualizarRequest datos) {
        Usuario usuario = buscarUsuario(id);
        if (usuarioRepository.existsByEmailAndIdNot(datos.email(), id)) {
            throw new ConflictoException("El email ya está registrado");
        }

        usuario.setEmail(datos.email());
        if (datos.password() != null) {
            usuario.setPasswordHash(passwordEncoder.encode(datos.password()));
        }
        log.info("Usuario {} actualizado", id);
        return UsuarioResponse.desde(usuario);
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, String nombreRol) {
        Usuario usuario = buscarUsuario(id);
        usuario.setRol(buscarRol(nombreRol.toUpperCase(Locale.ROOT)));
        log.info("Usuario {} cambiado al rol {}", id, usuario.getRol().getNombre());
        return UsuarioResponse.desde(usuario);
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscarUsuario(id);
        usuario.setActivo(activo);
        log.info("Usuario {} con estado activo={}", id, activo);
        return UsuarioResponse.desde(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(buscarUsuario(id));
        log.info("Usuario {} eliminado", id);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol " + nombre));
    }
}
