package cl.duoc.foro.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record CambioRolRequest(
        @NotBlank(message = "El rol es obligatorio")
        String rol) {
}
