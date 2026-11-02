package cl.duoc.foro.usuarios.security;

import cl.duoc.foro.usuarios.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

// Estos errores ocurren antes de llegar a los controladores, por eso no pasan por ManejadorErrores
@Component
@RequiredArgsConstructor
public class RespuestasSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException ex) throws IOException {
        escribir(request, response, HttpStatus.UNAUTHORIZED, "Debes iniciar sesión con un token válido");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException ex) throws IOException {
        escribir(request, response, HttpStatus.FORBIDDEN, "No tienes permisos para esta acción");
    }

    private void escribir(HttpServletRequest request, HttpServletResponse response, HttpStatus estado,
            String mensaje) throws IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        jsonMapper.writeValue(response.getWriter(), ErrorResponse.de(estado, mensaje, request.getRequestURI()));
    }
}
