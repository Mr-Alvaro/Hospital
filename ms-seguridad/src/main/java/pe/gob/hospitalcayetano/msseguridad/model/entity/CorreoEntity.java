package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "CORREOXP", schema = "GENE", catalog = "SIGEHOV2GENE")
public class CorreoEntity {

    @Id
    @Column(name = "CODCORXP")
    private String id;

    @Column(name = "CODPERSO")
    private String codPerso;

    @Column(name = "CORDESCR")
    private String correo;

    @Column(name = "ESTADOCO")
    private String estadoco;
}