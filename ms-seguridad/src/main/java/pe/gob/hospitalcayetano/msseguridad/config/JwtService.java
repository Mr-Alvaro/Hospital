package pe.gob.hospitalcayetano.msseguridad.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pe.gob.hospitalcayetano.msseguridad.model.IValidarLoginCustomEntity;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PersonalEntity;
import pe.gob.hospitalcayetano.msseguridad.model.entity.UsuarioEntity;
import pe.gob.hospitalcayetano.msseguridad.util.ConstanteUtil;

import java.security.Key;
import java.time.ZoneId;
import java.util.*;

@RefreshScope
@Service
public class JwtService {

    @Value("${key_token}")
    private String privateKey;

    @Value("${jwt_token_duration_segundos}")
    private long tokenDuration;

    @Value("${jwt_refresh_token_duration_segundos}")
    private long refreshTokenDuration;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    public String generateTokenV2(IValidarLoginCustomEntity userDetails) {
        return generateTokenV2(new HashMap<>(), userDetails);
    }

    public String generateRefreshTokenV2(IValidarLoginCustomEntity userDetails) {
        return generateRefreshTokenV2(new HashMap<>(), userDetails);
    }

    private String generateToken(Map<String,Object> extraClaims, UserDetails userDetails){
        UsuarioEntity usuario = (UsuarioEntity) userDetails;
        PersonalEntity personal = usuario.getPersonal();

        extraClaims.put(ConstanteUtil.TOKEN_INF_CODPERSONAL, personal.getCodigo());
        extraClaims.put(ConstanteUtil.TOKEN_INF_TIPO, ConstanteUtil.TOKEN_TIPO_TOKEN);

        return Jwts.builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenDuration * 1000))
                .setId(UUID.randomUUID().toString())
                .signWith(getSignKey())
                .compact();
    }

    private String generateRefreshToken(Map<String,Object> extraClaims, UserDetails userDetails){
        UsuarioEntity usuario = (UsuarioEntity) userDetails;
        extraClaims.put(ConstanteUtil.TOKEN_INF_CODUSUARIO, usuario.getCodigo());
        extraClaims.put(ConstanteUtil.TOKEN_INF_TIPO, ConstanteUtil.TOKEN_TIPO_REFRESHTOKEN);

        return Jwts.builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenDuration * 1000))
                .signWith(getSignKey())
                .compact();
    }

    public Claims decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(privateKey.getBytes()));
    }

    private String generateTokenV2(Map<String,Object> extraClaims, IValidarLoginCustomEntity userDetails) {
        extraClaims.put(ConstanteUtil.TOKEN_INF_CODPERSO, userDetails.getCODPERSO());
        extraClaims.put(ConstanteUtil.TOKEN_INF_TIPO, ConstanteUtil.TOKEN_TIPO_TOKEN);

        return Jwts.builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getCODPERSO())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenDuration * 1000))
                .setId(UUID.randomUUID().toString())
                .signWith(getSignKey())
                .compact();
    }

    private String generateRefreshTokenV2(Map<String,Object> extraClaims, IValidarLoginCustomEntity userDetails) {
        extraClaims.put(ConstanteUtil.TOKEN_INF_CODPERSO, userDetails.getCODPERSO());
        extraClaims.put(ConstanteUtil.TOKEN_INF_TIPO, ConstanteUtil.TOKEN_TIPO_REFRESHTOKEN);

        return Jwts.builder()
                .addClaims(extraClaims)
                .setSubject(userDetails.getCODPERSO())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenDuration * 1000))
                .signWith(getSignKey())
                .compact();
    }

}
