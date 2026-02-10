package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "PERFIL_MENU_V3",schema="SEGU")
public class PerfilMenuEntity implements Serializable {
    @Id
    @Column(name = "CODPERME")
    private String codPerMe;
    @Column(name = "CODIPERF")
    private String codiPerf;
    @JoinColumn(name = "CODIMENU")
    @OneToOne(optional = false)
    private MenuEntity menu;
    @Column(name = "ESTADORG")
    private int estado;
}
