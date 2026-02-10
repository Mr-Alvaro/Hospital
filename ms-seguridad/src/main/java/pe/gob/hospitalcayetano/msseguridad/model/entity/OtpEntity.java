package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "OTP", schema = "SEGU")
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODIGO")
    private Integer codigo;

    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "CORREO")
    private String correo;

    @Column(name = "CODOTP")
    private Integer codOtp;

    @Column(name = "FECHCREA")
    private LocalDateTime fechCrea;

    @Column(name = "FECHEXP")
    private LocalDateTime fechExp;

    @Column(name = "USADO")
    private Boolean usado;
}
