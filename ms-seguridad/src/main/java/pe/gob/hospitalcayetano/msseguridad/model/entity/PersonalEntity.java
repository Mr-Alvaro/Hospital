package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "PERSONAL",schema="GENE")
public class PersonalEntity {
    @Id
    @Column(name = "CODPERSO")
    private String codigo;
    @Column(name = "PRINOMBR")
    private String primerNombre;
    @Column(name = "SEGNOMBR")
    private String segundoNombre;
    @Column(name = "APEPATER")
    private String apellidoPaterno;
    @Column(name = "APEMATER")
    private String apellidoMaterno;
    @Column(name = "FECHANAC")
    private LocalDate fechaNacimiento;
    @Column(name = "NROCOLEG")
    private String nroColegiatura;
    @Column(name = "CODEMPLE")
    private String codEmple;
    @Column(name = "NRODOCUM")
    private String nroDocumento;
}
