package pe.gob.hospitalcayetano.msseguridad.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.gob.hospitalcayetano.msseguridad.model.Metadata;

import java.lang.reflect.Field;

@Slf4j
public final class ResponseUtil {


    private ResponseUtil() {}

    public static <T> ResponseEntity<T> obtenerResultado(Object data, Class<T> classResponse) {
        return new ResponseEntity<>(
                obtenerContenido(data, classResponse),
                HttpStatus.OK
        );
    }

    public static <T> ResponseEntity<T> obtenerResultado(Class<T> classResponse) {
        return new ResponseEntity<>(
                obtenerContenido(classResponse),
                HttpStatus.OK
        );
    }

    @SneakyThrows
    private static <T> T obtenerContenido(Object data, Class<T> classResponse) {
        try {
            Metadata metadata = new Metadata();
            metadata.setStatus(HttpStatus.OK.value());
            metadata.setMessage("El proceso fue exitoso.");

            T classDestinoNuevo = classResponse.newInstance();

            Field fieldMetadata = classDestinoNuevo.getClass().getDeclaredField("metadata");
            fieldMetadata.setAccessible(true);
            fieldMetadata.set(classDestinoNuevo, metadata);

            Field fieldData = classDestinoNuevo.getClass().getDeclaredField("data");
            fieldData.setAccessible(true);
            fieldData.set(classDestinoNuevo, data);

            return classDestinoNuevo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @SneakyThrows
    private static <T> T obtenerContenido(Class<T> classResponse) {
        try {
            Metadata metadata = new Metadata();
            metadata.setStatus(HttpStatus.OK.value());
            metadata.setMessage("El proceso fue exitoso.");

            T classDestinoNuevo = classResponse.newInstance();

            Field fieldMetadata = classDestinoNuevo.getClass().getDeclaredField("metadata");
            fieldMetadata.setAccessible(true);
            fieldMetadata.set(classDestinoNuevo, metadata);

            return classDestinoNuevo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}
