# TutorLink — Correcciones de la entrega final

Fecha de auditoría: **11 de noviembre de 2026**, zona `America/Guatemala`.

Rama de trabajo: `fix/entrega-final-observaciones`. Base utilizada: `5c32a44`, obtenida de `origin/feature/unit-tests` durante la Fase 0. Código auditado hasta `3611215`.

**Resultado:** las seis observaciones están corregidas en el código. La compilación y las 28 pruebas unitarias pasan. La validación manual en dispositivo y con Firebase real permanece pendiente; este informe no la presenta como realizada.

## Trazabilidad de las observaciones

Las rutas de esta tabla son relativas a la raíz del repositorio. Los SHA corresponden a commits manuales existentes y verificados en el historial local.

| Observación | Archivos corregidos | Solución | Commit |
|---|---|---|---|
| 1. Paquete con guion bajo y nombres en español | `app/build.gradle.kts`; `app/src/main/AndroidManifest.xml`; `app/src/main/java/com/example/tutorlink/TutorLinkApplication.kt`; `app/src/main/java/com/example/tutorlink/data/local/room/TutorLinkDatabase.kt`; paquetes e imports de producción y pruebas; `scripts/run-android.ps1` | Paquete y namespace `com.example.tutorlink`; clases `TutorLinkApplication` y `TutorLinkDatabase`; manifiesto y comando de arranque actualizados. Se conserva el applicationId registrado en Firebase. | `44b2948` |
| 2. Componentes sin Modifier externo | `app/src/main/java/com/example/tutorlink/ui/components/TopBar.kt`; `app/src/main/java/com/example/tutorlink/ui/components/BottomBar.kt`; eliminado `app/src/main/java/com/example/tutorlink/ui/components/ScreenPlaceholder.kt` | Ambas barras reciben `modifier: Modifier = Modifier` y lo aplican al contenedor raíz. El placeholder se elimina porque no tenía usos. | `1ce51e6`, `d6d2d7e` |
| 3. Código muerto | Eliminados `app/src/main/java/com/example/tutorlink/ui/components/ScreenPlaceholder.kt` y `app/src/main/java/com/example/tutorlink/ui/components/ScreenStates.kt` | Eliminados ScreenPlaceholder, LoadingState y ErrorState. Se mantienen DataStatus, EmptyState y los estados de carga/error de los ViewModels. | `d6d2d7e` |
| 4. Saludo y horarios fijos | `app/src/main/res/values/strings.xml`; `app/src/main/java/com/example/tutorlink/ui/screens/HomeScreen.kt`; `app/src/main/java/com/example/tutorlink/ui/screens/TutorDetailScreen.kt`; `app/src/main/java/com/example/tutorlink/data/Models.kt`; `app/src/main/java/com/example/tutorlink/ui/DomainUiMappers.kt`; estado, inyección y pruebas | Saludo mediante el perfil de users/{uid}, con «¡Hola!» cuando falta el nombre. Horarios derivados únicamente de availability, con aviso cuando no hay horarios publicados. El detalle observa los cambios del catálogo. | `6f44909`; integración con la separación: `d98bf00` |
| 5. Un único ViewModel para varias pantallas | `app/src/main/java/com/example/tutorlink/ui/home/HomeViewModel.kt`; `app/src/main/java/com/example/tutorlink/ui/search/SearchViewModel.kt`; `app/src/main/java/com/example/tutorlink/ui/calendar/CalendarViewModel.kt`; `app/src/main/java/com/example/tutorlink/ui/detail/TutorDetailViewModel.kt`; `app/src/main/java/com/example/tutorlink/ui/ScreenViewModelFactory.kt`; `app/src/main/java/com/example/tutorlink/ui/TutorLinkUiState.kt`; `app/src/main/java/com/example/tutorlink/di/AppContainer.kt`; `app/src/main/java/com/example/tutorlink/navigation/NavHost.kt`; repositorio compartido y pruebas | Cuatro ViewModels con responsabilidades propias y StateFlow de solo lectura. TutorLinkRepository comparte las lecturas autenticadas sin duplicar listeners. Reservas e historial permanecen en sus respectivos repositorios. Eliminados el ViewModel anterior y su factory. | `d98bf00` |
| 6. Restos de plantilla | Eliminado `app/src/androidTest/java/com/example/tutorlink/ExampleInstrumentedTest.kt`; actualizado `app/src/main/res/values/colors.xml` | Eliminada la prueba de plantilla y cinco colores purple_*/teal_* sin referencias. Se conservan las cuatro suites unitarias, Entrega4EvidenceTest y las paletas de TutorLink. | `3611215` |

