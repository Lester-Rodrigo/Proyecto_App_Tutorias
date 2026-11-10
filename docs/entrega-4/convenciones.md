# Convenciones y observaciones · Entrega 4

Cada observación se corrige o se justifica, indicando el commit correspondiente.

> Agregar en la primera tabla las observaciones del docente que no estén registradas. No aparecen en los issues ni en los
> pull requests de GitHub.

## Observaciones del docente

| Observación | Acción | Commit |
|---|---|---|
| Observaciones de la Entrega 3 | Corregidas: el formulario de reserva se normaliza antes de validar, el estado pasa a `TutorLinkViewModel` y el aviso de confirmación se muestra desde un `LaunchedEffect` | `Cambios debido a observaciones hechos` |
| | | |

## Revisión de convenciones del equipo

| Convención | Estado | Acción | Commit |
|---|---|---|---|
| El archivo se llama como su contenido | `BorromBar.kt` contenía `TutorLinkBottomBar` | Renombrado a `BottomBar.kt` | `Corrige convenciones de nombres y plurales` |
| Los textos visibles viven en `strings.xml` con plurales | La búsqueda mostraba "1 resultados" | Recurso `plurals` `results_count` | `Corrige convenciones de nombres y plurales` |
| Capas `data` / `domain` / `ui` con repositorios inyectados por `AppContainer` | Se cumple | Sin cambios | — |
| Mensajes de commit en español y en tercera persona del presente | Los commits más recientes la siguen; los anteriores usan gerundio ("Agregando…") | Se justifica: no se reescribe el historial ya publicado | — |
| Configuración local fuera del repositorio | `app/google-services.json` está versionado, aunque `app/.gitignore` lo excluye | Se justifica: identifica el proyecto Firebase y no contiene secretos; la seguridad depende de las reglas de Firestore | — |
| Archivos de `.idea/` | Se versionan algunos | Se justifica: comparten la configuración de ejecución del equipo | — |
