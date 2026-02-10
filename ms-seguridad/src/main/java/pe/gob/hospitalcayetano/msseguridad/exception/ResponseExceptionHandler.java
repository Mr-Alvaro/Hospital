package pe.gob.hospitalcayetano.msseguridad.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pe.gob.hospitalcayetano.msseguridad.model.ApiDataResponse500;
import pe.gob.hospitalcayetano.msseguridad.model.ApiResponse500;
import pe.gob.hospitalcayetano.msseguridad.service.EmailService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@Slf4j
@ControllerAdvice
@RefreshScope
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private EmailService emailService;

    @Value("${notificacion.error.lista.destinatarios:alexolc95@gmail.com}")
    private List<String> listaTo;

    @Value("${notificacion.error.enable:true}")
    private Boolean enable;

    @Value("${spring.profiles.active:-}")
    private String environment;

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse401> handlerBadCredentialsException(HttpServletRequest req, Exception ex) {
        log.warn(ex.getMessage(), ex);

        ApiDataResponse401 apiDataResponse401 = new ApiDataResponse401();
        apiDataResponse401.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiDataResponse401.setMessage("Acceso no autorizado.");

        ApiResponse401 response = new ApiResponse401();
        response.setMetadata(apiDataResponse401);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiResponse401> handlerInternalAuthenticationServiceException(HttpServletRequest req, Exception ex) {
        log.warn(ex.getMessage(), ex);

        ApiDataResponse401 apiDataResponse401 = new ApiDataResponse401();
        apiDataResponse401.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiDataResponse401.setMessage("Acceso no autorizado.");

        ApiResponse401 response = new ApiResponse401();
        response.setMetadata(apiDataResponse401);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(Exception401.class)
    public ResponseEntity<ApiResponse401> exception401(HttpServletRequest req, Exception ex) {
        log.warn(ex.getMessage(), ex);

        ApiDataResponse401 apiDataResponse401 = new ApiDataResponse401();
        apiDataResponse401.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiDataResponse401.setMessage("Acceso no autorizado.");

        ApiResponse401 response = new ApiResponse401();
        response.setMetadata(apiDataResponse401);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse500> handlerGenericError(HttpServletRequest req, Exception ex) {
        log.error(ex.getMessage(), ex);

        emailService.envioMensajePlano(listaTo, "Error " + "(" + environment + ") " + ex.getMessage(), getStringFromError(ex), enable);

        ApiDataResponse500 apiDataResponse500 = new ApiDataResponse500();
        apiDataResponse500.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiDataResponse500.setMessage("Ocurrió un error no esperado, por favor comuníquese con el área de soporte.");

        ApiResponse500 response = new ApiResponse500();
        response.setMetadata(apiDataResponse500);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getMetadata().getStatus()));
    }

    public static String getStringFromError(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

}
