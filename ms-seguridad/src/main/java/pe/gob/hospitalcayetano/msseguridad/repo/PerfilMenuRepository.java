package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PerfilMenuEntity;

import java.util.List;

@Repository
public interface PerfilMenuRepository extends JpaRepository<PerfilMenuEntity, String> {

    @Query(value = "SELECT * FROM [SIGEHOV2SEGU].[SEGU].[PERFIL_MENU_V3] pm " +
            "WHERE pm.ESTADORG = '1' and pm.CODIPERF = :codPerfil", nativeQuery = true)
    List<PerfilMenuEntity> selectPefilMenu(@Param("codPerfil") String codPerfil);

    @Query(value = "SELECT RIGHT('00000' + ISNULL(CONVERT(VARCHAR,(MAX(pm.CODPERME)+1)),''), 6) CODREGIS " +
            "FROM [SIGEHOV2SEGU].[SEGU].[PERFIL_MENU_V3] pm " +
            "WHERE pm.ESTADORG = '1'", nativeQuery = true)
    String selectMaxCodigoRegistro();
}