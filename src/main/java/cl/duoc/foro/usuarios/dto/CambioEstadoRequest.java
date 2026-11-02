package cl.duoc.foro.usuarios.dto;

import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo) {
}
