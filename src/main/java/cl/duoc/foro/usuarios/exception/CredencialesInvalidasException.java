package cl.duoc.foro.usuarios.exception;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Usuario o clave incorrectos");
    }
}
