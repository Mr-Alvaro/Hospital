package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PersonalEntity;

import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<PersonalEntity,String> {
    @Query(value = "EXEC SIGEHOV2GENE.GENE.USP_PERSONAL_SELECT :codPerso,''",nativeQuery = true)
    Optional<PersonalEntity> selectDatosUsuario(@Param("codPerso") String codPerso);

    @Query(value = "EXEC SIGEHOV2GENE.GENE.USP_PERSONAL_SELECT :codPerso, :nroDocumento ",nativeQuery = true)
    Optional<PersonalEntity> selectDatosUsuarioYDocumento(@Param("codPerso") String codPerso, @Param("nroDocumento") String nroDocumento);
}