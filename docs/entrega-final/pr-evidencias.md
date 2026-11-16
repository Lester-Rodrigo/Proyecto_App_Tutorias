# Secciones de evidencias para el PR

Estos enlaces de GitHub quedan disponibles cuando los archivos de este cambio se suban a `entrega-final`. Copiar las secciones siguientes en la descripción del PR; el resto de los requisitos (identificación, cross-review y observaciones atendidas) se completa por separado.

## Distribución y monitoreo

Versión **TutorLink 1.2 (3)** publicada en Firebase App Distribution con [notas de versión](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/notas-version-1.2.txt). El APK conserva el certificado de la versión anterior y su SHA-256 es `75ba1f4fb8d4f858cee575c224bffcf3981ff3ea9f19c5035b63bf2a317db33c`.

**Crashlytics:** [captura del evento no fatal de 1.2 (3)](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/evidencias/crashlytics-1.2-no-fatal.jpg) y [trazabilidad de la prueba](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/crashlytics.md).

**Error y decisión:** `FirebaseAuthInvalidCredentialsException`, capturada por `AuthRepositoryImpl.signIn()` al probar credenciales deliberadamente inválidas. **Esperado**: se rechaza el inicio de sesión y el repositorio registra el error como no fatal; no corresponde un commit de corrección. Se cerró y reabrió el build distribuido para enviar el reporte. La consola muestra un evento recuperable de **1.2 (3)**.

**Descargas externas:** la captura de al menos dos descargas de 1.2 (3) continúa pendiente. La ejecución en nuestro emulador no se cuenta como descarga de un tester externo.

## Demo

- [Diapositivas finales en PDF](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/demo/diapositivas-tutorlink.pdf).
- [Video continuo de respaldo del build 1.2 (3)](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/demo/video-respaldo.mp4): Inicio → Buscar → Perfil → Reserva confirmada → Calendario. Grabación real, sin cortes ni aceleración y de menos de cuatro minutos.
- [Guion de la exposición de ocho minutos](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/demo/guion-demo.md).
