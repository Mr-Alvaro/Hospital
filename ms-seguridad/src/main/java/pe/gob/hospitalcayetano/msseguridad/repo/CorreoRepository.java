package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.gob.hospitalcayetano.msseguridad.model.entity.CorreoEntity;

import java.util.Optional;

@Repository
public interface CorreoRepository extends JpaRepository<CorreoEntity, String> {

    Optional<CorreoEntity> findFirstByCodPersoAndEstadoco(
            String codPerso,
            String estadoco
    );
}
