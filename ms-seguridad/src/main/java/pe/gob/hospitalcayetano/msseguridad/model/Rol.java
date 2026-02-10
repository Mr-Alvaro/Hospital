package pe.gob.hospitalcayetano.msseguridad.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rol {
    private String codigo;
    private String descripcion;
}
