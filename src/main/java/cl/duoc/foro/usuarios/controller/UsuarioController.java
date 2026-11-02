package cl.duoc.foro.usuarios.controller;

import cl.duoc.foro.usuarios.dto.CambioEstadoRequest;
import cl.duoc.foro.usuarios.dto.CambioRolRequest;
import cl.duoc.foro.usuarios.dto.MensajeResponse;
import cl.duoc.foro.usuarios.dto.UsuarioActualizarRequest;
import cl.duoc.foro.usuarios.dto.UsuarioCrearRequest;
import cl.duoc.foro.usuarios.dto.UsuarioResponse;
import cl.duoc.foro.usuarios.exception.AccionNoPermitidaException;
import cl.duoc.foro.usuarios.security.UsuarioAutenticado;
import cl.duoc.foro.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "${cors.origen}")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCrearRequest datos) {
        UsuarioResponse creado = usuarioService.crear(datos);
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.id())).body(creado);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> listar(@RequestParam(required = false) String rol) {
        return usuarioService.listar(rol);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        validarAcceso(id, jwt);
        return usuarioService.obtener(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioActualizarRequest datos,
            @AuthenticationPrincipal Jwt jwt) {
        validarAcceso(id, jwt);
        return usuarioService.actualizar(id, datos);
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse cambiarRol(@PathVariable Long id, @Valid @RequestBody CambioRolRequest datos) {
        return usuarioService.cambiarRol(id, datos.rol());
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest datos) {
        return usuarioService.cambiarEstado(id, datos.activo());
    }

    @DeleteMapping("/{id}")
    public MensajeResponse eliminar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        validarAcceso(id, jwt);
        usuarioService.eliminar(id);
        return new MensajeResponse("Usuario eliminado");
    }

    private void validarAcceso(Long id, Jwt jwt) {
        if (!UsuarioAutenticado.desde(jwt).puedeGestionar(id)) {
            throw new AccionNoPermitidaException("Solo puedes gestionar tu propia cuenta");
        }
    }
}
