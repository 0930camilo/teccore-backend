# AGENTS — Guía rápida para agentes AI

Propósito: instrucciones concisas para agentes que deban entender o modificar este backend Spring Boot y trabajar de forma segura con su autenticación y modelo multi-institución.

Checklist rápido
- Entender el flujo: Controller -> Service -> Repository (ver `README.md` y paquetes bajo `com.corporacion.tecnica`).
- Autenticar vía `/auth/login` y usar `Authorization: Bearer <token>` en todas las llamadas protegidas.
- Para operaciones por institución, incluir `X-Institucion-Id` (y opcional `X-Sede-Id`) en la cabecera; el proyecto usa `TenantHeaderFilter` y `TenantContext`.

Arquitectura (resumen)
- Monolito modular Spring Boot con capas: `controller`, `service`, `repository`, `entity`, `dto`.
- Respuestas JSON estandarizadas con `ApiResponse` / `ApiResponseFactory` (ver `src/main/java/com/corporacion/tecnica/util/ApiResponseFactory.java`).
- Autenticación JWT sin estado: `JwtService`, `JwtAuthenticationFilter`, `SecurityConfig`.
- Multi-tenancy ligera: headers + token claims → `TenantContext` (thread-local) que usan servicios y consultas.

Comandos esenciales (Windows PowerShell)
- Compilar y testear: `.\mvnw.cmd clean test`
- Ejecutar localmente: `.\mvnw.cmd spring-boot:run`

Puntos de integración y archivos clave
- Seguridad y tenant
  - `src/main/java/com/corporacion/tecnica/config/SecurityConfig.java` — chain de filtros y rutas públicas (/auth/**).
  - `src/main/java/com/corporacion/tecnica/security/JwtService.java` — creación y lectura de claims (institutionId, sedeId).
  - `src/main/java/com/corporacion/tecnica/security/JwtAuthenticationFilter.java` — parsea token y establece `TenantContext`.
  - `src/main/java/com/corporacion/tecnica/security/TenantHeaderFilter.java` — lee `X-Institucion-Id` / `X-Sede-Id` antes de auth.
  - `src/main/java/com/corporacion/tecnica/util/TenantContext.java` — almacenamiento thread-local.

- Respuestas y DTOs
  - `src/main/java/com/corporacion/tecnica/util/ApiResponseFactory.java` — usar `ok()` / `created()` para consistencia.
  - DTOs en `src/main/java/com/corporacion/tecnica/dto` — controllers consumen DTOs; mappers están en `mapper`.

- Inicialización y supuestos DB
  - `src/main/java/com/corporacion/tecnica/config/DataInitializer.java` — CommandLineRunner que crea roles/ajusta índices; modificar con cuidado en entornos no-MySQL.

Patrones específicos del proyecto (observables)
- Todas las entidades multi-institución suelen extender `BaseInstitutionEntity` (ver `entity/BaseInstitutionEntity.java`). Mantener ese patrón para nuevas entidades.
- Repositorios usan nombres estándar Spring Data (ej. `UsuarioRepository.findByEmail`) — busque métodos `findBy` cuando implemente seguridad o búsqueda.
- Controllers devuelven `ApiResponse` y no retornan entidades JPA directamente; convierta a DTOs usando mappers existentes.

Ejemplo de flujo para agentes que llaman API
1) POST `/auth/login` con body JSON (`LoginRequest` DTO). Revise `AuthController` y `dto/auth` para campos.
2) Guardar `accessToken` de la respuesta (`AuthResponse`).
3) Llamadas protegidas: agregar cabeceras
   - `Authorization: Bearer <token>`
   - `X-Institucion-Id: <id>` (si la acción es por institución)
Ejemplo curl (Windows PowerShell-friendly):
  curl -H "Content-Type: application/json" -H "Authorization: Bearer <token>" -H "X-Institucion-Id: 5" "http://localhost:8080/api/usuarios"

Consejos para modificaciones y pruebas automatizadas
- Añada la ruta en `SecurityConfig` si crea endpoints públicos; siga la convención de `/auth/**` para auth pública.
- Use `ApiResponseFactory` para mantener clientes y tests coherentes.
- Extienda `DataInitializer` solo si necesita datos de arranque; envolver con comprobaciones de entorno para evitar ejecuciones no deseadas.

Dónde leer primero (orden recomendado)
1. `README.md` (visión general y comandos).
2. `src/main/java/com/corporacion/tecnica/config/SecurityConfig.java` (cómo se protege la app).
3. `src/main/java/com/corporacion/tecnica/security/JwtService.java` y filtros relacionados.
4. `src/main/java/com/corporacion/tecnica/util/ApiResponseFactory.java` y `TenantContext.java`.

Referencias rápidas
- `pom.xml` — build y plugin Spring Boot
- `application.properties` — JWT secret, CORS y otros valores configurables
- `src/main/java/com/corporacion/tecnica/config/DataInitializer.java`

Notas finales
- No se encontraron archivos de instrucciones de agentes (`AGENT.md` / `.github/copilot-instructions.md`) en la raíz antes de crear este archivo — este `AGENTS.md` documenta patrones detectables en el código fuente.

---
Archivo generado automáticamente por asistente. Actualice según cambios en la configuración de seguridad o modelo de datos.

