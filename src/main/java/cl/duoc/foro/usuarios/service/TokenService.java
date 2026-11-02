package cl.duoc.foro.usuarios.service;

import cl.duoc.foro.usuarios.model.Usuario;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long minutosValidez;

    public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.minutos-validez}") long minutosValidez) {
        this.jwtEncoder = jwtEncoder;
        this.minutosValidez = minutosValidez;
    }

    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet datos = JwtClaimsSet.builder()
                .issuer("ms-usuarios")
                .subject(usuario.getNombreUsuario())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(minutosValidez, ChronoUnit.MINUTES))
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(cabecera, datos)).getTokenValue();
    }

    public long segundosValidez() {
        return minutosValidez * 60;
    }
}