## Arquitectura resultante

```text
Compose / Navigation
  ├── HomeViewModel          → saludo, recomendaciones y próxima tutoría
  ├── SearchViewModel        → búsqueda, filtros e historial
  ├── CalendarViewModel      → sesiones, ordenamiento y filtro
  └── TutorDetailViewModel   → detalle, formulario y reservas
           │
           ├── TutorLinkRepository (datos compartidos)
           │     ├── CatalogRepository
           │     ├── UserRepository
           │     ├── SessionRepository
           │     └── AuthRepository
           ├── SearchHistoryRepository (búsqueda)
           └── SessionRepository (envío de reservas)
```

Los ViewModels reciben contratos de repositorio por constructor, utilizan `viewModelScope` y exponen estados inmutables con `StateFlow`. No importan Firebase ni Room y no crean repositorios. `AppContainer` proporciona las implementaciones.

El repositorio compartido se observa con `SharingStarted.WhileSubscribed`: mantiene las lecturas durante una pausa de hasta cinco segundos sin consumidores y después cancela los listeners y limpia su replay. El cambio de usuario sustituye las suscripciones y limpia los estados de las pantallas. Los cuatro ViewModels conservan el alcance del host de navegación para mantener el comportamiento existente entre pestañas y cambios de configuración.

## Compatibilidad preservada

- `namespace` y paquetes Kotlin: `com.example.tutorlink`.
- `applicationId`: **`com.example.app_tutorias`**, conservado intencionalmente para mantener la identidad instalada y la asociación con Firebase. Este identificador no es una referencia de código obsoleta.
- `app/google-services.json`: sin cambios respecto a la base de la auditoría.
- Room: archivo **`app_tutorias.db`**, versión **1**, tabla **`search_history`**. Entidad y DAO conservados salvo el paquete. En la Fase 1 se comparó el código generado de Room: el SQL y los hashes de identidad del esquema se conservaron.
- Sin cambios en las reglas ni en el esquema de Firestore; no se escribieron datos en la consola durante estas fases.
- Sin cambios en las dependencias del catálogo de versiones, las paletas clara/oscura ni los recursos del tema XML.
- La reserva mantiene una hora de duración y el identificador estable por intento para prevenir duplicados por doble pulsación. Las operaciones de persistencia remota se mantienen.

## Verificación final ejecutada

La última ejecución se realizó sobre una copia del código fuera de OneDrive. No se modificaron los archivos de configuración Gradle para sortear los problemas locales.

Comando ejecutado en esa copia:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug assembleDebugAndroidTest --console=plain --no-configuration-cache `
  '-Porg.gradle.java.installations.auto-detect=false' `
  '-Porg.gradle.java.installations.auto-download=false' `
  '-Porg.gradle.java.installations.paths=C:\Users\Asus\.gradle\jdks\eclipse_adoptium-25-amd64-windows.2'
```

**Resultado: BUILD SUCCESSFUL**, en 1 min 26 s. El APK de la app y el APK de pruebas instrumentadas se compilaron. Las pruebas instrumentadas no se ejecutaron.

| Suite unitaria | Pruebas | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|
| BookingValidationTest | 5 | 0 | 0 | 0 |
| BookingValidatorTest | 4 | 0 | 0 | 0 |
| FirestoreFlowViewModelTest | 16 | 0 | 0 | 0 |
| TutorFilterTest | 3 | 0 | 0 | 0 |
| **Total** | **28** | **0** | **0** | **0** |

Las pruebas incluyen catálogo desde repositorio, confirmación/error de reserva, envío duplicado, escritura pendiente y sincronización, carga inicial con plazo y recuperación, cierre/cambio de cuenta, historial, perfiles ausentes/vacíos/erróneos, disponibilidad dinámica/vacía, listeners compartidos, cancelación de reservas en curso y conservación de estado al reutilizar ViewModelStore. Son pruebas con repositorios simulados: no constituyen comprobaciones del backend real ni una prueba de rotación en dispositivo.

También se verificó:

