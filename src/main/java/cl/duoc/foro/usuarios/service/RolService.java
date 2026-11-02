package cl.duoc.foro.usuarios.service;

import cl.duoc.foro.usuarios.dto.RolResponse;
import cl.duoc.foro.usuarios.repository.RolRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<RolResponse> listar() {
        return rolRepository.listarConTotalUsuarios();
    }
}
