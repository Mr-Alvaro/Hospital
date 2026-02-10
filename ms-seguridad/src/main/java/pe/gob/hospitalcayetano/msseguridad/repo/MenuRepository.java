package pe.gob.hospitalcayetano.msseguridad.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.gob.hospitalcayetano.msseguridad.model.entity.MenuEntity;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, String> {

    @Query(value = "SELECT * FROM SIGEHOV2SEGU.[SEGU].[MENU_V3] m WHERE m.ESTADORG = '1' ORDER BY ORDENMENU ASC", nativeQuery = true)
    List<MenuEntity> selectMenu();


}