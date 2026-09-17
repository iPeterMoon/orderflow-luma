## Identificación
**Equipo: Luma**

Integrantes:
- Juan Pablo Olivarría Covarrubias
- Alicia Denise García Acosta
- Norma Alicia Beltran Martin
- Pedro Luna Esquer

**Proyecto:** OrderFlow

**Repositorio:** https://github.com/iPeterMoon/orderflow-luma

**Commit de entrega:** 

## Flujo
**Evento (on):** Se dispara en pull_request hacia main o develop, y en push hacia main o develop especificando los tipos opened, synchronize y reopened.

**Runner:** ubuntu-latest.

**Steps:**

1. uses: actions/checkout@v6: Descarga el código en el runner desactivando shallow clones (fetch-depth: 0) para calcular métricas históricas en Sonar.

2. Set up JDK 21: Configura la versión 21 de Java con la distribución temurin usando actions/setup-java@v4.

3. Cache SonarQube packages: Guarda en caché la ruta ~/.sonar/cache con la clave ${{ runner.os }}-sonar.

4. Cache Maven packages: Almacena en caché el directorio ~/.m2 basándose en el hash de los pom.xml.

5. Build and analyze: Inyecta SONAR_TOKEN y SONAR_HOST_URL desde los secrets y ejecuta mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=orderflow-luma -Dsonar.projectName='Orderflow Luma'.

**Función de Sonar:** Inspecciona el código compilado  y la cobertura generada por JaCoCo para detectar fallos de calidad, vulnerabilidades y code smells antes del merge. Con `sonar.qualitygate.wait=true`, el propio step de análisis espera el resultado del Quality Gate y falla si este queda en rojo, en lugar de solo reportar los datos a SonarQube sin evaluar el resultado.

## Predicción
**Qué se esperaba que fallara:** Se esperaba que SonarQube detectara un bloque catch vacío, un Bug por posible desreferencia nula (param.toLowerCase()) y código muerto (método y parámetro sin uso).

**Evidencia anticipada:** En la interfaz de SonarQube se esperaba un Quality Gate en FAILED. En GitHub Actions se anticipaba que el paso final terminara en rojo (fallido).

## Observación

**Run:** https://github.com/iPeterMoon/orderflow-luma/actions/runs/35163703063

**Resultado en SonarQube:** Quality Gate en `FAILED`. Condiciones incumplidas: se detectaron los 3 issues esperados (bloque `catch` vacío, posible desreferencia nula en `param.toLowerCase()`, método y parámetro sin uso).

**Resultado en GitHub Actions:**  El step terminó en verde, a pesar de que el Quality Gate había fallado en SonarQube. Esto ocurrió porque el workflow no tenía configurado el parámetro de espera síncrona del gate (`-Dsonar.qualitygate.wait=true`): sin él, el step solo envía el análisis al servidor y termina exitosamente si el envío se completa sin errores de red o autenticación, sin importar si el código cumple o no las condiciones de calidad.

**Segundo intento**

**Run:** https://github.com/iPeterMoon/orderflow-luma/actions/runs/35165094332

**Step:** [SonarQube Scan](https://github.com/iPeterMoon/orderflow-luma/actions/runs/35165094332/job/105024293909)

**Mensaje relevante:**
```
[ERROR] Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin:5.8.0.7211:sonar (default-cli) on project orderflow: The scanner analysis has failed! See the logs for more details. -> [Help 1]
```
 
**Resultado Sonar:** Quality Gate en `FAILED`, mismas condiciones incumplidas que en el Run 1.
 
**Resultado en GitHub Actions:** El step terminó en **rojo**. A diferencia del Run 1, ahora el propio proceso de Maven esperó el cálculo del Quality Gate y, al detectarlo en rojo, devolvió código de salida distinto de cero — lo que GitHub Actions interpreta como fallo del step y del job completo. Este `[ERROR]` no es un problema de conexión ni de configuración del scanner: es la señal esperada de que `qualitygate.wait=true` está funcionando como bloqueo.

## Corrección
**Cambio realizado:** Se agregó el parámetro `-Dsonar.qualitygate.wait=true` al comando de análisis en `.github/workflows/calidad.yml`. Posteriormente, se deshicieron los cambios de código introducidos deliberadamente para provocar el fallo (el `catch` vacío, la desreferencia nula y el código muerto), dejando el proyecto en su estado limpio original.

**Razón técnica:** El scanner de Sonar, por defecto, solo sube los resultados del análisis al servidor y termina el step exitosamente si esa carga se realizó sin errores técnicos (red, autenticación, formato). No evalúa por sí mismo si el código pasó o no el Quality Gate. El parámetro `sonar.qualitygate.wait=true` hace que el mismo step de Maven haga polling contra el servidor de SonarQube hasta que el Quality Gate termine de calcularse, y si el resultado es rojo, hace fallar el proceso de Maven con código de salida distinto de cero. Así, el estado del job en GitHub Actions queda alineado con el estado real del Quality Gate en SonarQube.

**Comparación antes/después:**
 
| | Antes (Run 1) | Después (Run 2) | Después de corregir el código (Run 3) |
|---|---|---|---|
| Quality Gate en Sonar | FAILED | FAILED | `<PASSED, una vez corregido el código>` |
| Resultado en GitHub Actions | Verde (falso positivo) | Rojo (correcto) | `<Verde, correcto>` |
| Link al run | [link Run 1](https://github.com/iPeterMoon/orderflow-luma/actions/runs/35163703063) | [link Run 2](https://github.com/iPeterMoon/orderflow-luma/actions/runs/35165094332) | [link Run 3](https://github.com/iPeterMoon/orderflow-luma/actions/runs/35165509875) |

## Decisión
**Qué bloquea la integración:** El rechazo del Quality Gate.

**Qué no demuestra el gate:** No valida el comportamiento de la aplicación en ejecución real como pruebas de integración o validación de endpoints.

**Qué falta revisar:** 
- Pruebas de integración/end-to-end contra el servicio corriendo, que el Quality Gate no ejecuta.
- Revisión manual de decisiones de diseño y arquitectura que Sonar no evalúa (por ejemplo, si la estructura multi-módulo del proyecto es adecuada).
- Configuración de branch protection en GitHub para que el check de este workflow sea obligatorio antes de mergear (pendiente de confirmar permisos de administración del repositorio).

## Limitaciones e IA
**Funciones no disponibles / pendientes:** Docker, AWS CLI, Terraform, Minikube, kubectl y Kompose
no se han cubierto aún — corresponden a sprints posteriores según la Regla del semestre del README.
Tampoco se implementó branch protection (requiere permisos de administración del repositorio que el
equipo no confirmó tener) ni la decoración automática de PRs (requiere SonarQube Developer Edition
o superior; el servidor del equipo corre en Community Edition).

**Uso de IA declarado:** Se usó Claude (Anthropic) como asistente durante el desarrollo de este Sprint,
específicamente para:
- Diagnosticar por qué el Quality Gate no bloqueaba el workflow de GitHub Actions a pesar de fallar en
  SonarQube (identificó la ausencia del parámetro `sonar.qualitygate.wait=true`).
- Revisar y corregir la configuración del plugin JaCoCo en el `pom.xml` multi-módulo 
- Redactar mensajes de commits y la descripción de PRs.


