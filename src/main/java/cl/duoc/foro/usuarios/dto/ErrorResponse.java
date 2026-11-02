package cl.duoc.foro.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatusCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime fecha,
        int estado,
        String mensaje,
        String ruta,
        Map<String, String> detalles) {

    public static ErrorResponse de(HttpStatusCode estado, String mensaje, String ruta) {
        return new ErrorResponse(LocalDateTime.now(), estado.value(), mensaje, ruta, null);
    }

    public static ErrorResponse de(HttpStatusCode estado, String mensaje, String ruta, Map<String, String> detalles) {
        return new ErrorResponse(LocalDateTime.now(), estado.value(), mensaje, ruta, detalles);
    }
}
