# Persona 3: build final y App Distribution

Fecha de preparación: 15 de noviembre de 2026 (Guatemala).
Base remota verificada: `origin/entrega-final`, commit `25c1064`.
Esa base ya integra las correcciones de `fix/entrega-final-observaciones`, Crashlytics y el README final.

## Versión y firma

Versión preparada: **1.2 (3)**, applicationId **com.example.app_tutorias**, Android mínimo **36**.
La versión anterior registrada en `app/release/output-metadata.json` es 1.0 (1); ese archivo es evidencia histórica, no evidencia de publicación de 1.2.

1. Recuperar **el mismo `tutorlink-release.jks`** usado para 1.0. No generar otro ni usar debug.keystore.
2. Copiar `keystore.properties.example` a `keystore.properties` en la raíz. Completar la ruta **absoluta** de la clave (usar `/` en Windows), alias y contraseñas. Es un archivo privado ignorado por Git.
3. Ejecutar desde la raíz con un JDK completo 25:

   ```powershell
   .\scripts\build-final.ps1 -JdkPath 'C:\ruta\al\jdk-25'
   ```

El script trabaja fuera de OneDrive, ejecuta las pruebas unitarias debug disponibles en este proyecto, lint de ambas variantes y compila los APK debug, de pruebas y release. La configuración actual de AGP no registra `testReleaseUnitTest`. Conserva los informes en la ruta temporal que muestra. No ejecuta las pruebas instrumentadas sin un dispositivo.
El APK firmado queda en `app/build/outputs/final/TutorLink-1.2-3.apk`, junto con su SHA-256 mostrado en consola.
Si falta la configuración de firma, `assembleRelease` falla expresamente en lugar de entregar un APK sin firma.
Para verificar solo debug: `./scripts/build-final.ps1 -ValidateOnly -JdkPath 'C:\ruta\al\jdk-25'`.

