package cl.duoc.foro.usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class Rol {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;
}
