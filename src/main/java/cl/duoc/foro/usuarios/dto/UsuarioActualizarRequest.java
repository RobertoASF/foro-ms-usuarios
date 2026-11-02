package cl.duoc.foro.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// El nombre de usuario no se cambia porque queda guardado en las publicaciones del foro.
// La clave es opcional: si no viene, se mantiene la actual.
public record UsuarioActualizarRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email,

        @Size(min = 8, max = 72, message = "La clave debe tener entre 8 y 72 caracteres")
        String password) {
}
