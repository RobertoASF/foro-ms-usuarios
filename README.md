# ms-usuarios

Microservicio del foro de TCG (juegos de cartas coleccionables) que se encarga de los usuarios, los roles y el inicio de sesión. Al iniciar sesión entrega un token JWT que también sirve para usar `ms-publicaciones`.

Hecho con Java 21, Spring Boot 4.1, Spring Data JPA, Spring Security y Oracle.

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Inicia sesión y entrega el token |
| POST | `/api/usuarios` | Público | Registra un usuario con rol USUARIO |
| GET | `/api/usuarios?rol=` | ADMIN | Lista usuarios, con filtro opcional por rol |
| GET | `/api/usuarios/{id}` | Dueño o ADMIN | Muestra una cuenta |
| PUT | `/api/usuarios/{id}` | Dueño o ADMIN | Cambia el email y, si viene, la clave |
| PUT | `/api/usuarios/{id}/rol` | ADMIN | Cambia el rol |
| PUT | `/api/usuarios/{id}/estado` | ADMIN | Activa o desactiva la cuenta |
| DELETE | `/api/usuarios/{id}` | Dueño o ADMIN | Elimina la cuenta |
| GET | `/api/roles` | ADMIN | Lista los roles con su total de usuarios |
| GET | `/actuator/health` | Público | Estado del servicio |

Las rutas protegidas piden el encabezado `Authorization: Bearer <token>`.

Ejemplo de inicio de sesión:

```json
{
  "nombreUsuario": "camila",
  "password": "Foro2026*"
}
```

Todos los errores responden con el mismo formato:

```json
{
  "fecha": "2026-11-02T10:15:30",
  "estado": 400,
  "mensaje": "Los datos enviados no son válidos",
  "ruta": "/api/usuarios",
  "detalles": {
    "email": "El email no tiene un formato válido"
  }
}
```

## Estructura

```
src/main/java/cl/duoc/foro/usuarios
├── config       Seguridad, JWT y cifrado de claves
├── controller   Endpoints REST
├── dto          Datos de entrada y salida, con sus validaciones
├── exception    Errores propios y manejo global
├── model        Entidades JPA
├── repository   Acceso a Oracle
├── security     Datos del usuario autenticado y respuestas 401 y 403
└── service      Reglas de negocio
```

## Base de datos

Los scripts están en la carpeta `db` y se ejecutan conectado como `FORO_USUARIOS`:

1. `01_tablas.sql` crea las tablas.
2. `02_datos.sql` carga los roles y los usuarios de prueba.

## Configuración

Está en `src/main/resources/application.properties`. La conexión a Oracle lee el Wallet desde la raíz del disco, en `C:\Wallet_dbfullstack3`. Las claves no van en ese archivo, se entregan con estas variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_PASSWORD` | Clave de la base de datos |
| `JWT_SECRETO` | Secreto para firmar los tokens, de al menos 32 caracteres |
| `CORS_ORIGEN` | Origen web permitido |
| `LOG_NIVEL` | Nivel de log del proyecto, por defecto `INFO` |

## Cómo ejecutarlo

El servicio se levanta junto a Oracle desde el repositorio `foro-infra`.

Las pruebas no necesitan base de datos:

```bash
mvn test
```
