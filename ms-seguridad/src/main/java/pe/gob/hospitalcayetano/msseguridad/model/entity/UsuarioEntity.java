package pe.gob.hospitalcayetano.msseguridad.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pe.gob.hospitalcayetano.msseguridad.model.Rol;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuarsis",schema="SEGU")
public class UsuarioEntity implements UserDetails {
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
    @Transient
    private PersonalEntity personal;
    @Transient
    private List<Rol> lstRoles;
    @Column(name = "ESTUSUAR")
    private String estado;
    @Column(name = "FLGUSUAR")
    private int flag;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return nomUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
