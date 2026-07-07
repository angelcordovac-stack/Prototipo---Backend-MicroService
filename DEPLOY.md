# Despliegue — SoftCorp Backend (microservicios)

## Topología

```
Netlify (Angular)  ──►  Gateway (Render)  ──►  Identidad  (Render)
   BACKEND_URL              /usuarios/**        /mantenimiento/**  /api/tecnicos/**
   = URL gateway            /api/incidencias/** ──►  Incidencias (Render) ──► Identidad
                            /api/equipos/**     ──►  Equipos     (Render)
                                     │
                                     └──►  PostgreSQL / Supabase (compartida)
```

En Render se despliegan **4 web services**: `gateway`, `identidad`, `incidencias`, `equipos`.
**Eureka** y **Config Server** NO se despliegan en Render (el free tier aísla y duerme
los servicios, rompiendo el descubrimiento). Quedan en el repo para desarrollo local.

## Por qué así (free tier de Render)

- Cada servicio es un web service independiente con **URL pública propia** y sin red
  privada, por eso el enrutado va por URL absoluta (`IDENTIDAD_URI`, etc.), no por `lb://`.
- Cada servicio es **autosuficiente por variables de entorno**: no depende de que el
  Config Server esté despierto.
- Los servicios **se duermen a los 15 min** y tardan 30–60 s en despertar (cold start).
  La primera petición tras inactividad será lenta; por eso el cliente Incidencias→Identidad
  tiene timeout de lectura de 60 s.

## Variables de entorno (ver `.env.example`)

Compartidas por identidad/incidencias/equipos: `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `TOKEN_JWT_SECRET`
(la clave JWT debe ser **idéntica** en los tres).

Gateway: `IDENTIDAD_URI`, `INCIDENCIAS_URI`, `EQUIPOS_URI`, `CORS_ALLOWED_ORIGINS`.
Incidencias: además `IDENTIDAD_URI`.

## Pasos en Render (con `render.yaml`)

1. Sube el repo a GitHub y crea un **Blueprint** en Render apuntando al repo.
2. Rellena el grupo `softcorp-shared` (BD + `TOKEN_JWT_SECRET`).
3. Primer deploy → copia las URLs `*.onrender.com` de identidad/incidencias/equipos en
   las variables `*_URI` del gateway (y `IDENTIDAD_URI` en incidencias). Redeploy.
4. `CORS_ALLOWED_ORIGINS` (gateway) = URL de tu sitio Netlify.
5. En Netlify: `BACKEND_URL` = URL pública del gateway. Redeploy del frontend.

## Desarrollo local

Sin Eureka/Config (rápido): exporta las variables de `.env.example` y arranca cada
servicio; los `*_URI` usan `localhost` por defecto.

Con el stack completo (Config + Eureka): arranca en orden Config (8888) → Eureka (8761)
→ servicios → Gateway (8080), con `EUREKA_ENABLED=true`.

## Nota de base de datos

Los 3 servicios comparten una sola base y cada uno gestiona **solo sus tablas**. Con
`ddl-auto=update` no hay conflicto (cada Hibernate solo conoce sus entidades). En
producción conviene pasar a `validate`.
