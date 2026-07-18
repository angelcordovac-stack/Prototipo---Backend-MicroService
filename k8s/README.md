# Despliegue local en Kubernetes (SoftCorp)

Estos manifiestos estan pensados para un cluster **local** (minikube o kind), no para la nube.
Usan `imagePullPolicy: IfNotPresent` porque las imagenes se construyen localmente con Docker y
no se suben a ningun registry.

## 1. Levantar un cluster local

```bash
minikube start
# o: kind create cluster --name softcorp
```

## 2. Construir las imagenes de cada microservicio

Con minikube, hay que construir las imagenes dentro del daemon de Docker del cluster para que
`imagePullPolicy: IfNotPresent` las encuentre sin necesidad de un registry:

```bash
eval $(minikube docker-env)   # apunta tu Docker CLI al daemon de minikube

docker build -t softcorp-config-server ./SoftCorp-MicroServiceConfig
docker build -t softcorp-eureka-server ./SoftCorp-MicroServiceEureka
docker build -t softcorp-gateway ./SoftCorp-MicroServiceGateway
docker build -t softcorp-usuarios-service ./SoftCorp-MicroServiceUsuarios
docker build -t softcorp-equipos-service ./SoftCorp-MicroServiceEquipos
docker build -t softcorp-incidencias-service ./SoftCorp-MicroServiceIncidencias
docker build -t softcorp-diccionario-service ./SoftCorp-MicroServiceDiccionarioFallas
```

Con kind, en su lugar hay que cargar las imagenes al cluster despues de construirlas:

```bash
kind load docker-image softcorp-config-server softcorp-eureka-server softcorp-gateway \
  softcorp-usuarios-service softcorp-equipos-service softcorp-incidencias-service \
  softcorp-diccionario-service --name softcorp
```

## 3. Aplicar los manifiestos

```bash
kubectl apply -f k8s/
```

Orden recomendado si prefieres aplicar por partes (los `initContainers` de cada Deployment ya
esperan a sus dependencias, pero ayuda no saturar el cluster de una vez):

1. PVCs y bases de datos (una por microservicio):
   - `postgres-usuarios` (PostgreSQL) — usada por `usuarios-service`
   - `mysql-equipos` (MySQL) — usada por `equipos-service`
   - `sqlserver-incidencias` (SQL Server) — usada por `incidencias-service`
   - `postgres-diccionario` (PostgreSQL) — usada por `diccionario-service`
2. `config-server`, luego `eureka-server`
3. `usuarios-service`, `equipos-service`
4. **`sqlserver-incidencias-init-job.yaml`** — SQL Server no crea bases de datos
   automáticamente a partir de variables de entorno (a diferencia de Postgres/MySQL).
   Este `Job` crea `incidenciasDb` una sola vez. Aplícalo y espera a que termine
   (`kubectl get jobs` hasta ver `COMPLETIONS 1/1`) antes de seguir:
   ```bash
   kubectl apply -f k8s/sqlserver-incidencias-init-job.yaml
   kubectl wait --for=condition=complete job/sqlserver-incidencias-init --timeout=120s
   ```
5. `incidencias-service`, `diccionario-service`
6. `gateway`

## 4. Acceder a los servicios

```bash
kubectl get pods -w                 # observar que todo llegue a Running/Ready
kubectl port-forward svc/gateway 8080:8080
```

Con el port-forward activo, el API Gateway queda disponible en `http://localhost:8080`, igual
que con `docker compose up`.

## Notas

- `sqlserver-incidencias` (SQL Server) puede tardar 30-60s en levantar; el `startupProbe` tiene
  un `failureThreshold` alto para darle tiempo antes de que Kubernetes lo reinicie.
- Cada base de datos tiene su propio `PersistentVolumeClaim`, por lo que los datos sobreviven a
  reinicios del Pod (no del cluster completo, salvo que tu proveedor de PV lo soporte).
- Las contrasenas y secretos van como variables de entorno en claro para simplificar el ejercicio
  academico. En un entorno real irian en `Secret` de Kubernetes, no en el manifiesto del Deployment.