Antes de publicar, verificar la firma:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\37.0.0\apksigner.bat" verify --verbose --print-certs app/build/outputs/final/TutorLink-1.2-3.apk
```

Comparar el certificado SHA-256 con el APK 1.0 distribuido. Probar la actualización sobre esa instalación sin borrar datos y recorrer búsqueda → perfil → reserva → calendario, Atrás, rotación, modo avión y reapertura.

## Publicación y evidencia

1. Abrir [App Distribution del proyecto](https://console.firebase.google.com/project/app-tutorias-5710d/appdistribution) con una cuenta autorizada.
2. Seleccionar la app Android `com.example.app_tutorias` y cargar el APK firmado 1.2 (3).
3. Pegar [las notas de versión](notas-version-1.2.txt).
4. Seleccionar el docente, el equipo revisor y al menos dos testers externos identificados por el equipo. Distribuir a esos destinatarios autorizados.
5. Pedir a los dos externos que acepten y descarguen **esta versión**, y confirmen su instalación. Una invitación enviada o aceptada no demuestra una descarga.
6. Abrir el detalle de **1.2 (3)** y guardar una captura que muestre la versión y dos descargas de testers externos. Adjuntarla en el PR `entrega-final` → `main`, con las notas y el enlace del build.
7. Para la evidencia de monitoreo, reproducir un error esperado capturado por un repositorio, cerrar y reabrir la app y guardar el evento no fatal de **este build** en Crashlytics. Explicar el error y su tratamiento en el PR.

## Verificación local ejecutada

Sobre una copia fuera de OneDrive con JDK 25:

```text
testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
BUILD SUCCESSFUL in 8m 17s — 87 tareas ejecutadas
```

- **28 pruebas unitarias**, sin fallos, errores ni omisiones (5 + 4 + 16 + 3).
- APK debug **1.2 (3)** y APK de pruebas instrumentadas compilados.
- Lint debug: **0 errores y 12 advertencias**. No se añadieron supresiones para ocultarlas.
- [Log de Gradle](evidencias/validacion-local.log), [informe XML de lint](evidencias/lint-results-debug.xml) e informes JUnit en [evidencias/](evidencias/).
- APK local de comprobación: `app/build/outputs/final/TutorLink-1.2-3-debug.apk`. Usa firma de depuración; no es el release final para distribuir.
- Sintaxis PowerShell del script y `git diff --check` verificados sin errores. El script rechaza inmediatamente generar un release sin `keystore.properties`.
- También se ejecutó `:app:preReleaseBuild` sin credenciales: rechazó correctamente el release con el mensaje de firma faltante ([log](evidencias/firma-sin-credenciales.log)). Este fallo intencional no es una compilación release aprobada.
- Las pruebas instrumentadas no se ejecutaron: no había dispositivo conectado.

### Release firmado verificado

`assembleRelease lintRelease`: **BUILD SUCCESSFUL in 5m 52s**, 55 tareas (54 ejecutadas y 1 actualizada). Lint release: **0 errores y 11 advertencias**. [Log](evidencias/validacion-release.log) e [informe lint](evidencias/lint-results-release.xml).

APK: `app/build/outputs/final/TutorLink-1.2-3.apk`.

- [Metadatos generados](evidencias/output-metadata-1.2.json): `com.example.app_tutorias`, versión `1.2`, código `3`.
- [Firma verificada con apksigner](evidencias/firma-release.txt): certificado idéntico al APK 1.0 descargado de Firebase.
- SHA-256 del APK: `75ba1f4fb8d4f858cee575c224bffcf3981ff3ea9f19c5035b63bf2a317db33c` ([archivo](evidencias/apk-sha256.txt)).
- La comparación de certificados comprueba continuidad de firma; la actualización y el recorrido funcional en dispositivo siguen pendientes.

## Publicación confirmada y pendientes

- Clave original recuperada y proporcionada por el usuario: alias `tutorlink`, entrada de clave privada válida. La configuración se guarda únicamente en `keystore.properties`, ignorado por Git. No se publican las contraseñas ni el archivo JKS.
- Se descargó el APK 1.0 (1) desde Firebase y se verificó su firma con `apksigner`. Su certificado SHA-256 coincide con la clave recuperada: `97742386bc611d09b7ab4f3a0902fcd9defe239ed6ac4567e76eab13597fd3db`.
- **1.2 (3) publicada y distribuida** en Firebase App Distribution el **15 de noviembre de 2026**, con hora de carga **21:16:52 (Guatemala)**. Firebase confirmó «Se completó la distribución correctamente».
- Destinatarios: el grupo existente «Testers TutorLink» (3 verificadores) y el verificador individual que tenía acceso a 1.0; **4 invitados en total**. Se publicaron las [notas de versión](notas-version-1.2.txt).
- Estado observado tras distribuir: **4 invitados, 1 aceptación, 0 descargas** de 1.2 (3). La aceptación corresponde al acceso de un tester; no demuestra que haya descargado el build nuevo.
- [Consola de la app](https://console.firebase.google.com/u/1/project/app-tutorias-5710d/appdistribution/app/android:com.example.app_tutorias/releases?hl=es-419).
- [Captura de publicación](evidencias/app-distribution-1.2-publicada.jpg). Esta captura prueba versión, distribución y notas; **no acredita las dos descargas externas requeridas**.
- La clasificación de los verificadores por rol se omitió a petición del usuario.
- `adb devices` no detectó dispositivos; la validación en dispositivo y de actualización permanece pendiente.
- Pendiente: dos testers externos deben descargar e instalar **1.2 (3)**, y luego guardar una captura actualizada donde se vean esas dos descargas. La evidencia de Crashlytics del build distribuido también requiere ejecutarlo y generar un evento no fatal real; no se presenta como obtenida.

## Referencias

- [Firma de Android: configuración privada de keystore](https://developer.android.com/studio/publish/app-signing).
- [Distribuir APK desde la consola de Firebase](https://firebase.google.com/docs/app-distribution/android/distribute-console).

Preparación de versión, configuración de firma, script y documentación asistida por OpenAI Codex. Las verificaciones ejecutadas y sus límites se registran por separado de las comprobaciones pendientes.
