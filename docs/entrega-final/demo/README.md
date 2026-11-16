# Demo final de TutorLink

- [Diapositivas en PDF](diapositivas-tutorlink.pdf): **8 diapositivas**, con el estilo de TutorLink y capturas reales del build final.
- [Video de respaldo](video-respaldo.mp4): **41,37 segundos**, **720 × 1600**, 30 fps, sin audio.
- [Guion de exposición de 8 minutos](guion-demo.md).
- [Texto con enlaces para la descripción del PR](../pr-evidencias.md).

## Qué muestra el video

Grabación real del **15 de noviembre de 2026 (Guatemala)**, en un emulador Android 17 y con una cuenta exclusiva de demostración:

**Inicio → Buscar → Perfil de Ana Morales → Formulario → Reserva confirmada → Mis tutorías**.

Se registra una reserva real en Firebase y se comprueba que aparece en el calendario. El video incluye el arranque de la app; no tiene cortes, montaje ni cambios de velocidad. La conversión a 30 fps conserva la duración de la toma original. No es una secuencia de capturas ni reutiliza la grabación de la Entrega 4.

Se utilizó el APK **1.2 (3)** publicado en App Distribution. Su SHA-256 instalado se comparó con el publicado: [comprobación](../evidencias/apk-instalado-verificado.txt). La automatización es una instrumentación separada firmada con el mismo certificado; **no se recompiló ni alteró el APK de producción** para grabar.

## Evidencias del recorrido

- [Resultado de instrumentación: OK (1 test)](../evidencias/prueba-demo-final.log).
- [Metadatos del video](../evidencias/video-metadata.json).
- [Inicio](../evidencias/dispositivo/02-inicio.png).
- [Búsqueda](../evidencias/dispositivo/03-busqueda.png).
- [Formulario](../evidencias/dispositivo/04-formulario.png).
- [Confirmación](../evidencias/dispositivo/05-confirmacion.png).
- [Calendario](../evidencias/dispositivo/06-calendario.png).
- [Registro de versión y resultado](../evidencias/dispositivo/demo.txt).
- [Prueba optativa que ejecuta el recorrido](../../../app/src/androidTest/java/com/example/tutorlink/EntregaFinalEvidenceTest.kt), método `recordFinalFlow`.

## Alcance y uso

La grabación respalda el flujo principal con conexión; no presenta como repetidas las pruebas de rotación, actualización ni modo avión. Las evidencias anteriores de modo avión y sincronización se conservan en [Entrega 4](../../entrega-4/evidencias/README.md).

Las diapositivas se revisaron mediante renderizado de sus ocho páginas. El video se decodificó y se inspeccionaron fotogramas del inicio, búsqueda, formulario, confirmación y calendario. Preparación de diapositivas, automatización y documentación asistida por OpenAI Codex; las capturas, el evento y el recorrido provienen de la app y Firebase reales.

Los enlaces externos preparados para el PR estarán disponibles después del commit y push de estos archivos a `entrega-final`.
