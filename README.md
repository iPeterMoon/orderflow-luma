# OrderFlow — Student Starter

OrderFlow funciona localmente, pero deliberadamente NO contiene el delivery system del curso.

## ¿De qué trata el sistema?

OrderFlow simula el procesamiento de pedidos de una empresa. Su API permite registrar pedidos asociados con un cliente,
consultar los pedidos existentes y verificar el estado de salud del servicio. También incluye un componente de
notificaciones que, más adelante, se preparará para ejecutarse como una función AWS Lambda.

La funcionalidad de negocio inicial es deliberadamente pequeña porque el propósito del proyecto no es construir una
tienda completa. Durante el semestre, el equipo transformará la manera en que OrderFlow se integra, prueba, empaqueta,
entrega, despliega, aprovisiona y observa mediante prácticas DevOps reproducibles.

## Requisitos

- Java 21
- Maven 3.9+
- Git

SECRETS:
- SONAR_TOKEN (GitHub Actions)
- SONAR_HOST_URL (GitHub Actions)

Más adelante: Docker, AWS CLI, Terraform, Minikube, kubectl y Kompose.

## Baseline

```bash
mvn clean test
mvn package
mvn -pl orders-api spring-boot:run
```

Prueba:

```bash
curl http://localhost:8080/actuator/health
Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -ContentType "application/json" -Body '{"customerId":"team-demo","total":150.00}'
curl http://localhost:8080/api/orders
```

## Análisis de calidad (SonarQube)

El proyecto usa SonarQube Server en un servidor self-host en un equipo de uno de los integrantes, se utiliza para análisis estático y de cobertura con JaCoCo. Para correrlo localmente:

``` bash
mvn clean verify
mvn sonar:sonar -Dsonar.host.url=<SONAR_HOST_URL> -Dsonar.token=<SONAR_TOKEN>
```

El análisis en CI corre automáticamente vía GitHub Actions en cada Pull Request hacia  `main`/`develop`
(ver `.github/workflows/calidad.yml`).

## Evidencia acumulativa

No sobrescriban evidencias anteriores. Cada Sprint conserva su propio archivo:

```text
docs/evidence/
├── sprint-00.md
├── sprint-01.md
├── sprint-02.md
├── sprint-03.md
├── sprint-04.md
├── sprint-05.md
├── sprint-06.md
└── sprint-07.md
```

Usen PR, pipeline, deployment o infraestructura sólo cuando ya correspondan al Sprint. Antes de eso registren:
`N/A — todavía no corresponde a este Sprint.`

## Regla del semestre

No implementen por adelantado `.github/workflows`, `delivery`, `infra`, `k8s` u `observability`. Esas carpetas se
desarrollan progresivamente como evidencia de aprendizaje.
