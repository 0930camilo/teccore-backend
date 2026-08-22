# TecCore Backend - Monolito Modular

Base profesional en Spring Boot 3 para una corporacion de cursos tecnicos con arquitectura en capas y soporte multiinstitucion.

## Stack

- Java 17
- Spring Boot 3.3.x
- Spring Web, Spring Data JPA, Spring Security
- JWT stateless
- MySQL + HikariCP
- MapStruct + Lombok
- Jakarta Validation
- Spring Mail
- OpenPDF
- Apache POI
- JUnit / Spring Boot Test

## Flujo

`HTTP -> Controller -> Service -> Repository -> DB`

`DB -> Repository -> Service -> Mapper/DTO -> JSON`

## Estructura principal

```text
com.corporacion.tecnica
├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
│   ├── impl
│   └── security
└── util
```

## Multiinstitucion

- `BaseInstitutionEntity` fuerza relacion con `Institucion` en entidades de negocio.
- `TenantHeaderFilter` lee `X-Institucion-Id`.
- JWT agrega `institutionId` en el token.
- `InstitutionScopeResolver` aplica aislamiento por institucion en servicios.

## Respuesta estandar

Todas las APIs retornan:

```json
{
  "success": true,
  "status": 200,
  "message": "...",
  "data": {}
}
```

## Endpoints base

- `POST /auth/login`
- `POST /auth/register`
- `GET/POST /instituciones`
- `GET/POST /docentes`
- `GET/POST /alumnos`
- `GET/POST /cursos`
- `GET/POST /materias`
- `GET/POST /notas`
- `GET/POST /actividades`
- `GET/POST /pagos`
- `GET /reportes/resumen-institucion`

## Ejecucion local

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Configura variables opcionales:

- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET`, `JWT_EXPIRATION_MS`
- `CORS_ALLOWED_ORIGINS`
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`

