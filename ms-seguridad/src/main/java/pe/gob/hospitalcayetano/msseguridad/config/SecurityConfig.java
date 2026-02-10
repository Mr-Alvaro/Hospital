package pe.gob.hospitalcayetano.msseguridad.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pe.gob.hospitalcayetano.msseguridad.filter.JwtAuthenticationFilter;
import pe.gob.hospitalcayetano.msseguridad.util.ConstanteUtil;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(req -> req
                        .requestMatchers(
                                ConstanteUtil.PATH_SEGURIDAD_LOGIN,
                                ConstanteUtil.PATH_SEGURIDAD_LOGIN_MOBILE,
                                ConstanteUtil.PATH_SEGURIDAD_VERIFICACION_2FA,
                                ConstanteUtil.PATH_ACTUATOR + "/**",
                                ConstanteUtil.PATH_SEGURIDAD_REFRESHTOKEN,
                                ConstanteUtil.PATH_SEGURIDAD_REFRESHTOKEN_MOBILE,
                                ConstanteUtil.PATH_SEGURIDAD_USUARIO,
                                ConstanteUtil.PATH_SEGURIDAD_USUARIO + "/**",
                                ConstanteUtil.PATH_SEGURIDAD_MENU,
                                ConstanteUtil.PATH_SEGURIDAD_MENU + "/**",
                                ConstanteUtil.PATH_SEGURIDAD_PERFIL,
                                ConstanteUtil.PATH_SEGURIDAD_PERFIL + "/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowCredentials(true);
        configuracion.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuracion.setAllowedHeaders(Collections.singletonList("*"));
        configuracion.setAllowedMethods(Collections.singletonList("*"));
        configuracion.addExposedHeader("*");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuracion);
        return source;
    }

}
