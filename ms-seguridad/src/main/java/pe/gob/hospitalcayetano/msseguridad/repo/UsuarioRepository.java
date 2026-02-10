package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.hospitalcayetano.msseguridad.model.IPermisoFrondProjection;
import pe.gob.hospitalcayetano.msseguridad.model.IValidarLoginCustomEntity;
import pe.gob.hospitalcayetano.msseguridad.model.entity.UsuarioEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, String> {

    @Query(value = "EXEC SIGEHOV2SEGU.SEGU.USP_LOGIN_SIGEHOV3 :nomusuario,'' ", nativeQuery = true)
    Optional<UsuarioEntity> selectUsuariobyNombre(@Param("nomusuario") String nomUsuario);

    @Query(value = "EXEC SIGEHOV2SEGU.SEGU.USP_USUARIO_SELECT :nroDocumento, :usuario ", nativeQuery = true)
    List<UsuarioEntity> selectUsuario(@Param("nroDocumento") String nroDocumento, @Param("usuario") String usuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE SIGEHOV2SEGU.[SEGU].[USUARSIS] " +
            "SET [PSWUSUAR] = :nroDocumento, " +
            " FLGUSUAR = 0 " +
            "WHERE CODUSUAR = :codigo ", nativeQuery = true)
    int limpiarClavePorUsuarioId(@Param("codigo") String codigo, @Param("nroDocumento") String nroDocumento);

    @Transactional
    @Modifying
    @Query(value = "UPDATE SIGEHOV2SEGU.[SEGU].[USUARSIS] " +
            "SET [PSWUSUAR] = :clave " +
            "WHERE CODUSUAR = :codigo ", nativeQuery = true)
    int cambiarClavePorUsuarioId(@Param("codigo") String codigo, @Param("clave") String clave);

    @Transactional
    @Modifying
    @Query(value = "UPDATE SIGEHOV2SEGU.[SEGU].[USUARSIS] " +
            " SET ESTUSUAR = 0, " +
            " AUCDUSCR= :usuarioReg, " +
            " AUPCIPCR= :equipoReg " +
            " WHERE CODUSUAR= :codigo", nativeQuery = true)
    int eliminarUsuario(@Param("codigo") String codigo, @Param("usuarioReg") String usuarioReg,
                            @Param("equipoReg") String equipoReg);

    @Transactional
    @Modifying
    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_USUARIO_UPDATE] :codigo,:codPerf,:estado,:usuarioReg,:equipoReg "
            , nativeQuery = true)
    int actualizarUsuario(@Param("codigo") String codigo, @Param("codPerf") String codPerf,
                              @Param("estado") String estado, @Param("usuarioReg") String usuarioReg,
                              @Param("equipoReg") String equipoReg);

    @Transactional
    @Modifying
    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_USUARIO_INSERT] '01','01','01','000',:codigo,:usuario,:clave,:codPersonal," +
            ":codPerfil,'000001',:usuarioReg,:equipoReg ", nativeQuery = true)
    int registrarUsuario(@Param("codigo") String codigo,
                             @Param("usuario") String usuario,
                             @Param("clave") String clave,
                             @Param("codPersonal") String codPersonal,
                             @Param("codPerfil") String codPerfil,
                             @Param("usuarioReg") String usuarioReg,
                             @Param("equipoReg") String equipoReg);

    @Transactional
    @Modifying
    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_TOKEN_INSERT_V3] :token,:refresh,:estado,:auFechCr ", nativeQuery = true)
    int registrarToken(@Param("token") String token,
                       @Param("refresh") String refresh,
                       @Param("estado") int estado,
                       @Param("auFechCr") Timestamp auFechCr);

    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_TOKEN_VALIDATE_V3] :token ", nativeQuery = true)
    Integer validarTokenDB(@Param("token") String token);

    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_LOGIN_MOBILE] :documento, :pswUsuario, :token", nativeQuery = true)
    List<IValidarLoginCustomEntity> validarLogin(
            @Param("documento") String documento,
            @Param("pswUsuario") String pswUsuario,
            @Param("token") String token
    );

    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_DATOS_X_CODPERSO] :codPerso", nativeQuery = true)
    IValidarLoginCustomEntity datosXCodPerso(
            @Param("codPerso") String codPerso
    );

    @Query(value = "EXEC SIGEHOV2SEGU.SEGU.USP_LISTAR_PERMISOS_X_PERFIL_FROND :codPerfil", nativeQuery = true)
    List<IPermisoFrondProjection> listarPermisosPorPerfil(@Param("codPerfil") String codPerfil);
}