package cl.duoc.foro.usuarios.exception;

public class AccionNoPermitidaException extends RuntimeException {

    public AccionNoPermitidaException(String mensaje) {
        super(mensaje);
    }
}
