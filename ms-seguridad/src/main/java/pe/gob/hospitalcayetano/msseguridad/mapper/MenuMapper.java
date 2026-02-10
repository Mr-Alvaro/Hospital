package pe.gob.hospitalcayetano.msseguridad.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pe.gob.hospitalcayetano.msseguridad.model.MenuData;
import pe.gob.hospitalcayetano.msseguridad.model.entity.MenuEntity;
import pe.gob.hospitalcayetano.msseguridad.model.entity.PerfilMenuEntity;

import java.util.Collections;
import java.util.List;

@Mapper
public interface MenuMapper {

    @Mapping(target = "label", source = "nombMenu")
    @Mapping(target = "orden", source = "ordenMenu")
    @Mapping(target = "relacion", source = "relaMenu")
    @Mapping(target = "icon", source = "iconMenu")
    @Mapping(target = "key", source = "codigo")
    MenuData menuEntityToMenu(MenuEntity menuEntity);

    List<MenuData> listObtenerDataMenuResponses(List<MenuEntity> menuEntityList);

    @Mapping(target = "label", source = "menu.nombMenu")
    @Mapping(target = "routerLink", expression = "java(convertStrinToStringList(perfilMenuEntity.getMenu().getLinkMenu()))")
    @Mapping(target = "orden", source = "menu.ordenMenu")
    @Mapping(target = "relacion", source = "menu.relaMenu")
    @Mapping(target = "icon", source = "menu.iconMenu")
    @Mapping(target = "key", source = "menu.codigo")
    MenuData perfilMenuEntityToMenuData(PerfilMenuEntity perfilMenuEntity);

    List<MenuData> listObtenerDataPerfilMenuResponses(List<PerfilMenuEntity> menuEntityList);

    @Named("convertStrinToStringList")
    default List<String> convertStrinToStringList(String data) {
        return Collections.singletonList(data);
    }
}