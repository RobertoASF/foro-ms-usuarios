package cl.duoc.foro.usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.type.NumericBooleanConverter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nombreUsuario;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // La columna es NUMBER(1) para que funcione igual en cualquier version de Oracle
    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private boolean activo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
