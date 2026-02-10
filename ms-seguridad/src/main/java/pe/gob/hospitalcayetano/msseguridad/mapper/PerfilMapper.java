package pe.gob.hospitalcayetano.msseguridad.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.hospitalcayetano.msseguridad.model.dto.PerfilData;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PerfilEntity;

import java.util.List;

@Mapper
public interface PerfilMapper {

    @Mapping(target="descripcionCorta", source="nomCorto")
    @Mapping(target="descripcionLarga", source="nomLargo")
    @Mapping(target="orden", source="nroOrden")
    PerfilData perfilEntityToPerfilData(PerfilEntity perfilEntity);

    List<PerfilData> listObtenerDataPerfilResponses(List<PerfilEntity> perfilEntities);
}
