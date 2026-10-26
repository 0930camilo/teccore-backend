# Body examples para la API TecCore

Este documento reúne ejemplos de JSON para los request bodies de los endpoints del backend. Los ejemplos se basan en las clases DTO del proyecto y en los payloads reales usados en los tests.

## 1) Auth

### POST /auth/login
```json
{
  "email": "admin@tec.com",
  "password": "123456"
}
```

### POST /auth/register
```json
{
  "nombre": "Ana Gómez",
  "email": "ana@tec.com",
  "password": "123456",
  "institucionId": 1,
  "sedeId": 2,
  "rol": "ADMIN_SEDE"
}
```

## 2) Institución

### POST /instituciones
```json
{
  "codigo": "TEC001",
  "nombre": "Tecnológica del Norte",
  "nit": "900123456-7",
  "correo": "contacto@tecnologica.edu.co",
  "telefono": "3001234567",
  "direccion": "Cra 10 # 20-30"
}
```

## 3) Sede

### POST /sedes
```json
{
  "nombre": "Sede Norte",
  "ciudad": "Bogotá",
  "direccion": "Calle 80 # 15-20",
  "institucionId": 1,
  "estado": "ACTIVO"
}
```

## 4) Programa

### POST /programas
```json
{
  "nombre": "Ingeniería de Sistemas",
  "duracionSemestres": 10,
  "nivel": "PREGRADO",
  "costoSemestral": 2500000.00,
  "estado": "ACTIVO",
  "sedeId": 1
}
```

## 5) Semestre

### POST /semestres
```json
{
  "nombre": "Semestre 1",
  "numero": 1,
  "anio": 2026,
  "estado": "ACTIVO",
  "programaId": 1,
  "institucionId": 1,
  "sedeId": 1
}
```

## 6) Materia

### POST /materias
```json
{
  "nombre": "Programación I",
  "intensidadHoraria": 4,
  "semestreId": 1,
  "institucionId": 1,
  "sedeId": 1,
  "docenteId": 2,
  "estado": "ACTIVO",
  "diaSemana": "LUNES",
  "horaInicio": "08:00",
  "horaFin": "10:00"
}
```

## 7) Docente

### POST /docentes
```json
{
  "nombres": "Carlos",
  "apellidos": "Pérez",
  "documento": "1045678901",
  "correo": "carlos.perez@tec.com",
  "cargaHorariaSemanal": 20,
  "institucionId": 1
}
```

## 8) Alumno

### POST /alumnos
```json
{
  "nombres": "María",
  "apellidos": "Rodríguez",
  "documento": "1012345678",
  "correo": "maria@tec.com",
  "telefono": "3214567890",
  "institucionId": 1,
  "sedeId": 1,
  "semestreId": 1,
  "materiaIds": [1, 2, 3]
}
```

## 9) Nota

### POST /notas
```json
{
  "alumnoId": 1,
  "materiaId": 2,
  "periodo": "2026-1",
  "valor": 4.5,
  "institucionId": 1
}
```

## 10) Pago

### POST /pagos
```json
{
  "alumnoId": 1,
  "valor": 500000.00,
  "concepto": "Matricula",
  "fechaPago": "2026-01-15",
  "institucionId": 1
}
```

## 11) Asistencia

### POST /asistencias
```json
{
  "alumnoId": 1,
  "materiaId": 2,
  "fecha": "2026-10-24",
  "estadoAsistencia": "PRESENTE",
  "observacion": "Asistió a tiempo",
  "institucionId": 1
}
```

## Nota importante
- Los enum values deben usarse exactamente como los define el backend, por ejemplo: `ACTIVO`, `INACTIVO`, `PRESENTE`, `LUNES`, `ADMIN_SEDE`.
- Los campos no obligatorios pueden omitirse del JSON.
- Los body de ejemplo coinciden con la validación actual de las clases `Request` del proyecto.
