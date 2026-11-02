# Task test · Anna Bran · Entrega 4

## Datos de la sesión

- **Participante:** Anna Bran.
- **Fecha:** miércoles 28 de octubre de 2026.
- **Dispositivo:** laptop del usuario, donde está instalado el programa.
- **Perfil de la participante:** no registrado.
- **Versión del programa y entorno de ejecución:** no registrados; no se confirmó si se utilizó emulador, prototipo u otra versión.
- **Observador:** identidad no registrada.
- **Grabación de esta sesión:** no aportada.
- **Registro elaborado:** 2 de noviembre de 2026, a partir del relato del equipo proporcionado en esta conversación.

El equipo confirmó que Anna realizó la prueba. Las observaciones siguientes son una síntesis de ese relato,
no una transcripción literal de la participante. El video automatizado del 30 de octubre es una evidencia
técnica independiente y no se atribuye a Anna.

## Tarea y criterio de éxito

Encontrar un tutor, hacer una reserva y comprobar que aparece en el calendario.

El guion preparado proponía buscar apoyo en Matemáticas y reservar una tutoría sobre Ecuaciones para
pasado mañana a las 16:00. No se registraron por separado el tutor, tema, fecha u hora que Anna eligió,
ni un ID de documento de Firestore correspondiente a su sesión.

**Criterio de éxito observado según el equipo:** completar la reserva sin ayuda y localizarla después en el calendario.
La comprobación del documento en Firestore durante esta sesión no fue reportada.

## Resultado

| Paso | Resultado reportado | Tiempo | Observación |
|---|---|---|---|
| Elegir un tutor y completar el formulario | Completó la reserva sin ayuda; estos pasos no se registraron por separado | No registrado | No se reportaron dificultades concretas al llenar el formulario |
| Confirmar la reserva | Sí, sin ayuda | No registrado | El aviso de confirmación duró poco; quedó con dudas sobre si se había guardado |
| Encontrar la reserva en el calendario | Sí, sin ayuda | No registrado | Tardó un poco en ubicarla; según el relato, buscó primero en la semana actual y luego avanzó a la siguiente |
| Explorar la cancelación (consulta adicional) | Preguntó cómo cancelar; el relato inicial indica que finalmente lo logró | No registrado | La opción no fue evidente; no se registraron la ruta seguida ni la evidencia de cancelación |

**Resultado global:** tarea principal completada sin ayuda, con dificultades para reconocer la confirmación
y localizar la reserva en el calendario.

**Tiempo total:** no registrado. **Facilidad (1–5):** no registrada.
No se asignan tiempos, puntuaciones ni citas que no se hayan recogido durante la sesión.

## Hallazgos y seguimiento

| Hallazgo reportado | Efecto en la tarea | Acción propuesta | Estado |
|---|---|---|---|
| La confirmación desaparece demasiado rápido | Genera dudas sobre si la reserva quedó guardada | Revisar la duración del aviso y hacer persistente el estado de confirmación o el acceso al calendario | Propuesta; sin cambio de código en este registro |
| La reserva estaba en una semana distinta de la que revisó primero | Retrasa su localización | Confirmar la versión probada y revisar cómo se muestra la fecha o se lleva al usuario a la reserva recién creada | Pendiente de contrastar con la versión probada |
| La opción de cancelar no se identifica a simple vista | Lleva a preguntar cómo realizar esa acción | Confirmar dónde se realizó la cancelación y revisar su relación con el alcance de la entrega | Pendiente de contrastar; no se acredita una función de cancelación en la rama actual |

## Relación con la versión actual

La sesión ocurrió el 28 de octubre, antes de la grabación técnica del 30 de octubre.
En la rama `entrega-4` revisada para este registro, el calendario muestra listas de próximas y completadas,
sin navegación por semanas; el alcance documentado también deja fuera la interfaz de cancelación.
Por ello, las observaciones de avance de semana y cancelación se conservan como reportadas por el equipo,
con la versión y el recorrido por confirmar. No se presentan como funciones verificadas en el APK grabado.

## Evidencias relacionadas

- [Guion de la Entrega 3](../testeo/guion-sesion-testeo.md).
- [Alcance de la Entrega 4](alcance.md).
- [Video, capturas y comprobaciones automatizadas del 30 de octubre](evidencias/README.md).

La documentación de la sesión está completada con la información disponible. Quedan sin registrar los
tiempos, la valoración de facilidad, la versión utilizada y el detalle del recorrido de cancelación.
No se implementaron correcciones de interfaz como parte de esta transcripción.
