# HT3 — Parte 1: Pruebas unitarias de TutorLink

Fecha de ejecución: 5 de noviembre de 2026. Rama: `feature/unit-tests`, creada desde `entrega-4`.

## Regla y decisión de arquitectura

**Una tutoría no puede reservarse si su duración es menor o igual a cero.**

El modelo real `domain/model/TutoringSession.kt` representa el intervalo mediante
`startAt` y `endAt` de tipo `Instant`; no existe un campo de duración en minutos.
Por eso se exige `endAt.isAfter(startAt)`, sin convertir ni truncar minutos.
Una sesión de 30 segundos es positiva y se acepta; no se agrega una duración mínima
de un minuto que el proyecto no define.

La reserva es parte del flujo principal: `TutorLinkViewModel.submitBooking` construye
actualmente una sesión de una hora (`startAt.plusSeconds(3600)`) y llama a
`SessionRepository.createSession`. `AppContainer` proporciona `SessionRepositoryImpl`.
No se encontraron casos de uso separados para la reserva.

Se agregó `domain/validation/BookingValidator.kt`, una validación Kotlin pura sobre
el modelo existente. El repositorio la ejecuta antes de convertir a DTO y antes de
invocar `SessionRemoteDataSource`. Si la duración es inválida, devuelve
`Result.failure(IllegalArgumentException)` mediante su manejo de errores existente;
el ViewModel ya maneja ese resultado sin confirmar la reserva.

La validación existente de campos del formulario (`data/BookingValidation.kt`)
continúa intacta: comprueba tema, fecha, hora y notas, una responsabilidad diferente.
No se duplicó esa lógica ni se agregaron campos a la UI.

## Archivos creados o modificados

- Creado: `app/src/main/java/com/example/app_tutorias/domain/validation/BookingValidator.kt`.
- Modificado: `app/src/main/java/com/example/app_tutorias/data/repository/SessionRepositoryImpl.kt`
  (importación y llamada al validador).
- Creado: `app/src/test/java/com/example/app_tutorias/BookingValidatorTest.kt`.
- Creado: `docs/HT3_PRUEBAS_UNITARIAS.md`.
- Creados: los registros y XML de evidencia en `docs/evidencias/ht3/`.

Se reutilizaron JUnit 4.13.2 y las dependencias existentes; no se modificaron archivos
Gradle. El archivo local previo `RESUMEN_PROYECTO.md` se conservó sin cambios.

## Pruebas locales

Clase: `com.example.app_tutorias.BookingValidatorTest`.

| Nombre exacto del método | Entrada | Resultado esperado |
| --- | --- | --- |
| `zero minute duration is rejected` | Final igual al inicio | `IllegalArgumentException` y mensaje de validación |
| `negative duration is rejected` | Final un minuto antes del inicio | `IllegalArgumentException` |
| `sixty minute duration is accepted` | Final 60 minutos después | Sin excepción |
| `positive duration below one minute is accepted without truncation` | Final 30 segundos después | Sin excepción |

Se usa un inicio fijo `2026-11-06T16:00:00Z`; estas pruebas no dependen del reloj,
de la zona horaria, de Firebase, de Android Context, de un emulador ni de la red.
No se crearon pruebas instrumentadas ni se cambiaron las pruebas existentes.

## Comandos y entorno

Primer intento:

```powershell
.\gradlew.bat testDebugUnitTest --offline
```

Gradle falló antes de ejecutar pruebas en `:app:processDebugGoogleServices`:
`Failed to create folder: ...\app\build\generated\res\processDebugGoogleServices`.
Crear la carpeta manualmente y repetir con `--console=plain` produjo el mismo error.
La ruta de salida estaba dentro de OneDrive y sus directorios tenían el atributo
`ReparsePoint`. Se resolvió para esta ejecución redirigiendo únicamente las salidas
a una carpeta temporal fuera de OneDrive con un script de inicialización local.
No se alteraron Firebase ni la configuración Gradle versionada.

Para reproducir desde la raíz del repositorio en Windows:

```powershell
$taskBuildRoot = Join-Path $env:TEMP 'tutorlink-ht3-build'
New-Item -ItemType Directory -Path $taskBuildRoot -Force | Out-Null
$taskInit = Join-Path $taskBuildRoot 'build-outside-onedrive.gradle'
$taskBuildPath = $taskBuildRoot.Replace('\', '/')
Set-Content -LiteralPath $taskInit -Encoding ascii -Value "allprojects { layout.buildDirectory.set(file('$taskBuildPath/' + project.name)) }"

.\gradlew.bat testDebugUnitTest --offline --console=plain --no-configuration-cache --init-script $taskInit
.\gradlew.bat assembleDebug --offline --console=plain --no-configuration-cache --init-script $taskInit
```

Se ejecutó el comando de pruebas anterior en las tres fases indicadas abajo.
`--offline` impide que Gradle descargue dependencias; el equipo ya tenía el SDK,
la distribución Gradle y las dependencias necesarias en caché. En una máquina nueva
será necesario provisionarlos previamente. Las pruebas no realizan llamadas de red.

Reporte HTML final local:
`%TEMP%\tutorlink-ht3-build\app\reports\tests\testDebugUnitTest\index.html`.
APK generado:
`%TEMP%\tutorlink-ht3-build\app\outputs\apk\debug\app-debug.apk`.

