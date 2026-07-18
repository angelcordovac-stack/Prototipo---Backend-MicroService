# SoftCorp — Sistema de gestión de incidencias técnicas (microservicios)

Migración de un monolito de mantenimiento técnico a una arquitectura de microservicios con
Spring Boot y Spring Cloud (Eureka, Config Server, Gateway, OpenFeign).

## Arquitectura

**Infraestructura (3):**
| Servicio | Puerto | Función |
|---|---|---|
| `config-server` | 8888 | Configuración centralizada de todos los microservicios |
| `eureka-server` | 8761 | Registro y descubrimiento de servicios |
| `gateway` | 8080 | Punto de entrada único, enruta a cada microservicio |

**Negocio (5):**
| Servicio | Puerto | Motor de BD | Dominio |
|---|---|---|---|
| `usuarios-service` | 8081 | PostgreSQL | Identidad, perfiles, técnicos, JWT |
| `equipos-service` | 8082 | MySQL | Maestro de equipos |
| `incidencias-service` | 8083 | SQL Server | Incidencias, repuestos, informes técnicos |
| `diccionario-service` | 8084 | PostgreSQL | Base de conocimiento de fallas comunes |
| `reportes-service` | 8085 | PostgreSQL | Monitoreo y reportes del área |

Todos los microservicios de negocio se comunican entre sí vía **Feign Client** a través de
**Eureka**. La autenticación usa **JWT** con el rol embebido como claim — cada microservicio
valida el token localmente (sin consultar la base de datos de `usuarios-service`) y las llamadas
Feign reenvían el `Authorization` del usuario original, para que la autorización por rol se
aplique con la identidad real de quien inició la petición, no con un token genérico de servicio.

## Requisitos previos

- Docker y Docker Compose.
- Para Kubernetes local: minikube o kind (ver `k8s/README.md`).
- Puertos libres: `8080-8084`, `8761`, `8888`, `5433-5434` (Postgres), `3306` (MySQL), `1433` (SQL Server).

## Despliegue con Docker Compose

```bash
docker compose up --build
```

Levanta, en orden: las 4 bases de datos → `config-server` → `eureka-server` → los 4
microservicios de negocio → `gateway`.

Verificar registro en Eureka: **http://localhost:8761**

### Usuario de prueba

La siembra inicial (`DataSeeder` en `usuarios-service`) crea:
- Perfiles: `Jefe`, `Tecnico`, `Sistemas`
- Usuario Jefe: `admin@softcorp.com` / `admin123`

```bash
curl -X POST http://localhost:8080/usuarios/login -H "Content-Type: application/json" \
  -d '{"correo":"admin@softcorp.com","password":"admin123"}'
```

El resto de las rutas del Gateway: `/usuarios`, `/tecnicos`, `/equipos`, `/incidencias`,
`/repuestos`, `/informes`, `/diccionario-fallas`, `/reportes`.

`ms-reportes` no tiene datos de negocio propios (solo un historial de auditoria de cuando se
genero cada reporte): agrega, vía Feign, lo que ya existe en `ms-incidencias`, `ms-usuarios` y
`ms-equipos` para ofrecer:
- `GET /reportes/dashboard` — panel general de incidencias (solo `JEFE`)
- `GET /reportes/tecnicos/rendimiento` — cuántas incidencias resuelve cada técnico (solo `JEFE`)
- `GET /reportes/incidencias/filtrar?estado=&desde=&hasta=&idTecnico=` — filtrado avanzado
- `GET /reportes/incidencias/{id}/detalle` — detalle completo (incidencia + equipo + técnico + repuestos + informes)
- `GET /reportes/historial` — auditoría de cuándo y quién generó cada reporte (solo `JEFE`)

### Detener y limpiar

```bash
docker compose down -v   # -v tambien borra los volumenes de datos
```

## Despliegue en Kubernetes local

Ver instrucciones detalladas en [`k8s/README.md`](k8s/README.md).

## Notas de diseño

- Cada microservicio de negocio tiene su propio `pom.xml`, `Dockerfile` y `application.yaml`, y
  no comparte carpetas de lógica con los demás — solo se comunican vía Feign.
- Los DTOs y clientes Feign se duplican localmente en cada servicio (no hay un JAR compartido),
  a propósito, para mantener el acoplamiento entre servicios lo más bajo posible.
- La siembra de datos usa un `CommandLineRunner` por servicio en vez de scripts SQL crudos, para
  funcionar igual sin importar el motor de base de datos configurado.
