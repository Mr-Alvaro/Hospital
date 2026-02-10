package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.gob.hospitalcayetano.msseguridad.model.entity.OtpEntity;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Integer> {

    Optional<OtpEntity>
    findTopByCorreoAndCodOtpAndUsadoFalseOrderByFechCreaDesc(
            String correo,
            Integer codOtp
    );
}