package pe.gob.hospitalcayetano.msseguridad.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import pe.gob.hospitalcayetano.msseguridad.model.dto.PerfilData;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PerfilEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-10T13:52:29-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.5 (Oracle Corporation)"
)
public class PerfilMapperImpl implements PerfilMapper {

    @Override
    public PerfilData perfilEntityToPerfilData(PerfilEntity perfilEntity) {
        if ( perfilEntity == null ) {
            return null;
        }

        PerfilData perfilData = new PerfilData();

        perfilData.setDescripcionCorta( perfilEntity.getNomCorto() );
        perfilData.setDescripcionLarga( perfilEntity.getNomLargo() );
        perfilData.setOrden( perfilEntity.getNroOrden() );

        return perfilData;
    }

    @Override
    public List<PerfilData> listObtenerDataPerfilResponses(List<PerfilEntity> perfilEntities) {
        if ( perfilEntities == null ) {
            return null;
        }

        List<PerfilData> list = new ArrayList<PerfilData>( perfilEntities.size() );
        for ( PerfilEntity perfilEntity : perfilEntities ) {
            list.add( perfilEntityToPerfilData( perfilEntity ) );
        }

        return list;
    }
}
