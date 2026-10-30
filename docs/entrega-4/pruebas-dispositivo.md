# Pruebas en dispositivo · Entrega 4

Registro de las pruebas manuales de reserva real, modo avión e historial local.
Hacerlas en un teléfono o emulador con la rama `entrega-4` y una cuenta de prueba.

**Dispositivo:** ______ · **Android:** ______ · **Cuenta:** ______ · **Fecha:** ______

Antes de empezar: abrir Inicio, Buscar y Calendario con conexión para que Firestore llene su caché.

## 1. Reserva real con conexión

| # | Paso | Resultado esperado | Resultado observado | Evidencia |
|---|---|---|---|---|
| 1.1 | Buscar → elegir un tutor → Reservar con fecha `AAAA-MM-DD` (hoy o posterior) y hora `HH:mm` | Antes de 10 s aparece el mensaje de reserva confirmada | | |
| 1.2 | Pulsar *Ver calendario* | La sesión aparece en *Próximas* sin la etiqueta "Pendiente de sincronizar" | | |
| 1.3 | Abrir la consola de Firebase › Firestore › `sessions` | Hay un documento nuevo | | |
| 1.4 | Revisar los campos del documento | `studentId` = UID del usuario; `tutorId` y `subjectId` (primera materia del tutor) correctos; `startAt` coincide con la hora local; `endAt` = `startAt` + 1 h; `status` = `UPCOMING` | | |

## 2. Modo avión

| # | Paso | Resultado esperado | Resultado observado | Evidencia |
|---|---|---|---|---|
| 2.1 | Activar el modo avión, cerrar la app por completo y abrirla | Inicio, Buscar y Calendario muestran los datos de la caché; no se queda cargando | | |
| 2.2 | Reservar con otro tutor sin conexión | Tras unos 10 s: mensaje "Reserva pendiente de sincronización…" y botón *Pendiente de sincronizar* deshabilitado | | |
| 2.3 | Abrir el Calendario | La sesión nueva tiene la etiqueta "Pendiente de sincronizar" | | |
| 2.4 | Revisar `sessions` en la consola | La reserva pendiente todavía no existe | | |
| 2.5 | Desactivar el modo avión con la app abierta en el Calendario | La etiqueta cambia al estado normal en pocos segundos | | |
| 2.6 | Revisar `sessions` en la consola | Existe **un solo** documento para esa reserva | | |
| 2.7 | (Opcional) Repetir 2.2 y cerrar la app antes de reconectar; abrirla con conexión | La reserva se sincroniza igual, porque Firestore guarda la cola en disco | | |

## 3. Historial de búsquedas (Room)

| # | Paso | Resultado esperado | Resultado observado | Evidencia |
|---|---|---|---|---|
| 3.1 | En Buscar, escribir "Álgebra" y pulsar *Buscar* en el teclado | Al vaciar el campo aparece en *Búsquedas recientes* | | |
| 3.2 | Escribir otra búsqueda y abrir un tutor de los resultados | La búsqueda también queda en el historial, la más reciente primero | | |
| 3.3 | Tocar un chip del historial | El campo se llena y la lista se filtra | | |
| 3.4 | Activar el modo avión, cerrar la app y abrirla | En Buscar se siguen viendo las búsquedas recientes | | |
| 3.5 | Pulsar *Borrar historial* | La sección desaparece | | |
| 3.6 | Cerrar sesión y entrar con otra cuenta | No se ven las búsquedas de la cuenta anterior | | |

## Hallazgos

Anotar aquí cualquier diferencia con lo esperado y el commit que la corrige.

| Paso | Qué pasó | Corrección o justificación | Commit |
|---|---|---|---|
| | | | |
