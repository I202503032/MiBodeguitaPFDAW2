# inventario-service
Microservicio inicial de inventario para MiBodeguita, con Spring Boot 4.1.1 y Java 25.

## Configuración
Configurar variables de entorno `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. Por defecto conecta a `jdbc:mysql://localhost:3306/mibodegaapp` con usuario `root` y contraseña vacía.

`spring.jpa.hibernate.ddl-auto=validate` evita que este servicio altere automáticamente las tablas existentes. Antes de ejecutarlo, confirmar que la base de datos tenga las tablas `productos` y `movimientos_inventario` con las columnas esperadas.

Puerto: 8081.

## Endpoints
- GET `/api/productos`
- GET `/api/productos/{id}`
- POST `/api/productos`
- PUT `/api/productos/{id}`
- DELETE `/api/productos/{id}`
- POST `/api/productos/{id}/movimiento` con JSON `{ "tipo":"ENTRADA", "cantidad":5, "motivo":"Reposición", "usuarioId":1 }`
- GET `/api/productos/{id}/movimientos`

## Nota de migración
El servicio almacena `usuarioId` como identificador escalar para evitar una dependencia JPA directa con la entidad Usuario del monolito. La base de datos actual debe revisarse antes de ejecutar: las relaciones y nombres reales de columnas deben coincidir. El proyecto aún requiere pruebas de compilación y ejecución con el JDK 25 y Maven instalado.
