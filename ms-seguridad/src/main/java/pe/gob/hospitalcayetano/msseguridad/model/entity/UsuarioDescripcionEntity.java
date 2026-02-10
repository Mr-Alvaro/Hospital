package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarsis",schema="SEGU")
public class UsuarioDescripcionEntity {
    @Id
    @Column(name = "CODUSUAR")
    private String codigo;
    @Column(name = "NOMUSUAR")
    private String nomUsuario;
    @Column(name = "PSWUSUAR")
    private String password;
    @Column(name = "PERUSUAR")
    private String rolPredet;
    @Column(name = "NOMPERFIL")
    private String rolDescri;
    @Column(name = "CODEMPLE")
    private String codEmpleado;
    @Column(name = "ESTUSUAR")
    private String estado;
    @Column(name = "FLGUSUAR")
    private String flag;
    @Column(name = "APEPATER")
    private String apellidoParterno;
    @Column(name = "APEMATER")
    private String apellidoMarterno;
    @Column(name = "PRINOMBR")
    private String primerNombre;
    @Column(name = "SEGNOMBR")
    private String segundoNombre;
    @Column(name = "DETALLED")
    private String numeroDocumento;
    @Column(name = "SITUSUAR")
    private String situacion;
}
