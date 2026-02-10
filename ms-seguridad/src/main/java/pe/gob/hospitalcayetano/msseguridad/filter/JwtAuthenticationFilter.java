package pe.gob.hospitalcayetano.msseguridad.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pe.gob.hospitalcayetano.msseguridad.config.JwtService;
import pe.gob.hospitalcayetano.msseguridad.exception.Exception401;
import pe.gob.hospitalcayetano.msseguridad.util.ConstanteUtil;

import java.util.ArrayList;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {

            final String authHeader = request.getHeader("Authorization");
            final String jwt;

            if (authHeader == null || authHeader.isEmpty()) {
                if (request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_LOGIN ) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_LOGIN_MOBILE) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_VERIFICACION_2FA) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_ACTUATOR) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_REFRESHTOKEN) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_REFRESHTOKEN_MOBILE) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_DECODE_TOKEN) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_DECODE_TOKEN_MOBILE) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_USUARIO) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_MENU) ||
                        request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_PERFIL)) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    resolver.resolveException(request, response, null, new BadCredentialsException(""));
                    return;
                }
            }

            if (!authHeader.startsWith("Bearer ")) {
                resolver.resolveException(request, response, null, new BadCredentialsException(""));
                return;
            }

            jwt = authHeader.substring(7);

            Claims claims = jwtService.decodeToken(jwt);

            if (claims.get(ConstanteUtil.TOKEN_INF_TIPO, String.class).equals(ConstanteUtil.TOKEN_TIPO_REFRESHTOKEN) &&
                    !request.getRequestURI().contains(ConstanteUtil.PATH_SEGURIDAD_REFRESHTOKEN))
                throw new Exception401(ConstanteUtil.MENSAJE_ERROR_USO_INDEBIDO_REFRESHTOKEN);

            if (claims.getSubject() != null && !claims.getSubject().isEmpty() &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resolver.resolveException(request, response, null, new BadCredentialsException(""));
        }
    }
}