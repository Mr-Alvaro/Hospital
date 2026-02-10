package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PerfilEntity;
import pe.gob.hospitalcayetano.msseguridad.model.entity.UsuarioEntity;

import java.util.List;

@Repository
public interface PerfilRepository extends JpaRepository<PerfilEntity, String> {

    @Query(value = "SELECT * FROM [SIGEHOV2SEGU].[SEGU].[TGEMAE01] pf WHERE pf.ESTADORG = '1' " +
            "and pf.CODTABLA = '000001' and CODREGIS <> '@' and pf.NOMCORTO LIKE CONCAT('%', :nombre, '%') " +
            "ORDER BY NROORDEN ASC", nativeQuery = true)
    List<PerfilEntity> selectPerfil(@Param("nombre") String nombre);

    @Query(value = "SELECT RIGHT('00000' + ISNULL(CONVERT(VARCHAR,(MAX(pf.CODREGIS)+1)),''), 6) CODREGIS " +
            "FROM [SIGEHOV2SEGU].[SEGU].[TGEMAE01] pf " +
            "WHERE pf.CODTABLA = '000001' and CODREGIS <> '@'", nativeQuery = true)
    String selectMaxCodPerfil();

    @Query(value = "SELECT MAX(pf.NROORDEN)+1 NROORDEN FROM [SIGEHOV2SEGU].[SEGU].[TGEMAE01] pf " +
            "WHERE pf.CODTABLA = '000001' and CODREGIS <> '@'", nativeQuery = true)
    int selectMaxOrdenPerfil();

    @Transactional
    @Modifying
    @Query(value = "UPDATE [SIGEHOV2SEGU].[SEGU].[TGEMAE01] " +
            "SET ESTADORG = '0' " +
            "WHERE CODREGIS = :perfilId ", nativeQuery = true)
    int eliminarPerfilPorId(@Param("perfilId") String perfilId);

    @Query(value = "EXEC SIGEHOV2SEGU.[SEGU].[USP_VALIDAR_PERMISO_V3] :codPerfil, :nombreMs", nativeQuery = true)
    Boolean validarUsuario(@Param("codPerfil") String codPerfil, @Param("nombreMs") String nombreMs);

}