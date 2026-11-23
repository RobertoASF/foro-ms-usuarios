package cl.duoc.foro.usuarios.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Reúne las reglas de la clave en un solo lugar. Son las mismas que valida el frontend.
@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Size(min = 8, max = 20, message = "La clave debe tener entre 8 y 20 caracteres")
@Pattern(regexp = ".*[A-Z].*", message = "La clave debe tener al menos una mayúscula")
@Pattern(regexp = ".*[a-z].*", message = "La clave debe tener al menos una minúscula")
@Pattern(regexp = ".*\\d.*", message = "La clave debe tener al menos un número")
@Pattern(regexp = ".*[^A-Za-z0-9\\s].*", message = "La clave debe tener al menos un carácter especial")
@Pattern(regexp = "\\S*", message = "La clave no puede tener espacios")
public @interface ClaveSegura {

    String message() default "La clave no cumple las reglas de seguridad";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
