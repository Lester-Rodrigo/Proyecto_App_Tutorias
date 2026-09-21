# Guion de la sesión de testeo · TutorLink

**Rama evaluada:** `pantallas-mock`
**Responsable:** Persona 3 (testeo y reporte)
**Duración estimada:** 15–20 minutos por participante
**Participantes sugeridos:** 3–5 (cliente + estudiantes que buscarían una tutoría)

---

## 1. Preparación (antes de la sesión)

1. Instalar la app en un teléfono o emulador (ver [Instrucciones](#6-instrucciones-para-correr-la-app)).
2. Abrir la app en la pantalla **Iniciar sesión**.
3. Tener listo este guion y la [hoja de observación](#5-hoja-de-observación).
4. Pedir permiso para tomar notas o grabar la pantalla.

## 2. Introducción para el participante (leer en voz alta)

> "Gracias por ayudarnos. Vamos a probar una app para reservar tutorías. No te estamos evaluando a ti,
> sino a la app: si algo no se entiende, es culpa del diseño. Piensa en voz alta mientras la usas:
> dinos qué buscas, qué esperas que pase y qué te sorprende. Yo no te voy a ayudar durante las tareas,
> pero al final platicamos."

## 3. Tareas

Leer cada tarea sin mencionar nombres de botones. Anotar si la completa, el tiempo y dónde duda.

| # | Tarea (lo que se le dice al participante) | Pantallas | Éxito cuando… |
|---|---|---|---|
| T1 | "Entra a la app y cuéntame qué puedes hacer desde la primera pantalla." | Login → Inicio | Describe búsqueda, tutores y próxima tutoría |
| T2 | "Necesitas ayuda con **Física**. Encuentra un tutor que te sirva." | Inicio → Buscar | Llega a Carlos Méndez usando búsqueda o filtro |
| T3 | "Quieres ver solo tutores de **Idiomas**." | Buscar | Activa el filtro Idiomas y ve a Sofía Herrera |
| T4 | "Reserva una tutoría con ese tutor para **pasado mañana a las 4 de la tarde**, sobre *Ecuaciones*." | Detalle y reserva | Botón *Reservar tutoría* habilitado y mensaje de confirmación |
| T5 | "Comprueba que tu reserva quedó guardada." | Calendario | Encuentra la sesión en *Próximas* |
| T6 | "¿Dónde verías las tutorías que ya tomaste?" | Calendario | Usa el filtro *Completadas* |

## 4. Preguntas de cierre

1. Del 1 al 5, ¿qué tan fácil fue reservar una tutoría? ¿Por qué?
2. ¿Qué fue lo que **más te gustó** o te pareció más claro?
3. ¿En qué momento **te confundiste** o dudaste?
4. ¿Esperabas que algo hiciera otra cosa al tocarlo?
5. ¿Qué agregarías o quitarías?

## 5. Hoja de observación

Copiar una tabla por participante.

**Participante:** ______ · **Perfil:** ______ · **Dispositivo:** ______ · **Fecha:** ______

| Tarea | ¿Completó? (Sí / Con ayuda / No) | Tiempo | Dudas, errores, citas textuales |
|---|---|---|---|
| T1 | | | |
| T2 | | | |
| T3 | | | |
| T4 | | | |
| T5 | | | |
| T6 | | | |

**Facilidad (1–5):** ___
**Lo que funciona:** ______
**Lo que confunde:** ______

## 6. Instrucciones para correr la app

Requisitos: Android Studio (con su JDK), Android SDK y un emulador o teléfono con depuración USB.

```bash
git clone https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias.git
cd Proyecto_App_Tutorias
git checkout pantallas-mock
```

**Opción A – Android Studio:** *File › Open* → seleccionar la carpeta → esperar la sincronización de Gradle →
elegir un emulador → **Run ▶ app**.

**Opción B – línea de comandos (Windows):**

```bash
./gradlew testDebugUnitTest assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.app_tutorias/.MainActivity
```

También se puede usar `scripts/run-android.ps1`, que compila, abre el emulador e instala la app.

> Si Gradle no encuentra el SDK, crear `local.properties` en la raíz con
> `sdk.dir=C\:/Users/<usuario>/AppData/Local/Android/Sdk` (este archivo no se sube al repo).
