package cl.duoc.foro.usuarios.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.foro.usuarios.config.SeguridadConfig;
import cl.duoc.foro.usuarios.security.RespuestasSeguridad;
import cl.duoc.foro.usuarios.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UsuarioController.class,
        properties = "jwt.secreto=clave-solo-para-pruebas-de-mas-de-32-caracteres")
@Import({SeguridadConfig.class, RespuestasSeguridad.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void listarSinTokenResponde401EnJson() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estado").value(401));
    }

    @Test
    void listarConRolUsuarioResponde403() throws Exception {
        mockMvc.perform(get("/api/usuarios").with(token(3L, "USUARIO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarConRolAdminResponde200() throws Exception {
        when(usuarioService.listar(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios").with(token(1L, "ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerOtraCuentaConRolUsuarioResponde403() throws Exception {
        mockMvc.perform(get("/api/usuarios/4").with(token(3L, "USUARIO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void registroConDatosInvalidosIndicaCadaCampo() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombreUsuario": "a", "email": "no-es-email", "password": "123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.nombreUsuario").exists())
                .andExpect(jsonPath("$.detalles.email").exists())
                .andExpect(jsonPath("$.detalles.password").exists());
    }

    private JwtRequestPostProcessor token(Long id, String rol) {
        return jwt().jwt(datos -> datos.subject("prueba").claim("uid", id).claim("rol", rol))
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol));
    }
}
