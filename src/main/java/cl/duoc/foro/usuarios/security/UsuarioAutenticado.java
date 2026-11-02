package cl.duoc.foro.usuarios.security;

import org.springframework.security.oauth2.jwt.Jwt;

public record UsuarioAutenticado(Long id, String nombreUsuario, String rol) {

    public static UsuarioAutenticado desde(Jwt jwt) {
        Number id = jwt.getClaim("uid");
        return new UsuarioAutenticado(id.longValue(), jwt.getSubject(), jwt.getClaimAsString("rol"));
    }

    public boolean puedeGestionar(Long idUsuario) {
        return id.equals(idUsuario) || "ADMIN".equals(rol);
    }
}
