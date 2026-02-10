package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.hospitalcayetano.msseguridad.model.entity.UsuarioDescripcionEntity;

import java.util.List;

@Repository
public interface UsuarioDescripcionRepository extends JpaRepository<UsuarioDescripcionEntity, String> {

    @Query(value = "SELECT  US.CODUSUAR, " +
            "US.NOMUSUAR, " +
            "US.NROVERSI, " +
            "US.PSWUSUAR, " +
            "US.PSWPUBLI, " +
            "US.SWCLAVEP, " +
            "US.NROPERIO, " +
            "US.CODEMPLE, " +
            "us.FLGUSUAR, " +
            "PE.APEPATER, " +
            "PE.APEMATER, " +
            "PE.PRINOMBR, " +
            "PE.SEGNOMBR, " +
            "DO.DETALLED, " +
            "DO.CODTIPOD, " +
            "PERUSUAR, " +
            "(SELECT NOMCORTO FROM SIGEHOV2SEGU.SEGU.TGEMAE01 WHERE CODTABLA='000001' AND CODREGIS=US.PERUSUAR)NOMPERFIL, " +
            "ESTUSUAR, " +
            "SITUSUAR, " +
            "TIPUSUAR, " +
            "PER.NROCOLEG " +
            "FROM SIGEHOV2SEGU.SEGU.USUARSIS US " +
            "INNER JOIN SIGEHOV2GENE.GENE.PERSONA  PE WITH (NOLOCK) ON PE.CODPERSO=US.CODEMPLE  " +
            "INNER JOIN SIGEHOV2GENE.GENE.DOCXPER  DO WITH (NOLOCK) ON US.CODEMPLE=DO.CODPERSO " +
            "LEFT JOIN SIGEHOV2GENE.GENE.PERSONAL PER WITH (NOLOCK) ON PER.CODPERSO=DO.CODPERSO " +
            "WHERE PE.ESTADORG='1' AND US.ESTUSUAR='1' AND PER.ESTADORG='1' " +
            "AND DO.ESTADOAD='1' AND US.NOMUSUAR like CONCAT('%',:username,'%') " +
            "ORDER BY US.NOMUSUAR", nativeQuery = true)
    List<UsuarioDescripcionEntity> listarUsuarios(@Param("username") String username);
}