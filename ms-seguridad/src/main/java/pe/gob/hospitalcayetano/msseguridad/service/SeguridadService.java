package pe.gob.hospitalcayetano.msseguridad.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import pe.gob.hospitalcayetano.msseguridad.SeguridadesApiDelegate;
import pe.gob.hospitalcayetano.msseguridad.config.JwtService;
import pe.gob.hospitalcayetano.msseguridad.config.SIGEHOPasswordEncoder;
import pe.gob.hospitalcayetano.msseguridad.exception.Exception401;
import pe.gob.hospitalcayetano.msseguridad.mapper.MenuMapper;
import pe.gob.hospitalcayetano.msseguridad.mapper.PerfilMapper;
import pe.gob.hospitalcayetano.msseguridad.mapper.UsuarioMapper;
import pe.gob.hospitalcayetano.msseguridad.model.dto.PerfilData;
import pe.gob.hospitalcayetano.msseguridad.model.dto.LoginDataResponse;
import pe.gob.hospitalcayetano.msseguridad.model.dto.*;
import pe.gob.hospitalcayetano.msseguridad.model.entity.*;
import pe.gob.hospitalcayetano.msseguridad.repo.*;
import pe.gob.hospitalcayetano.msseguridad.util.ConstanteUtil;
import pe.gob.hospitalcayetano.msseguridad.util.ResponseUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class SeguridadService implements SeguridadesApiDelegate {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioDescripcionRepository usuarioDescripcionRepository;
    private final PersonalRepository personalRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MenuRepository menuRepository;
    private final PerfilMenuRepository perfilMenuRepository;
    private final PerfilRepository perfilRepository;
    private final MenuMapper menuMapper = Mappers.getMapper(MenuMapper.class);
    private final PerfilMapper perfilMapper = Mappers.getMapper(PerfilMapper.class);
    private final UsuarioMapper usuarioMapper = Mappers.getMapper(UsuarioMapper.class);
    private final SIGEHOPasswordEncoder sigehoPasswordEncoder;
    private final OtpService otpService;

    @Value("${seguridadMfa.roles:#{T(java.util.Collections).emptyList()}}")
    private List<String> rolesAplicaOtp;

    @Value("${ms.x-ms-key}")
    private String mskey;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsuario(), request.getPassword()));
        UsuarioEntity usuario = selectUsuariobyNombre(request.getUsuario()).orElse(null);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        boolean requiereOtp = rolesAplicaOtp.contains(usuario.getRolPredet());

        if(requiereOtp){

            LoginDataResponse data = loginConOtp(usuario);

            return ResponseUtil.obtenerResultado(data, LoginResponse.class);
        }else{

            LoginDataResponse loginDataResponse = loginSinOtp(usuario);

            return ResponseUtil.obtenerResultado(loginDataResponse, LoginResponse.class);
        }
    }

    @Override
    public ResponseEntity<VerificacionMfaResponse> verificacionMfa(
            VerificacionMfaRequest request) {
        String usuarioPreAuth = otpService.validarOtp(request.getCorreo(), request.getCodigo());

        UsuarioEntity usuario = selectUsuariobyNombre(usuarioPreAuth)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        Claims claims = jwtService.decodeToken(token);
        guardarTokenEnDBAsync(token, refreshToken, claims);

        ListarDatosUsuarioResponse datosUsuarioResponse = listarDatosUsuario(token).getBody();

        VerificacionMfaDataResponse data = new VerificacionMfaDataResponse();
        data.setToken(token);
        data.setRefreshToken(refreshToken);
        data.setExp(datosUsuarioResponse.getData().getExp());
        data.setIat(datosUsuarioResponse.getData().getIat());
        data.setJti(datosUsuarioResponse.getData().getJti());

        return ResponseUtil.obtenerResultado(data, VerificacionMfaResponse.class);
    }

    @Override
    public ResponseEntity<ListarDatosUsuarioResponse> listarDatosUsuario(String token) {
        try {
            Claims claims = jwtService.decodeToken(token);
            String codPerso = claims.get(ConstanteUtil.TOKEN_INF_CODPERSONAL, String.class);
            IValidarLoginCustomEntity datosXCodPerso = usuarioRepository.datosXCodPerso(codPerso);
            PersonalEntity datosPersonal = personalRepository.selectDatosUsuario(codPerso).orElse(null);
            String correo = otpService.obtenerCorreoPorCodPerso(codPerso, false);

            ListarDatosUsuarioDataResponse dataResponse = new ListarDatosUsuarioDataResponse();
            dataResponse.setCodUsuario(datosXCodPerso.getCODREGIS());
            dataResponse.setCodPersonal(safeString(datosPersonal.getCodigo()));
            dataResponse.setPriNombre(safeString(datosPersonal.getPrimerNombre()));
            dataResponse.setSegNombre(safeString(datosPersonal.getSegundoNombre()));
            dataResponse.setApePaterno(safeString(datosPersonal.getApellidoPaterno()));
            dataResponse.setApeMaterno(safeString(datosPersonal.getApellidoMaterno()));
            dataResponse.setNroColeg(safeString(datosPersonal.getNroColegiatura()));
            dataResponse.setSub(claims.get("sub", String.class));
            dataResponse.setNroDocumento(safeString(datosPersonal.getNroDocumento()));

            if (datosPersonal.getFechaNacimiento() != null) {
                Date fechaNacDate = Date.from(
                        datosPersonal.getFechaNacimiento()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                );
                dataResponse.setFechaNac((int) (fechaNacDate.getTime() / 1000));
            }

            List<ListarRolesDataResponse> rolesList = new ArrayList<>();
            ListarRolesDataResponse role = new ListarRolesDataResponse();
            String codigoRol = safeString(datosXCodPerso.getPERFILUS());
            role.setCodigo(codigoRol);
            role.setDescripcion(safeString(datosXCodPerso.getNOMBREPERFIL()));
            List<String> permisos = usuarioRepository.listarPermisosPorPerfil(codigoRol)
                    .stream()
                    .map(IPermisoFrondProjection::getNOMBRE_PERMISO)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            role.setPermisos(permisos);
            rolesList.add(role);

            dataResponse.setCorreo(correo);
            dataResponse.setRoles(rolesList);
            dataResponse.setExp(claims.get("exp", Integer.class));
            dataResponse.setIat(claims.get("iat", Integer.class));
            dataResponse.setJti(claims.getId());

            validarTokenAutorizado(token);

            return ResponseUtil.obtenerResultado(dataResponse, ListarDatosUsuarioResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new Exception401();
        }
    }

    @Override
    public ResponseEntity<RefreshTokenResponse> refrescarToken(RefreshTokenRequest request) {
        try {
            Claims claims = jwtService.decodeToken(request.getToken());

            if (!claims.get(ConstanteUtil.TOKEN_INF_TIPO, String.class).equals(ConstanteUtil.TOKEN_TIPO_REFRESHTOKEN))
                throw new Exception401();

            UsuarioEntity usuario = selectUsuariobyNombre(claims.getSubject()).orElse(null);

            String token = jwtService.generateToken(usuario);
            String refreshToken = jwtService.generateRefreshToken(usuario);

            guardarTokenEnDBAsync(token, refreshToken, claims);

            RefreshTokenDataResponse dataResponse = new RefreshTokenDataResponse();
            dataResponse.setToken(token);
            dataResponse.setRefreshToken(refreshToken);
            dataResponse.setJti(listarDatosUsuario(token).getBody().getData().getJti());

            return ResponseUtil.obtenerResultado(dataResponse, RefreshTokenResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception401();
        }
    }

    public Optional<UsuarioEntity> selectUsuariobyNombre(String nomUsuario) {
        UsuarioEntity usr = usuarioRepository.selectUsuariobyNombre(nomUsuario).orElse(null);

        if (usr != null) {
            log.info("Usuario encontrado: {}", nomUsuario);

            List<Rol> lstRol = new ArrayList<>();
            lstRol.add(Rol.builder().codigo(usr.getRolPredet()).descripcion(usr.getRolDescri()).build());
            usr.setLstRoles(lstRol);

            PersonalEntity datosPersonal = personalRepository.selectDatosUsuario(usr.getCodEmpleado()).orElse(null);

            if (datosPersonal != null) {
                usr.setPersonal(datosPersonal);
            }
        }

        return Optional.of(usr);
    }

    @Override
    public ResponseEntity<MenuListarResponse> listarMenu() {
        List<MenuEntity> menuEntityList = menuRepository.selectMenu();
        return ResponseUtil.obtenerResultado(menuMapper.listObtenerDataMenuResponses(menuEntityList),
                MenuListarResponse.class);
    }

    @Override
    public ResponseEntity<MenuResponse> registrarMenu(Menu body) {
        return SeguridadesApiDelegate.super.registrarMenu(body);
    }

    @Override
    public ResponseEntity<MenuListarResponse> listarMenuPerfil(String perfilId) {
        List<PerfilMenuEntity> menuEntityList = perfilMenuRepository.selectPefilMenu(perfilId);
        return ResponseUtil.obtenerResultado(menuMapper.listObtenerDataPerfilMenuResponses(menuEntityList),
                MenuListarResponse.class);
    }

    @Override
    public ResponseEntity<MenuPerfilResponse> asociarMenuPerfil(MenuPerfilRequest request) {

        List<PerfilMenuEntity> menuEntityList = perfilMenuRepository.selectPefilMenu(request.getCodigoPerfil());

        //se elimina los menus que ya no necesita
        List<PerfilMenuEntity> menusAEliminar = new ArrayList<>();
        if (!ObjectUtils.isEmpty(menuEntityList)) {
            menusAEliminar = menuEntityList.stream()
                    .filter(aObject -> !request.getIdMenus().contains(aObject.getMenu().getCodigo()))
                    .toList();

            menusAEliminar.forEach(x -> {
                x.setEstado(0);
                perfilMenuRepository.save(x);
            });
        }

        if (!ObjectUtils.isEmpty(menusAEliminar)) {
            menuEntityList = perfilMenuRepository.selectPefilMenu(request.getCodigoPerfil());
        }

        List<String> menusAAgregarConvert = new ArrayList<>();
        menuEntityList.forEach(e -> menusAAgregarConvert.add(e.getMenu().getCodigo()));

        List<String> menusAAgregarConvertTmp;
        menusAAgregarConvertTmp = request.getIdMenus().stream()
                .filter(aObject -> !menusAAgregarConvert.contains(aObject))
                .toList();

        if (!ObjectUtils.isEmpty(menusAAgregarConvertTmp)) {
            PerfilMenuEntity perfilMenuEntity = new PerfilMenuEntity();
            perfilMenuEntity.setCodiPerf(request.getCodigoPerfil());
            perfilMenuEntity.setEstado(1);
            if (!ObjectUtils.isEmpty(menusAAgregarConvert)) {
                //se asocia a menus nuevos - act
                menusAAgregarConvertTmp.forEach(x -> {
                    String codRegis = perfilMenuRepository.selectMaxCodigoRegistro();
                    perfilMenuEntity.setCodPerMe(!codRegis.equals("00000") ? codRegis : ConstanteUtil.STRING_6_CEROS_CODIGO);
                    perfilMenuEntity.setMenu(new MenuEntity());
                    perfilMenuEntity.getMenu().setCodigo(x);
                    perfilMenuRepository.save(perfilMenuEntity);
                });
            } else {
                //se asocia los menus nuevos
                request.getIdMenus().forEach(x -> {
                    String codRegis = perfilMenuRepository.selectMaxCodigoRegistro();
                    perfilMenuEntity.setCodPerMe(!codRegis.equals("00000") ? codRegis : ConstanteUtil.STRING_6_CEROS_CODIGO);
                    perfilMenuEntity.setMenu(new MenuEntity());
                    perfilMenuEntity.getMenu().setCodigo(x);
                    perfilMenuRepository.save(perfilMenuEntity);
                });
            }
        }

        MenuPerfilResponseData menuPerfilResponse = new MenuPerfilResponseData();
        menuPerfilResponse.setMsg(ObjectUtils.isEmpty(request.getIdMenus()) ? "0" : "1");
        return ResponseUtil.obtenerResultado(menuPerfilResponse, MenuPerfilResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioLimpiarClaveResponse> limpiarClavePorUsuarioId(String usuarioId, String codEmple) {
        AtomicBoolean sw = new AtomicBoolean(false);
        personalRepository.selectDatosUsuario(codEmple).ifPresent(x -> {
                    sw.set(true);
                    usuarioRepository.
                            limpiarClavePorUsuarioId(usuarioId, sigehoPasswordEncoder.encode(x.getNroDocumento()));
                }
        );
        UsuarioLimpiarClaveDataResponse usuarioLimpiarClaveDataResponse = new UsuarioLimpiarClaveDataResponse();
        usuarioLimpiarClaveDataResponse.setIndicador(sw.get());
        return ResponseUtil.obtenerResultado(usuarioLimpiarClaveDataResponse, UsuarioLimpiarClaveResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioListarResponse> listarUsuarios(String nombreUsuario, String estado) {
        List<UsuarioDescripcionEntity> usuarioList = usuarioDescripcionRepository.listarUsuarios(nombreUsuario);
        return ResponseUtil.obtenerResultado(usuarioMapper.listObtenerDataUsuarioResponses(usuarioList),
                UsuarioListarResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioResponse> obtenerDatosUsuario(UsuarioRequest request) {
        List<UsuarioEntity> usuarioList = usuarioRepository.selectUsuario(request.getPersonal().getNroDocumento(),
                request.getUsuario());
        UsuarioDataResponse dataResponse = new UsuarioDataResponse();

        if (usuarioList.size() == 1) {
            UsuarioEntity usuario = usuarioList.get(0);
            PersonalEntity datosPersonal = personalRepository.selectDatosUsuario(usuario.getCodEmpleado())
                    .orElse(null);
            dataResponse.setCodigo(usuario.getCodigo());
            dataResponse.setUsuario(usuario.getNomUsuario());
            dataResponse.setPerfil(new Perfil());
            dataResponse.getPerfil().setCodReg(usuario.getRolPredet());
            dataResponse.getPerfil().setNombreCorto(usuario.getRolDescri());
            dataResponse.setNumeroDocumento(datosPersonal.getNroDocumento());
            dataResponse.setEstado(usuario.getEstado());
            dataResponse.setFlag(BigDecimal.valueOf(usuario.getFlag()));
            dataResponse.setPersonal(new PersonalData());
            dataResponse.getPersonal().setCodigoEmple(usuario.getCodigo());
            dataResponse.getPersonal().setNombreCompleto(datosPersonal.getApellidoPaterno() + " " +
                    datosPersonal.getApellidoMaterno() + ", " +
                    datosPersonal.getPrimerNombre() + " " +
                    datosPersonal.getSegundoNombre());
            dataResponse.setUsuario(datosPersonal.getPrimerNombre() + "." + datosPersonal.getApellidoPaterno()
                    + datosPersonal.getApellidoMaterno().substring(0, 1));
        } else if (usuarioList.isEmpty()) {
            try {
                PersonalEntity datosPersonal = personalRepository.selectDatosUsuarioYDocumento("",
                        request.getPersonal().getNroDocumento()).orElse(null);
                dataResponse.setPersonal(new PersonalData());
                dataResponse.getPersonal().setCodigoEmple(datosPersonal.getCodigo());
                dataResponse.getPersonal().setNroDocumento(request.getPersonal().getNroDocumento());
                dataResponse.getPersonal().setNombreCompleto(datosPersonal.getApellidoPaterno() + " " +
                        datosPersonal.getApellidoMaterno() + ", " +
                        datosPersonal.getPrimerNombre() + " " +
                        datosPersonal.getSegundoNombre());
                dataResponse.setUsuario(datosPersonal.getPrimerNombre() + "." + datosPersonal.getApellidoPaterno()
                        + datosPersonal.getApellidoMaterno().substring(0, 1));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                dataResponse.setCodigo("");
                log.error("No existe personal con ése número de Documento..!");
            }
        }
        return ResponseUtil.obtenerResultado(dataResponse, UsuarioResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioResponse> actualizarUsuario(UsuarioRequest usuarioRequest) {
        usuarioRepository.actualizarUsuario(
                usuarioRequest.getCodigo(),
                usuarioRequest.getPerfil().getCodReg(),
                usuarioRequest.getEstado(), "", "");
        UsuarioDataResponse usuarioDataResponse = new UsuarioDataResponse();
        return ResponseUtil.obtenerResultado(usuarioDataResponse, UsuarioResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioDeleteResponse> eliminarUsuarioPorId(String usuarioId) {
        int res = usuarioRepository.eliminarUsuario(usuarioId, "", "");
        UsuarioDeleteDataResponse usuarioDeleteDataResponse = new UsuarioDeleteDataResponse();
        usuarioDeleteDataResponse.setIndicador(res == 1);
        return ResponseUtil.obtenerResultado(usuarioDeleteDataResponse, UsuarioDeleteResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioResponse> registrarUsuario(UsuarioRequest usuarioRequest) {
        usuarioRepository.registrarUsuario(
                usuarioRequest.getCodigo(),
                usuarioRequest.getUsuario(),
                sigehoPasswordEncoder.encode(usuarioRequest.getNumeroDocumento()),
                usuarioRequest.getPersonal().getCodigo(),
                usuarioRequest.getPerfil().getCodReg(),
                "", "");
        UsuarioDataResponse usuarioDataResponse = new UsuarioDataResponse();
        return ResponseUtil.obtenerResultado(usuarioDataResponse, UsuarioResponse.class);
    }

    @Override
    public ResponseEntity<PerfilListarResponse> listarPerfiles(String nombre) {
        List<PerfilEntity> perfilEntities = perfilRepository.selectPerfil(nombre);
        return ResponseUtil.obtenerResultado(perfilMapper.listObtenerDataPerfilResponses(perfilEntities),
                PerfilListarResponse.class);
    }

    @Override
    public ResponseEntity<PerfilDataResponse> registrarPerfil(PerfilRequest perfilRequest) {
        PerfilEntity perfilEntity = new PerfilEntity();
        perfilEntity.setCodigo(perfilRepository.selectMaxCodPerfil());
        perfilEntity.setCodTabla(ConstanteUtil.STRING_6_CEROS_CODIGO);
        perfilEntity.setCodCateg(ConstanteUtil.STRING_6_CEROS_CODIGO);
        perfilEntity.setNomCorto(perfilRequest.getDescripcionCorta());
        perfilEntity.setNomLargo("");
        perfilEntity.setNroOrden(perfilRepository.selectMaxOrdenPerfil());
        perfilEntity.setEstado(perfilRequest.getEstado());
        perfilEntity.setSwMigrad(0);
        perfilEntity.setAuFecCr(LocalDateTime.now());
        PerfilData perfilResponse = perfilMapper.perfilEntityToPerfilData(perfilRepository.save(perfilEntity));
        return ResponseUtil.obtenerResultado(perfilResponse, PerfilDataResponse.class);
    }

    @Override
    public ResponseEntity<PerfilDataResponse> actualizarPerfilPorId(String perfilId, PerfilRequest perfilRequest) {
        PerfilEntity perfilEntity = new PerfilEntity();
        perfilEntity.setCodigo(perfilId);
        perfilEntity.setCodTabla(ConstanteUtil.STRING_6_CEROS_CODIGO);
        perfilEntity.setCodCateg(ConstanteUtil.STRING_6_CEROS_CODIGO);
        perfilEntity.setNomCorto(perfilRequest.getDescripcionCorta());
        perfilEntity.setNomLargo("");
        perfilEntity.setNroOrden(perfilRequest.getOrden().intValue());
        perfilEntity.setEstado(perfilRequest.getEstado());
        perfilEntity.setSwMigrad(0);
        perfilEntity.setAuFecCr(LocalDateTime.now());
        PerfilData perfilResponse = perfilMapper.perfilEntityToPerfilData(perfilRepository.save(perfilEntity));
        return ResponseUtil.obtenerResultado(perfilResponse, PerfilDataResponse.class);
    }

    @Override
    public ResponseEntity<PerfilDeleteResponse> eliminarPerfilPorId(String perfilId) {
        int res = perfilRepository.eliminarPerfilPorId(perfilId);
        PerfilDeleteDataResponse perfilDeleteDataResponse = new PerfilDeleteDataResponse();
        perfilDeleteDataResponse.setIndicador(res == 1);
        return ResponseUtil.obtenerResultado(perfilDeleteDataResponse, PerfilDeleteResponse.class);
    }

    @Override
    public ResponseEntity<UsuarioCambiarClaveResponse> cambiarClavePorUsuarioId(String usuarioId, UsuarioRequest usuarioRequest) {
        int res = usuarioRepository.
                cambiarClavePorUsuarioId(usuarioId, sigehoPasswordEncoder.encode(usuarioRequest.getClave()));
        UsuarioCambiarClaveDataResponse usuarioCambiarClaveResponse = new UsuarioCambiarClaveDataResponse();
        usuarioCambiarClaveResponse.setIndicador(res == 1);
        return ResponseUtil.obtenerResultado(usuarioCambiarClaveResponse, UsuarioCambiarClaveResponse.class);
    }

    @Override
    public ResponseEntity<ValidarLoginResponse> loginMobile(ValidarLoginRequest request) {
        List<IValidarLoginCustomEntity> validarLoginList = usuarioRepository.validarLogin(
                request.getDocumento(),
                request.getUsuario(),
                request.getToken()
        );

        if (validarLoginList.isEmpty()) {
            throw new Exception401("No se encontraron datos de login");
        }

        IValidarLoginCustomEntity loginData = validarLoginList.get(validarLoginList.size() - 1);

        String tokenU = jwtService.generateTokenV2(loginData);
        String refreshToken = jwtService.generateRefreshTokenV2(loginData);

        Claims claims = jwtService.decodeToken(tokenU);

        ObtenerValidarLoginDataResponse response = new ObtenerValidarLoginDataResponse();

        response.setToken(tokenU);
        response.setRefreshToken(refreshToken);
        response.setExp((int) claims.getExpiration().getTime());
        response.setIat((int) claims.getIssuedAt().getTime());
        response.setJti(claims.getId());

        return ResponseUtil.obtenerResultado(response, ValidarLoginResponse.class);
    }

    @Override
    public ResponseEntity<ListarDatosUsuarioMobileResponse> listarDatosUsuarioMobile(String token) {
        try {
            Claims claims = jwtService.decodeToken(token);

            String codPerso = claims.get(ConstanteUtil.TOKEN_INF_CODPERSO, String.class);

            IValidarLoginCustomEntity datosXCodPerso = usuarioRepository.datosXCodPerso(codPerso);

            ListarDatosUsuarioMobileDataResponse dataResponse = new ListarDatosUsuarioMobileDataResponse();
            dataResponse.setCodRegis(datosXCodPerso.getCODREGIS());
            dataResponse.setNumCel(datosXCodPerso.getNUMECELU());
            dataResponse.setPerfilUsu(datosXCodPerso.getPERFILUS());
            dataResponse.setPaciente(datosXCodPerso.getPACIENTE());
            dataResponse.setEstadoRg(datosXCodPerso.getESTADORG());
            dataResponse.setCodPerso(datosXCodPerso.getCODPERSO());
            dataResponse.setDni(datosXCodPerso.getDNI());
            dataResponse.setTipoDoc(datosXCodPerso.getTIPODOC());
            dataResponse.setFechaNac(datosXCodPerso.getFECHANAC());
            dataResponse.setEdad(datosXCodPerso.getEdad());
            dataResponse.setTipoSexo(datosXCodPerso.getTG1SEXOP());
            dataResponse.setSexo(datosXCodPerso.getSexo());
            dataResponse.setNombPerfil(datosXCodPerso.getNOMBREPERFIL());
            dataResponse.setServicio(datosXCodPerso.getTGSERVIC());
            dataResponse.setCodAmbie(datosXCodPerso.getCODAMBIE());
            dataResponse.setUnidad(datosXCodPerso.getUNIDAD());
            dataResponse.setExp(claims.get("exp", Integer.class));
            dataResponse.setIat(claims.get("iat", Integer.class));
            dataResponse.setJti(claims.getId());

            return ResponseUtil.obtenerResultado(dataResponse, ListarDatosUsuarioMobileResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            throw new Exception401();
        }
    }

    @Override
    public ResponseEntity<RefreshTokenMobileResponse> refrescarTokenMobile(RefreshTokenMobileRequest request) {
        try {
            Claims claims = jwtService.decodeToken(request.getToken());

            if (!claims.get(ConstanteUtil.TOKEN_INF_TIPO, String.class).equals(ConstanteUtil.TOKEN_TIPO_REFRESHTOKEN))
                throw new Exception401();

            String codPerso = claims.get(ConstanteUtil.TOKEN_INF_CODPERSO, String.class);

            IValidarLoginCustomEntity datosXCodPerso = usuarioRepository.datosXCodPerso(codPerso);

            String token = jwtService.generateTokenV2(datosXCodPerso);
            String refreshToken = jwtService.generateRefreshTokenV2(datosXCodPerso);

            guardarTokenEnDBAsync(token, refreshToken, claims);

            RefreshTokenMobileDataResponse dataResponse = new RefreshTokenMobileDataResponse();
            dataResponse.setToken(token);
            dataResponse.setRefreshToken(refreshToken);
            dataResponse.setJti(listarDatosUsuarioMobile(token).getBody().getData().getJti());

            return ResponseUtil.obtenerResultado(dataResponse, RefreshTokenMobileResponse.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception401();
        }
    }

    @Override
    public ResponseEntity<ValidarPermisoUsuarioResponse> validarPermisoUsuario(String token, String contexPath) {
        try {
            validarTokenAutorizado(token);

            boolean tienePermiso = Optional.ofNullable(listarDatosUsuario(token).getBody())
                    .map(ListarDatosUsuarioResponse::getData)
                    .map(ListarDatosUsuarioDataResponse::getRoles)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(ListarRolesDataResponse::getCodigo)
                    .anyMatch(codigo -> perfilRepository.validarUsuario(codigo, contexPath));

            if (!tienePermiso) {
                throw new Exception401();
            }

            ValidarPermisoUsuarioDataResponse dataResponse = new ValidarPermisoUsuarioDataResponse();
            dataResponse.setPermisoUsuario(true);

            return ResponseUtil.obtenerResultado(
                    dataResponse,
                    ValidarPermisoUsuarioResponse.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception401();
        }
    }

    private void guardarTokenEnDBAsync(String token, String refreshToken, Claims claims) {
        try {
            Integer exp = claims.get("exp", Integer.class);
            LocalDateTime fechaCaducidad = Instant.ofEpochSecond(exp)
                    .atZone(ZoneId.of("America/Lima"))
                    .toLocalDateTime();

            usuarioRepository.registrarToken(token, refreshToken, 1, Timestamp.valueOf(fechaCaducidad));
        } catch (Exception e) {
            log.error("Error al guardar el token: {}", e.getMessage(), e);
        }
    }

    private boolean validarTokenEnBD(String token) {
        try {
            Integer valido = usuarioRepository.validarTokenDB(token);
            return valido > 0;
        } catch (Exception e) {
            log.error("Error validando token en BD {}", e.getMessage(), e);
            return false;
        }
    }

    private void validarTokenAutorizado(String token) {
        if (!validarTokenEnBD(token)) {
            throw new Exception401();
        }
    }

    private String safeString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : "";
    }

    private LoginDataResponse loginConOtp(UsuarioEntity usuario) {
        String codPerso = usuario.getCodEmpleado();
        String correo = otpService.obtenerCorreoPorCodPerso(codPerso, true);

        otpService.generarOtp(
                usuario.getNomUsuario(),
                correo
        );

        LoginDataResponse response = new LoginDataResponse();
        response.setToken("");
        response.setRefreshToken("");
        response.setExp(null);
        response.setIat(null);
        response.setJti(null);
        response.setCorreo(correo);
        response.setKeyMs(mskey);
        response.setVerMfaHabilitado(true);

        return response;
    }

    private LoginDataResponse loginSinOtp(UsuarioEntity usuario){
        String codPerso = usuario.getCodEmpleado();
        String correo = otpService.obtenerCorreoPorCodPerso(codPerso, false);

        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        Claims claims = jwtService.decodeToken(token);
        guardarTokenEnDBAsync(token, refreshToken, claims);

        ListarDatosUsuarioResponse datosUsuarioResponse = listarDatosUsuario(token).getBody();

        LoginDataResponse response = new LoginDataResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setCorreo(correo);
        response.setExp(datosUsuarioResponse.getData().getExp());
        response.setIat(datosUsuarioResponse.getData().getIat());
        response.setJti(datosUsuarioResponse.getData().getJti());
        response.setKeyMs(mskey);
        response.setVerMfaHabilitado(false);

        return response;
    }
}
