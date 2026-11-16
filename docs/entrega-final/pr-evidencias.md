# Secciones de evidencias para el PR

Estos enlaces de GitHub quedan disponibles cuando los archivos de este cambio se suban a `entrega-final`. Copiar las secciones siguientes en la descripción del PR; el resto de los requisitos (identificación, cross-review y observaciones atendidas) se completa por separado.

## Distribución y monitoreo

Versión **TutorLink 1.3 (4)** publicada en Firebase App Distribution con [notas de versión](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/notas-version-1.3.txt). El APK conserva el certificado de la versión anterior y su SHA-256 es `be1a237aa66524ea83b9c1dfb85107bbe244b6c674ecff92aea83570225e11fa`.

**Crashlytics:** [captura del evento no fatal de 1.3 (4)](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/evidencias/1.3/crashlytics-no-fatal.jpg) y [trazabilidad de la prueba](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/release-1.3.md).

**Error y decisión:** `FirebaseAuthInvalidCredentialsException`, capturada por `AuthRepositoryImpl.signIn()` al probar credenciales deliberadamente inválidas. **Esperado**: se rechaza el inicio de sesión y el repositorio registra el error como no fatal; no corresponde un commit de corrección. Se cerró y reabrió el build distribuido para enviar el reporte. La consola muestra un evento recuperable de **1.3 (4)**.

**Descargas externas:** la captura de al menos dos descargas de 1.3 (4) continúa pendiente. La ejecución en nuestro emulador no se cuenta como descarga de un tester externo.

## Demo

- [Diapositivas finales en PDF](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/demo/diapositivas-tutorlink.pdf).
- [Video continuo de respaldo del build 1.3 (4)](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/demo/video-respaldo.mp4): Inicio → Buscar → Perfil → Reserva confirmada → Calendario. Grabación real, sin cortes ni aceleración y de menos de cuatro minutos.
- [Guion de la exposición de ocho minutos](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/blob/entrega-final/docs/entrega-final/demo/guion-demo.md).
