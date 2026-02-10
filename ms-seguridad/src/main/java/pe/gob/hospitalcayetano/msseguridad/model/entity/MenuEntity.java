package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "MENU_V3",schema="SEGU")
public class MenuEntity implements Serializable {
    @Id
    @Column(name = "CODIMENU")
    private String codigo;
    @Column(name = "NOMBMENU")
    private String nombMenu;
    @Column(name = "LINKMENU")
    private String linkMenu;
    @Column(name = "ICONMENU")
    private String iconMenu;
    @Column(name = "RELAMENU")
    private String relaMenu;
    @Column(name = "ORDENMENU")
    private int ordenMenu;
    @Column(name = "ESTADORG")
    private int estado;
}
