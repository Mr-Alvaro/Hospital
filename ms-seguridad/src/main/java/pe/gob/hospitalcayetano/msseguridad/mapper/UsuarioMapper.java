package pe.gob.hospitalcayetano.msseguridad.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.gob.hospitalcayetano.msseguridad.model.UsuarioDataResponse;
import pe.gob.hospitalcayetano.msseguridad.model.entity.UsuarioDescripcionEntity;

import java.util.List;

@Mapper
public interface UsuarioMapper {

    List<UsuarioDataResponse> listObtenerDataUsuarioResponses(List<UsuarioDescripcionEntity> usuarioList);

    @Mapping(target="usuario", source = "nomUsuario")
    @Mapping(target="personal.codigoEmple", source = "codEmpleado")
    @Mapping(target="personal.primerNombre", source = "primerNombre")
    @Mapping(target="personal.segundoNombre", source = "segundoNombre")
    @Mapping(target="personal.apellidoPaterno", source = "apellidoParterno")
    @Mapping(target="personal.apellidoMaterno", source = "apellidoMarterno")
    @Mapping(target="numeroDocumento", source = "numeroDocumento")
    @Mapping(target="perfil.codReg", source = "rolPredet")
    @Mapping(target="perfil.nombreCorto", source = "rolDescri")
    @Mapping(target="situacion.codReg", source = "situacion")
    UsuarioDataResponse usuarioToUsuarioDataResponse(UsuarioDescripcionEntity usuario);
}