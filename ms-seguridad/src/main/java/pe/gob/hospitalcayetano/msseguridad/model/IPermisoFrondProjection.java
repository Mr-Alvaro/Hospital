package pe.gob.hospitalcayetano.msseguridad.model;

import java.time.LocalDateTime;

public interface IPermisoFrondProjection {
    String getCODPERMISO();
    String getNOMBRE_PERMISO();
    String getESTADORG();
    LocalDateTime getFECHA_ASIGNACION();
}