## Resultados comprobados: correcto → fallo → restaurado

| Fase | Resultado | Evidencia conservada |
| --- | --- | --- |
| Código correcto inicial | `BUILD SUCCESSFUL in 57s`, salida 0; 19 pruebas sin fallos | [01-correct.log](evidencias/ht3/01-correct.log) |
| Cambio incorrecto temporal | `BUILD FAILED in 9s`, salida 1; 19 pruebas, 1 fallo | [02-intentional-failure.log](evidencias/ht3/02-intentional-failure.log), [XML del fallo](evidencias/ht3/02-intentional-failure.xml) |
| Código correcto restaurado | `BUILD SUCCESSFUL in 9s`, salida 0; 19 pruebas sin fallos | [03-restored.log](evidencias/ht3/03-restored.log), [XML restaurado](evidencias/ht3/03-restored.xml) |
| Compilación APK debug | `BUILD SUCCESSFUL in 26s`, salida 0 | [04-assemble-debug.log](evidencias/ht3/04-assemble-debug.log) |

Conteo inicial y final leído de los reportes XML de Gradle:

| Clase | Pruebas | Fallos / errores / omitidas |
| --- | --- | --- |
| `BookingValidationTest` | 5 | 0 / 0 / 0 |
| `BookingValidatorTest` | 4 | 0 / 0 / 0 |
| `FirestoreFlowViewModelTest` | 7 | 0 / 0 / 0 |
| `TutorFilterTest` | 3 | 0 / 0 / 0 |
| Total | 19 | 0 / 0 / 0 |

### Cambio temporal y fallo observado

Condición correcta:

```kotlin
require(session.endAt.isAfter(session.startAt))
```

Se sustituyó temporalmente por:

```kotlin
require(!session.endAt.isBefore(session.startAt))
```

La condición incorrecta acepta igualdad, es decir, duración cero. No se modificaron
las pruebas. Falló exactamente `BookingValidatorTest.zero minute duration is rejected`
en `BookingValidatorTest.kt:16`, con este mensaje del XML:

```text
java.lang.AssertionError: expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown
```

Se guardaron los bytes del archivo original y se restauraron en un bloque `finally`
inmediatamente después de la ejecución incorrecta. Luego se ejecutó nuevamente toda
la suite. La versión final vuelve a usar `isAfter` y las 19 pruebas pasan.

## Alcance y verificación

- Se revisó el diff: no se modificaron login, registro, navegación, consultas ni
  escrituras de Firestore, reglas de seguridad o Firebase App Distribution.
- La ruta real de producción usa el validador en `SessionRepositoryImpl.createSession`
  antes de la llamada remota. Los tests nuevos ejercitan directamente el dominio;
  no se realizó una reserva real contra Firebase.
- Las 15 pruebas previas también pasaron; las de ViewModel usan repositorios falsos.
- Se compiló el APK debug con `assembleDebug`. No se verificó visualmente la aplicación
  ni se probaron login y registro en un dispositivo.
- La compilación informó un aviso existente de opt-in de coroutines en
  `SearchHistoryRepositoryImpl.kt` y un aviso de bibliotecas nativas sin símbolos
  eliminados; ninguno impidió las pruebas o la generación del APK.
- `git diff --check` no encontró errores de espacios. No se hizo commit, push ni merge.

## Respuestas listas para transcribir a la hoja

**Regla:** Una tutoría solo puede reservarse cuando su instante de finalización es
posterior al inicio; se rechazan duraciones de cero minutos y negativas.

**Clase y nombre de la prueba:** `BookingValidatorTest`, métodos
`zero minute duration is rejected`, `negative duration is rejected`,
`sixty minute duration is accepted` y
`positive duration below one minute is accepted without truncation`.

**Qué alteraron y qué resultado obtuvieron:** Cambiamos temporalmente la condición
`endAt.isAfter(startAt)` por `!endAt.isBefore(startAt)`, permitiendo duración cero.
La suite ejecutó 19 pruebas y falló la que rechaza cero porque no se lanzó
`IllegalArgumentException`. Restauramos la condición y las 19 pruebas volvieron a pasar.

**Qué error de su app detectaría esta prueba y qué ventaja ofrece frente a comprobarlo
manualmente:** Detectaría una regresión que permita guardar reservas sin duración o
con el final anterior al inicio. Automatiza los casos límite de forma repetible,
rápida y sin emulador ni servicios externos. El formulario actual genera una hora
automáticamente, por lo que estos intervalos inválidos son difíciles de provocar
manualmente; la prueba protege ante cambios futuros en el cálculo del final.

## Evidencia para el docente y propuesta de commit

Mostrar el validador, su llamada en el repositorio, los cuatro métodos de prueba,
los registros de las tres fases y los XML del fallo y de la restauración. Abrir el
reporte HTML final para mostrar las 19 pruebas aprobadas y el registro de
`assembleDebug` para acreditar la compilación. Los registros son evidencia real;
no se generaron capturas de pantalla.

Mensaje de commit propuesto:

```text
test: validar duración de reservas y documentar pruebas unitarias HT3
```

Revisar e incluir únicamente los archivos de esta tarea; `RESUMEN_PROYECTO.md` es
trabajo local previo y queda fuera de estos cambios.
