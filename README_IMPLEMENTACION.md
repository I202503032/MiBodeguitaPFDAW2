# MiBodeguita — instrucciones de ejecución de la versión de trabajo

## Contenido de esta versión
- Backend Spring Boot con Spring Data JPA, Spring MVC, Spring Security y validación.
- API de autenticación `POST /api/auth/login` con verificación BCrypt y respuesta sin contraseña.
- API de productos: `GET /api/productos`, `GET /api/productos/{id}`, `POST /api/productos`, `PUT /api/productos/{id}`, `DELETE /api/productos/{id}`.
- Prueba automatizada del repositorio para registrar, listar, actualizar y eliminar.
- Frontend Angular en la carpeta hermana `angular-mibodeguita`.

## Configurar conexión MySQL
Por seguridad, las credenciales ya no se guardan en `application.properties`. Define estas variables de entorno antes de iniciar Spring Boot:

- `DB_URL=jdbc:mysql://localhost:3306/mibodegaapp`
- `DB_USERNAME="usuario que usas en mysql`"
- `DB_PASSWORD=tu_contraseña_de_mysql`
- `FRONTEND_ORIGIN=http://localhost:4200`

La base de datos`mibodegaapp` debe existir xq Hibernate mantiene `ddl-auto=update`, igual que en el proyecto de origen.

## Ejecutar backend
Desde la carpeta del proyecto Java, en una terminal con Java y Maven disponibles:

```bash
./mvnw spring-boot:run
```

## Ejecutar frontend
En otra terminal:

```bash
cd angular-mibodeguita
npm install
npm start
```

Abrir `http://localhost:4200`.

## Endpoints
| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/auth/login` | Validar correo y contraseña; responde datos básicos del usuario, sin contraseña |
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Consultar un producto |
| POST | `/api/productos` | Registrar producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

El cuerpo de producto usa `nombre`, `descripcion`, `imagen`, `precio`, `cantidad` y opcionalmente `usuarioId`.

## Atención a las cuentas anteriores
Las nuevas contraseñas de registro se guardan con BCrypt. 
Para no bloquear cuentas antiguas, el login detecta temporalmente contraseñas antiguas en texto plano y,
si la contraseña coincide, actualiza ese registro a BCrypt. Las cuentas existentes deben iniciar sesión una vez para completar esa migración. 
