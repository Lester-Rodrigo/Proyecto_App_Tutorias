# TutorLink · Proyecto App Tutorías

App Android para buscar tutores y reservar tutorías, hecha con **Jetpack Compose**, **Material Design 3**
y **Navigation Compose**. El flujo principal usa Firebase Authentication y Firestore mediante repositorios.
Consulta la [configuración y verificación de Firestore](docs/firestore.md).

## Entrega 3 · Prototipo navegable

| Evidencia | Enlace |
|---|---|
| Informe final (testeo, hallazgos e instrucciones) | [PDF](docs/testeo/informe-final.pdf) · [Markdown](docs/testeo/informe-final.md) |
| Guion de la sesión de testeo | [docs/testeo/guion-sesion-testeo.md](docs/testeo/guion-sesion-testeo.md) |
| Diagrama del flujo y capturas | [docs/testeo/wireframes](docs/testeo/wireframes) · [docs/testeo/capturas](docs/testeo/capturas) |
| Pull request del informe | [#1](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/pull/1) |
| Pull request del proyecto Android | [#2](https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/pull/2) |

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Iniciar sesión | Entrada a la app |
| Inicio | Saludo, búsqueda, materias populares, tutores recomendados y próxima tutoría |
| Buscar | Búsqueda en tiempo real y filtro por materia |
| Detalle y reserva | Perfil del tutor y formulario con validación en tiempo real (tema, fecha, hora y notas) |
| Mis tutorías | Sesiones próximas y completadas |
| Configuración | Cerrar sesión |

## Cómo correr la app

Requisitos: Android Studio (incluye el JDK), Android SDK y un emulador o teléfono con depuración USB.

```bash
git clone https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias.git
cd Proyecto_App_Tutorias
git checkout pantallas-mock
```

**En Android Studio:** *File › Open* → elegir la carpeta → esperar la sincronización de Gradle → **Run ▶ app**.

**Por línea de comandos:**

```bash
./gradlew testDebugUnitTest assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Si Gradle no encuentra el SDK, crear `local.properties` con
`sdk.dir=C\:/Users/<usuario>/AppData/Local/Android/Sdk`.

## Estructura

```
app/src/main/java/com/example/app_tutorias/
├── MainActivity.kt   # Punto de entrada
├── navigation/       # Rutas y NavHost
├── data/             # Modelos, datos mock, validación y filtros
└── ui/               # ViewModel, componentes, pantallas y tema Material 3
```

## Uso de IA

Parte de la documentación de testeo se elaboró con apoyo de Claude (Anthropic). El detalle está en la
[sección 9 del informe](docs/testeo/informe-final.md#9-uso-de-inteligencia-artificial).