- Los **80 archivos Kotlin** de producción y pruebas usan el paquete `com.example.tutorlink` o sus subpaquetes.
- No quedan referencias a las clases, componentes, prueba y colores eliminados en el código fuente.
- Las barras conservadas reciben y aplican el Modifier externo.
- No quedan el saludo «Andrea» ni los horarios señalados en el código.
- Los mocks no alimentan navegación, inyección ni los cuatro ViewModels del flujo principal; permanecen para previews y pruebas.
- Revisión estática de imports y referencias, sin candidatos evidentes a imports sin uso. No se ejecutó lintDebug.
- `git diff --check` sin errores; sin cambios de producción durante la Fase 7.

## Limitaciones y comprobaciones pendientes

`adb devices` no mostró dispositivos conectados. No se declara completado ninguno de estos recorridos manuales:

- [ ] Iniciar sesión con dos cuentas reales y comprobar el saludo de cada una y el caso sin nombre.
- [ ] Recorrer Inicio → Buscar → Detalle → Reservar → Calendario y volver atrás.
- [ ] Comprobar horarios reales y tutores sin disponibilidad publicada. Los documentos de demostración pueden no tener horarios, según `docs/firestore.md`; no se inspeccionó su contenido actual en consola.
- [ ] Crear una reserva con una cuenta de prueba autorizada y comprobar su documento en Firestore.
- [ ] Activar modo avión, comprobar caché e historial, reservar sin conexión y verificar una sola sincronización al recuperar la red.
- [ ] Rotar el dispositivo y comprobar conservación de filtros y formulario. La reutilización de ViewModelStore sí tiene una prueba unitaria.
- [ ] Revisar las barras y el resto del flujo en temas claro y oscuro.
- [ ] Actualizar una instalación existente y comprobar que conserva el historial de Room.

Advertencias de compilación pendientes: opt-in de `ExperimentalCoroutinesApi` en SearchHistoryRepositoryImpl, API de Compose de pruebas obsoleta, APIs Gradle obsoletas y bibliotecas nativas empaquetadas sin eliminar símbolos. No impidieron compilar ni ejecutar las pruebas unitarias.

En la Fase 0, la ejecución directa seleccionó un runtime de VS Code sin `jlink.exe`; otro intento no pudo crear carpetas de compilación dentro de OneDrive. La solución utilizada para verificar fue una copia temporal y un JDK completo mediante argumentos de la invocación. El usuario debe seleccionar un JDK completo en su entorno y repetir las verificaciones si modifica el código.

Las referencias a paquetes/clases antiguos en informes y evidencias de entregas anteriores se conservan como contexto histórico. `README.md` y `docs/firestore.md` se actualizan para describir el código actual. El archivo local preexistente `RESUMEN_PROYECTO.md` permanece intacto y fuera de este cambio.

## Fase 7 y publicación

El commit de este informe y las actualizaciones de documentación está **Pendiente**: debe realizarlo manualmente el usuario. No se ejecutaron commits, push, merge ni rebase por el agente y no se creó ningún PR.

Antes de publicar, verificar la rama, el diff preparado y los commits locales. La incorporación de esta rama a `entrega-final` debe realizarse mediante un PR revisado por el equipo. El **PR oficial de evaluación será `entrega-final` → `main` y deberá permanecer abierto, sin fusionarse, hasta la evaluación**.

## Texto propuesto para el PR de correcciones

**Título:** Corrige las seis observaciones de la Entrega 4 de TutorLink

### Cambios

Se corrigen los paquetes y nombres de clases, los parámetros Modifier de las barras, los componentes sin uso, los datos fijos del saludo y la disponibilidad, la concentración de lógica en un solo ViewModel y los restos de plantilla.

Inicio, Buscar, Calendario y Detalle tienen ViewModels propios y comparten las lecturas autenticadas mediante un repositorio. Se conserva el applicationId de Firebase, el archivo y esquema de Room, la lógica de reserva y las paletas visuales existentes.

### Validación

- 28 pruebas unitarias aprobadas, sin fallos, errores ni omisiones.
- assembleDebug y assembleDebugAndroidTest exitosos en copia fuera de OneDrive con JDK completo.
- Sin referencias obsoletas en el código ni errores de git diff --check.
- Pendiente: pruebas instrumentadas, navegación/rotación/temas en dispositivo, Firebase real, modo avión y actualización de una instalación existente. Las pruebas unitarias usan repositorios simulados.

### Trazabilidad

Commits de corrección: `44b2948`, `1ce51e6`, `d6d2d7e`, `6f44909`, `d98bf00`, `3611215`. El detalle de cada observación y sus archivos se encuentra en este documento. Commit de documentación final: **Pendiente**.
