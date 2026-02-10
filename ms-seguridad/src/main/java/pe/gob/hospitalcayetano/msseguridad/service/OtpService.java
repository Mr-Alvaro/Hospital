package pe.gob.hospitalcayetano.msseguridad.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import pe.gob.hospitalcayetano.msseguridad.exception.Exception401;
import pe.gob.hospitalcayetano.msseguridad.model.entity.CorreoEntity;
import pe.gob.hospitalcayetano.msseguridad.model.entity.OtpEntity;
import pe.gob.hospitalcayetano.msseguridad.repo.CorreoRepository;
import pe.gob.hospitalcayetano.msseguridad.repo.OtpRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@RefreshScope
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final CorreoRepository correoRepository;

    @Value("${seguridadMfa.expiracionMin:10}")
    private int otpExpiracionMin;

    public void generarOtp(String usuario, String correo) {

        int otp = 100000 + new SecureRandom().nextInt(900000);

        OtpEntity entity = new OtpEntity();
        entity.setUsuario(usuario);
        entity.setCorreo(correo);
        entity.setCodOtp(otp);
        entity.setFechCrea(LocalDateTime.now());
        entity.setFechExp(LocalDateTime.now().plusMinutes(otpExpiracionMin));
        entity.setUsado(false);

        otpRepository.save(entity);

        String mensaje = """
            Saludos cordiales,

            Su código de verificación es: %d
            El código expira en %d minutos.

            Atentamente,
            Oficina de Comunicaciones HNCH
            """.formatted(otp, otpExpiracionMin);

        emailService.envioMensajePlano(
                java.util.List.of(correo),
                "Código de verificación",
                mensaje,
                true
        );
    }

    public String validarOtp(String correo, Integer otpIngresado) {
        OtpEntity otp = otpRepository
                .findTopByCorreoAndCodOtpAndUsadoFalseOrderByFechCreaDesc(
                        correo,
                        otpIngresado
                )
                .orElseThrow(() -> {
                    log.info("OTP no encontrado o no valido para el correo");
                    return new Exception401();
                });

        if (otp.getFechExp().isBefore(LocalDateTime.now())) {
            log.info("OTP vencido fechaExp = {}", otp.getFechExp());
            throw new Exception401();
        }

        otp.setUsado(true);
        otpRepository.save(otp);
        return otp.getUsuario();
    }

    public String obtenerCorreoPorCodPerso(String codPerso, boolean aplicaOtp) {
        Optional<CorreoEntity> correo = correoRepository.findFirstByCodPersoAndEstadoco(codPerso, "1");

        if (correo.isEmpty()) {
            if (aplicaOtp) {
                log.info("Correo no encontrado o no válido.");
                throw new Exception401();
            } else {
                return "";
            }
        }

        log.info("Correo encontrado.");
        return correo.get().getCorreo();
    }
}