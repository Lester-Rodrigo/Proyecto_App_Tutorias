# Evidencias ejecutadas · Entrega 4

Grabación y capturas reales del 30 de octubre de 2026 (America/Guatemala), en el emulador Pixel 10 Pro,
Android 17, con el APK debug de la rama `entrega-4` y una cuenta exclusiva de prueba.
Se usaron Firebase Authentication y Cloud Firestore del proyecto `app-tutorias-5710d`, sin repositorios mock.

## Video

[Ver o descargar el video continuo](video-entrega-4.mp4) · **2 min 41 s**, 720 × 1600, sin audio.
La grabación conserva el tiempo real, con pausas de aproximadamente 3 segundos entre acciones y 5 segundos
en las capturas importantes. Se normalizó el archivo a 30 fps para su reproducción; no se aceleró el recorrido.

## Resultados

| Comprobación | Resultado y evidencia |
|---|---|
| Catálogo real | [Tutores de Firestore](01-catalogo-firestore.png) |
| Reserva con conexión | [Formulario](02-formulario-conexion.png), [confirmación](03-reserva-confirmada.png), [calendario](04-calendario-confirmado.png) |
| Historial y catálogo en modo avión | [Búsquedas recientes y datos en caché](05-historial-y-cache-modo-avion.png) |
| Reserva sin conexión | [Formulario](06-formulario-sin-conexion.png), [aviso pendiente](07-reserva-pendiente.png), [calendario pendiente](08-calendario-pendiente.png) |
| Recuperación de red | [La misma reserva confirmada en el calendario](09-calendario-sincronizado.png) |
| Historial después de cerrar completamente la app | [Nueva apertura sin red después de `am force-stop`](10-historial-tras-cierre-completo-sin-red.png) |

La ejecución principal y la comprobación adicional de apertura sin red finalizaron con `OK (1 test)` cada una.
Durante el tramo sin conexión se activó el modo avión y se deshabilitaron Wi-Fi y datos móviles.
La captura adicional de cierre completo se realizó después del video; no se insertó en la grabación continua.

## Documentos reales en Firestore

- UID de prueba: `Now9iMaw0tTHThI9aJHUvFEuh9r2`.
- Reserva con conexión: [`sessions/31db179b-29dc-4251-bd21-0ba1c8322a96`](reserva-online-servidor.json).
- Reserva sincronizada: [`sessions/e0d0e967-2116-476a-9442-484bb29f3d1e`](reservas-sincronizadas-servidor.json).
- Ambas pertenecen a `ana-morales`, materia `math`, estado `UPCOMING`, y duran una hora.
- La primera comienza el **1 de noviembre de 2026 a las 16:00** de Guatemala; la segunda a las **17:00**.
- La reserva sin red produjo un único documento nuevo al sincronizar; el snapshot final informa `hasPendingWrites: false`.
- [Registro de pasos y tiempos UTC](results.json).

Los JSON se obtuvieron con `Source.SERVER` desde el SDK autenticado de la app. Son lecturas reales del servidor;
no son capturas de la consola web. Se pueden localizar esos mismos IDs en Firestore → Datos → sessions.
Las otras reservas que aparecen en el calendario corresponden a las tomas previas de esta grabación.

## Captura de la consola de Firebase

El usuario aportó la [captura de la reserva con conexión en la consola](11-firestore-consola-reserva-online.png). Muestra el documento `31db179b-29dc-4251-bd21-0ba1c8322a96`, sus relaciones, horario y estado `UPCOMING`, coincidentes con el snapshot del servidor.

## Alcance de la evidencia

Quedan pendientes la captura visual en la consola de la reserva sincronizada (`e0d0e967-2116-476a-9442-484bb29f3d1e`), el task test con una persona externa y la
vinculación de las historias de la Entrega 2. No se presentan como realizadas las comprobaciones adicionales de
borrar historial, cambiar de cuenta, reutilizar un chip o cerrar la app mientras hay una reserva pendiente.
El control de permisos de esta sesión bloqueó la apertura de la consola de Firebase en el navegador.

La automatización está en `app/src/androidTest/java/com/example/app_tutorias/Entrega4EvidenceTest.kt`.
Requiere habilitación explícita (`runLiveEvidence=true` o `coldStartEvidence=true`) y un emulador/cuenta preparados.
La primera variante crea dos reservas reales; las credenciales se proporcionan como argumentos y no están en Git.
