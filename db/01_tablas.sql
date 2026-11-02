-- Tablas del microservicio de usuarios.
-- Se ejecuta conectado como FORO_USUARIOS.

CREATE TABLE roles (
    id          NUMBER(2)          PRIMARY KEY,
    nombre      VARCHAR2(20 CHAR)  NOT NULL UNIQUE,
    descripcion VARCHAR2(200 CHAR) NOT NULL
);

CREATE TABLE usuarios (
    id             NUMBER(19)         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_usuario VARCHAR2(30 CHAR)  NOT NULL UNIQUE,
    email          VARCHAR2(120 CHAR) NOT NULL UNIQUE,
    password_hash  VARCHAR2(100 CHAR) NOT NULL,
    rol_id         NUMBER(2)          NOT NULL REFERENCES roles (id),
    activo         NUMBER(1)          DEFAULT 1 NOT NULL CHECK (activo IN (0, 1)),
    fecha_creacion TIMESTAMP          DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_usuarios_rol ON usuarios (rol_id);
