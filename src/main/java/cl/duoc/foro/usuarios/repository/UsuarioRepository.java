package cl.duoc.foro.usuarios.repository;

import cl.duoc.foro.usuarios.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    @EntityGraph(attributePaths = "rol")
    List<Usuario> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "rol")
    List<Usuario> findByRolNombreOrderByIdAsc(String nombreRol);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
