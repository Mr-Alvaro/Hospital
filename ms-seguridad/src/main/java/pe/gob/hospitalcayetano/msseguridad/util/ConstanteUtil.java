package pe.gob.hospitalcayetano.msseguridad.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstanteUtil {
    public static final String PATH_BASE_SEGURIDAD = "/api/v1/seguridades";

    public static final String PATH_SEGURIDAD_LOGIN = PATH_BASE_SEGURIDAD + "/login";
    public static final String PATH_SEGURIDAD_VERIFICACION_2FA = PATH_BASE_SEGURIDAD + "/verificacionMfa";
    public static final String PATH_SEGURIDAD_LOGIN_MOBILE = PATH_BASE_SEGURIDAD + "/loginMobile";
    public static final String PATH_SEGURIDAD_REFRESHTOKEN_MOBILE = PATH_BASE_SEGURIDAD + "/refreshTokenMobile";
    public static final String PATH_SEGURIDAD_REFRESHTOKEN = PATH_BASE_SEGURIDAD + "/refreshToken";
    public static final String PATH_SEGURIDAD_DECODE_TOKEN = PATH_BASE_SEGURIDAD + "/decodeToken";
    public static final String PATH_SEGURIDAD_DECODE_TOKEN_MOBILE = PATH_BASE_SEGURIDAD + "/decodeTokenMobile";
    public static final String PATH_SEGURIDAD_USUARIO = "/api/v1/seguridades/usuario";
    public static final String PATH_SEGURIDAD_MENU = "/api/v1/seguridades/menu";
    public static final String PATH_SEGURIDAD_PERFIL = "/api/v1/seguridades/perfil";
    public static final String PATH_ACTUATOR = "/actuator";
    public static final String TOKEN_INF_CODUSUARIO = "codUsuario";
    public static final String TOKEN_INF_CODPERSONAL = "codPersonal";
    public static final String TOKEN_INF_PRINOMBRE = "priNombre";
    public static final String TOKEN_INF_SEGNOMBRE = "segNombre";
    public static final String TOKEN_INF_APEPATERNO = "apePaterno";
    public static final String TOKEN_INF_APEMATERNO = "apeMaterno";
    public static final String TOKEN_INF_NROCOLEG = "nroColeg";
    public static final String TOKEN_INF_FECHANAC = "fechaNac";
    public static final String TOKEN_INF_ROLES = "roles";
    public static final String TOKEN_INF_TIPO = "tipo";
    public static final String TOKEN_TIPO_TOKEN = "token";
    public static final String TOKEN_TIPO_REFRESHTOKEN = "refresh-token";
    public static final String MENSAJE_ERROR_USO_INDEBIDO_REFRESHTOKEN = "El token de refresco no tiene acceso a la ruta.";
    public static final String STRING_6_CEROS_CODIGO = "000001";

    public static final String TOKEN_INF_CODREGIS      = "CODREGIS";
    public static final String TOKEN_INF_NUMECELU      = "NUMECELU";
    public static final String TOKEN_INF_CLAVE         = "CLAVE";
    public static final String TOKEN_INF_PERFILUS      = "PERFILUS";
    public static final String TOKEN_INF_PACIENTE      = "PACIENTE";
    public static final String TOKEN_INF_ESTADORG      = "ESTADORG";
    public static final String TOKEN_INF_CODPERSO      = "CODPERSO";
    public static final String TOKEN_INF_DNI           = "DNI";
    public static final String TOKEN_INF_TIPODOC       = "TIPODOC";
    public static final String TOKEN_INF_FECHANACI      = "FECHANAC";
    public static final String TOKEN_INF_EDAD          = "EDAD";
    public static final String TOKEN_INF_TG1SEXOP      = "TG1SEXOP";
    public static final String TOKEN_INF_SEXO          = "SEXO";
    public static final String TOKEN_INF_NOMBREPERFIL  = "NOMBREPERFIL";
    public static final String TOKEN_INF_TGSERVIC      = "TGSERVIC";
    public static final String TOKEN_INF_CODAMBIE      = "CODAMBIE";
    public static final String TOKEN_INF_UNIDAD        = "UNIDAD";

}
