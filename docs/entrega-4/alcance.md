# Alcance congelado · Entrega 4

A partir de esta entrega no se agregan funciones nuevas. Solo se corrigen errores y hallazgos del testeo.

> Completar la columna **Historia (Entrega 2)** con el identificador y el texto de cada historia de usuario de la
> Entrega 2. Esas historias no están en el repositorio. Las funciones se listaron a partir del código de la rama `entrega-4`.

## Dentro

| Historia (Entrega 2) | Función | Dónde se ve |
|---|---|---|
| | Registrarse e iniciar sesión con correo y contraseña (Firebase Authentication) | Iniciar sesión |
| | Ver materias y tutores recomendados desde Firestore | Inicio |
| | Buscar tutores por nombre o materia y filtrar por materia | Buscar |
| | Guardar y reutilizar el historial de búsquedas sin conexión (Room) | Buscar |
| | Ver el perfil de un tutor: descripción, especialidad, precio y calificación | Detalle del tutor |
| | Reservar una tutoría con tema, fecha, hora y notas validados en tiempo real | Detalle y reserva |
| | Ver las tutorías próximas y completadas | Mis tutorías |
| | Consultar datos y reservar sin conexión, con aviso de reserva pendiente y sincronización automática | Toda la app |
| | Abrir el perfil de un tutor desde un enlace `tutorlink://tutor/{id}` | Detalle del tutor |
| | Cambiar a tema oscuro y conservar la preferencia (DataStore) | Configuración |
| | Cerrar sesión | Configuración |

## Fuera

| Historia (Entrega 2) | Función | Motivo |
|---|---|---|
| | Marcar tutores como favoritos | El repositorio existe, pero no hay interfaz; queda para después |
| | Cancelar o reprogramar una reserva | El repositorio admite cambiar el estado, pero no hay interfaz ni reglas probadas |
| | Elegir un horario de la disponibilidad del tutor | El catálogo no tiene horarios estructurados |
| | Perfil y agenda para tutores | La app solo cubre el rol de estudiante |
| | Notificaciones por correo o push | No forma parte del flujo principal |
| | Pagos y calificación de tutorías | Fuera del alcance del curso |
