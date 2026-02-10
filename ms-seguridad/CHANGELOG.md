## ms-seguridad

## [Versión 1.0.10] - 2026-01-12
### Agregado
- Se agrego el campo nro doc al decode token.

## [Versión 1.0.9] - 2025-12-30
### Modificado
- Corrección de validación para obtener correo para otp.

## [Versión 1.0.8] - 2025-12-30
### Modificado
- Validación y forma de obtener correo para validar otp.

## [Versión 1.0.7] - 2025-12-30
### Agregado
- Se agrego el endpoint verificacionMfa al servicio.
- Se un elemento mas al responde de login que es el mskey.
- Se agrego el endpoint refresh al contrato.
- Se hizo una mejora en el pipeline para desplegar en el commit.
### Modificado
- Se cambio el nombre del response de verificacionMfa en ms-seguridad.
- Se modifico el pathbase en constanteUtil

## [Versión 1.0.6] - 2025-10-24
### Agregado
- Se agrego validacion y registro del token hacia la base de datos.
- La zona horaria de la fecha de caducidad del token hacia la base de datos.
- Se agrego imagen mas ligera al dockerfile.
- Se agrego pipeline para sonarQube y dependencia.
- Se agrego loginMobile al servicio.
- Se agrego listarDatosUsuarioMobile al servicio.
- Se agrego refrescarTokenMobile al servicio.
- Se agregaron las rutas de loginMobile y refrescarTokenMobile al config de security.
- Se agregaron validaciones para usuarios con perfiles sgd.
- Se agregaron lista de permisos en el servicio.
- Se agrego token de anulacion en producción.
- Se agrego cambios en el pipeline para despliegue para producción.
### Modificado
- Se modifico conexion con el servidor mediante ssh en pipeline.

## [Versión 1.0.5] - 2025-09-01
### Agregado
- Se agrego una política de reinicio para los contenedores.

## [Versión 1.0.4] - 2025-07-10
### Corregido
- Silenciado log de advertencia `TLSv1` del driver SQL Server desde `logback-spring.xml`.

---

### Note

Types of exchanges:

- *Agregado*: Nuevas funcionalidades.
- *Cambiado*: Cambios en funcionalidades.
- *Deprecado*: Funcionalidades obsoletas.
- *Removido*: Funcionalidades eliminadas.
- *Corregido*: Correciones.
- *Asegurado*: vulnerabilidades.
