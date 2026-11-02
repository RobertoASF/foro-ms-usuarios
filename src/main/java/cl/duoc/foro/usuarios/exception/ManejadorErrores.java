package cl.duoc.foro.usuarios.exception;

import cl.duoc.foro.usuarios.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Object> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<Object> conflicto(ConflictoException ex, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Object> credenciales(CredencialesInvalidasException ex, HttpServletRequest request) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccionNoPermitidaException.class)
    public ResponseEntity<Object> noPermitida(AccionNoPermitidaException ex, HttpServletRequest request) {
        return responder(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accesoDenegado(AccessDeniedException ex, HttpServletRequest request) {
        return responder(HttpStatus.FORBIDDEN, "No tienes permisos para esta acción", request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Operacion rechazada por la base de datos: {}", ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "Los datos entran en conflicto con registros existentes", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> inesperado(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno", request.getRequestURI());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> detalles = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            detalles.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse cuerpo = ErrorResponse.de(status, "Los datos enviados no son válidos", ruta(request), detalles);
        return ResponseEntity.status(status).headers(headers).body(cuerpo);
    }

    // Da el mismo formato JSON a los errores propios de Spring (404, 405, JSON mal formado, etc.)
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ErrorResponse cuerpo = ErrorResponse.de(status, mensajePara(status), ruta(request));
        return ResponseEntity.status(status).headers(headers).body(cuerpo);
    }

    private String mensajePara(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> "La solicitud no es válida";
            case 404 -> "El recurso solicitado no existe";
            case 405 -> "El método HTTP no está permitido en esta ruta";
            case 415 -> "El tipo de contenido no es compatible, usa application/json";
            default -> "No se pudo procesar la solicitud";
        };
    }

    private ResponseEntity<Object> responder(HttpStatus estado, String mensaje, String ruta) {
        return ResponseEntity.status(estado).body(ErrorResponse.de(estado, mensaje, ruta));
    }

    private String ruta(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
