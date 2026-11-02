package cl.duoc.foro.usuarios.repository;

import cl.duoc.foro.usuarios.dto.RolResponse;
import cl.duoc.foro.usuarios.model.Rol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombre(String nombre);

    @Query("""
            SELECT new cl.duoc.foro.usuarios.dto.RolResponse(r.id, r.nombre, r.descripcion, COUNT(u))
            FROM Rol r LEFT JOIN Usuario u ON u.rol = r
            GROUP BY r.id, r.nombre, r.descripcion
            ORDER BY r.id
            """)
    List<RolResponse> listarConTotalUsuarios();
}
