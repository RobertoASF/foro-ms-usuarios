-- Datos iniciales. La clave de prueba de todos los usuarios es Foro2026*

SET DEFINE OFF

INSERT INTO roles (id, nombre, descripcion) VALUES (1, 'ADMIN', 'Administra usuarios, roles y categorías');
INSERT INTO roles (id, nombre, descripcion) VALUES (2, 'MODERADOR', 'Puede borrar temas y comentarios de otros usuarios');
INSERT INTO roles (id, nombre, descripcion) VALUES (3, 'USUARIO', 'Publica temas y comentarios en el foro');

INSERT INTO usuarios (nombre_usuario, email, password_hash, rol_id, fecha_creacion)
VALUES ('admin', 'admin@foro.cl', '$2a$10$9YA/DRR/z8m6.7MepS2WKe9l3Dj7.RJ0t3RoszAXYrdSfsJ1.0.lu', 1, TIMESTAMP '2026-10-01 09:00:00');
INSERT INTO usuarios (nombre_usuario, email, password_hash, rol_id, fecha_creacion)
VALUES ('moderador', 'moderador@foro.cl', '$2a$10$a5jIpZ095x/YRP6WtXPgt.MFRBaTxOXx7QPqTk94jqQCIZJY2s1fW', 2, TIMESTAMP '2026-10-02 09:30:00');
INSERT INTO usuarios (nombre_usuario, email, password_hash, rol_id, fecha_creacion)
VALUES ('camila', 'camila@foro.cl', '$2a$10$j9OfNusYq4rQ5oqdtXQoGeTVDsJ06rEBV7Gul9c.MHrC/vMKkba5C', 3, TIMESTAMP '2026-10-05 16:20:00');
INSERT INTO usuarios (nombre_usuario, email, password_hash, rol_id, fecha_creacion)
VALUES ('diego', 'diego@foro.cl', '$2a$10$OpNO/ncp6C0qKzmmzY0QOucIbMDT8Vk/MtyJfuhCi..bzILWVE.qu', 3, TIMESTAMP '2026-10-06 11:45:00');

COMMIT;
