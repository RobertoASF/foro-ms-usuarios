package cl.duoc.foro.usuarios.dto;

import cl.duoc.foro.usuarios.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombreUsuario,
        String email,
        String rol,
        boolean activo,
        LocalDateTime fechaCreacion) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.isActivo(),
                usuario.getFechaCreacion());
    }
}
