# Entrega 4 · Datos reales, modo sin conexión e historial local

> Descripción para el pull request `entrega-4` → `main`. El PR se deja **abierto y sin fusionar**.

## Resumen

TutorLink deja de usar datos mock en el flujo principal. El catálogo y las reservas vienen de Cloud Firestore a través de
repositorios. La app funciona sin conexión gracias a la caché de Firestore. El historial de búsquedas se guarda en Room.

**Video (2 min 41 s, continuo):** [Ver grabación](evidencias/video-entrega-4.mp4)

**Evidencias ejecutadas:** [Capturas, resultados e IDs de Firestore](evidencias/README.md). Pendientes: captura de la consola web, task test externo e historias de la Entrega 2.

## Qué incluye

- **Autenticación:** registro e inicio de sesión con Firebase Authentication y perfil en `users`.
- **Catálogo:** `subjects` y `tutors` se leen con `CatalogRepository` y se convierten a modelos de dominio.
- **Reservas:** `SessionRepository.createSession()` escribe en `sessions` con un ID estable por intento, para no duplicar
  reservas. El calendario escucha las sesiones del usuario en tiempo real.
- **Sin conexión:** la caché de Firestore permite leer datos guardados. Una reserva sin confirmar se marca como
  *Pendiente de sincronizar* y se confirma sola al volver la red.
- **Room:** las búsquedas se guardan por usuario y se muestran como *Búsquedas recientes*, también sin conexión.
- **DataStore:** el tema oscuro se conserva entre sesiones.
- **Arquitectura:** capas `data` / `domain` / `ui`, inyección manual con `AppContainer` y estado expuesto con `StateFlow`.

## Evidencias

| Requisito | Evidencia |
|---|---|
| Reserva real en `sessions` y en el calendario | [Pruebas en dispositivo §1](pruebas-dispositivo.md#1-reserva-real-con-conexión) |
| Modo avión: caché, aviso pendiente y sincronización | [Pruebas en dispositivo §2](pruebas-dispositivo.md#2-modo-avión) |
| Historial en Room visible al reabrir sin conexión | [Pruebas en dispositivo §3](pruebas-dispositivo.md#3-historial-de-búsquedas-room) |
| Task test con persona externa ★ | [Task test](task-test.md) |
| Alcance congelado (Dentro / Fuera) | [Alcance](alcance.md) |
| Convenciones y observaciones del docente | [Convenciones](convenciones.md) |
| Configuración de Firestore | [docs/firestore.md](../firestore.md) |

## Cómo probar

1. Abrir el proyecto en Android Studio en la rama `entrega-4` y ejecutar **Run ▶ app**.
2. Ejecutar las pruebas unitarias: `./gradlew testDebugUnitTest`.
3. Seguir los pasos de [Pruebas en dispositivo](pruebas-dispositivo.md).

## Pruebas unitarias

- `FirestoreFlowViewModelTest`: catálogo desde el repositorio, reserva única ante doble pulsación, error de escritura,
  reserva pendiente frente a confirmada, recuperación cuando no llega el primer snapshot, limpieza al cambiar de cuenta e
  historial de búsquedas (guardar, reutilizar, borrar y mostrar sin catálogo).
- `BookingValidationTest` y `TutorFilterTest`.

## Uso de IA

Parte del código del historial de búsquedas y la documentación de esta entrega se elaboraron con apoyo de Claude
(Anthropic). El equipo revisó y probó los cambios.
