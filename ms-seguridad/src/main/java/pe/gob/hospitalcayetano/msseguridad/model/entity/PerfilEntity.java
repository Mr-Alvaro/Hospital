package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TGEMAE01",schema="SEGU",catalog = "SIGEHOV2SEGU")
public class PerfilEntity implements Serializable {
    @Id
    @Column(name = "CODREGIS")
    private String codigo;
    @Column(name = "CODCATEG")
    private String codCateg;
    @Column(name = "CODTABLA")
    private String codTabla;
    @Column(name = "NOMCORTO")
    private String nomCorto;
    @Column(name = "NOMLARGO")
    private String nomLargo;
    @Column(name = "NROORDEN")
    private int nroOrden;
    @Column(name = "ESTADORG")
    private String estado;
    @Column(name = "SWMIGRAD")
    private int swMigrad;
    @Column(name = "AUFECHCR")
    private LocalDateTime auFecCr;
}