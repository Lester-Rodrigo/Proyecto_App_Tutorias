package com.example.app_tutorias

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/** Opt-in integration test against the configured Firebase project and an emulator.
 * Creates a test account and two reservations. Credentials must be supplied as runner arguments.
 * Captures the actual app and records server snapshots; it does not use mock repositories.
 */
@RunWith(AndroidJUnit4::class)
class Entrega4EvidenceTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val output get() = File(instrumentation.targetContext.getExternalFilesDir(null), "entrega4").apply { mkdirs() }
    private val results = JSONObject()
    private val steps = JSONArray()

    private fun shell(command: String): String = instrumentation.uiAutomation.executeShellCommand(command)
        .use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).bufferedReader().readText() }

    private fun waitText(text: String, timeout: Long = 30_000) {
        rule.waitUntil(timeout) { rule.onAllNodes(hasText(text, substring = true)).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun click(text: String) {
        rule.onNode(hasText(text, substring = false) and hasClickAction()).performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.mainClock.advanceTimeBy(1_000)
        rule.waitForIdle()
        Thread.sleep(3_000)
        if (text == "Buscar" && rule.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size > 1) {
            rule.onNodeWithContentDescription("Atrás").performSemanticsAction(SemanticsActions.OnClick) { it() }
            rule.mainClock.advanceTimeBy(1_000)
            rule.waitForIdle()
            Thread.sleep(3_000)
        }
    }

    private fun shot(name: String) {
        rule.mainClock.advanceTimeBy(1_000)
        rule.waitForIdle()
        Thread.sleep(5_000)
        instrumentation.uiAutomation.takeScreenshot().let { bitmap ->
            File(output, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
        steps.put(JSONObject().put("step", name).put("timeUtc", java.time.Instant.now().toString()))
        File(output, "results.json").writeText(results.put("steps", steps).toString(2))
    }

    private fun field(label: String, value: String) {
        rule.onNode(hasSetTextAction() and hasText(label)).performScrollTo().performTextReplacement(value)
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        Thread.sleep(2_000)
    }

    private fun reserve(topic: String, hour: String) {
        field("Materia o tema", topic)
        field("Fecha", LocalDate.now().plusDays(2).toString())
        field("Hora", hour)
        rule.onNodeWithText("Reservar tutoría").performScrollTo()
        shot(if (hour == "16:00") "02-formulario-conexion" else "06-formulario-sin-conexion")
        click("Reservar tutoría")
    }

    private fun serverSessions(): com.google.firebase.firestore.QuerySnapshot = Tasks.await(
        FirebaseFirestore.getInstance().collection("sessions")
            .whereEqualTo("studentId", FirebaseAuth.getInstance().currentUser!!.uid)
            .get(Source.SERVER), 30, TimeUnit.SECONDS)

    private fun saveServer(name: String): com.google.firebase.firestore.QuerySnapshot {
        val snapshot = serverSessions()
        val docs = JSONArray()
        snapshot.documents.forEach { doc ->
            val fields = JSONObject()
            doc.data.orEmpty().forEach { (key, value) ->
                fields.put(key, if (value is com.google.firebase.Timestamp) value.toDate().toInstant().toString() else value)
            }
            docs.put(JSONObject().put("id", doc.id).put("fields", fields)
                .put("hasPendingWrites", doc.metadata.hasPendingWrites()))
        }
        File(output, "$name.json").writeText(JSONObject().put("source", "Source.SERVER")
            .put("projectId", "app-tutorias-5710d").put("observedAtUtc", java.time.Instant.now().toString())
            .put("documents", docs).toString(2))
        return snapshot
    }

    @Test fun recordRealBookingOfflineSyncAndHistory() {
        val args = InstrumentationRegistry.getArguments()
        org.junit.Assume.assumeTrue("Requires explicit opt-in to create real Firebase reservations", args.getString("runLiveEvidence") == "true")
        val email = requireNotNull(args.getString("evidenceEmail")) { "Supply a dedicated evidenceEmail" }
        val password = requireNotNull(args.getString("evidencePassword")) { "Supply evidencePassword" }
        var recorderPid: String? = null
        try {
            if (FirebaseAuth.getInstance().currentUser == null) {
            waitText("Crear una cuenta")
            click("Crear una cuenta")
            rule.onNode(hasSetTextAction() and hasText("Nombre")).performTextReplacement("Prueba Entrega 4")
            rule.onNode(hasSetTextAction() and hasText("Correo")).performTextReplacement(email)
            rule.onNode(hasSetTextAction() and hasText("Contraseña")).performTextReplacement(password)
            click("Registrarme")
            }
            waitText("Ana Morales", 60_000)
            val baselineIds = serverSessions().documents.map { it.id }.toSet()
            results.put("uid", FirebaseAuth.getInstance().currentUser!!.uid)
            results.put("device", android.os.Build.MODEL).put("android", android.os.Build.VERSION.RELEASE)
            results.put("accountEmail", email)
            Thread.sleep(2_000)
            click("Buscar")
            waitText("Ana Morales")
            shot("01-catalogo-firestore")
            click("Ana Morales")
            reserve("Ecuaciones con conexion", "16:00")
            waitText("Tutoría reservada")
            shot("03-reserva-confirmada")
            rule.onNodeWithText("Ver calendario").performScrollTo()
            click("Ver calendario")
            waitText("Mis tutorías")
            waitText("Ecuaciones con conexion")
            shot("04-calendario-confirmado")
            val online = saveServer("reserva-online-servidor")
            assertEquals(1, online.documents.count { it.id !in baselineIds })
            results.put("onlineReservationId", online.documents.single { it.id !in baselineIds }.id)

            click("Buscar")
            rule.onNode(hasSetTextAction()).performTextReplacement("Ana")
            rule.onNode(hasSetTextAction()).performImeAction()
            rule.onNodeWithContentDescription("Limpiar búsqueda").performSemanticsAction(SemanticsActions.OnClick) { it() }
            waitText("Búsquedas recientes")

            shell("cmd connectivity airplane-mode enable")
            shell("svc wifi disable")
            shell("svc data disable")
            results.put("airplaneMode", shell("settings get global airplane_mode_on").trim())
            assertEquals("1", results.getString("airplaneMode"))
            rule.activityRule.scenario.recreate()
            waitText("Ana Morales", 30_000)
            click("Buscar")
            waitText("Búsquedas recientes")
            shot("05-historial-y-cache-modo-avion")
            click("Ana Morales")
            reserve("Ecuaciones sin conexion", "17:00")
            waitText("Pendiente de sincronizar", 25_000)
            shot("07-reserva-pendiente")
            rule.onNodeWithText("Ver calendario").performScrollTo()
            click("Ver calendario")
            waitText("Mis tutorías")
            waitText("Pendiente de sincronizar")
            shot("08-calendario-pendiente")

            shell("cmd connectivity airplane-mode disable")
            shell("svc wifi enable")

            Tasks.await(FirebaseFirestore.getInstance().waitForPendingWrites(), 45, TimeUnit.SECONDS)
            rule.waitUntil(30_000) { rule.onAllNodesWithText("Pendiente de sincronizar").fetchSemanticsNodes().isEmpty() }
            waitText("Ecuaciones sin conexion")
            shot("09-calendario-sincronizado")
            val synced = saveServer("reservas-sincronizadas-servidor")
            assertEquals(2, synced.documents.count { it.id !in baselineIds })
            assertEquals(1, synced.documents.count { it.id !in baselineIds && it.getString("subjectName") == "Ecuaciones sin conexion" })
            results.put("status", "PASS").put("sessionCount", synced.size())
            File(output, "results.json").writeText(results.toString(2))
            Thread.sleep(3_000)
        } catch (error: Throwable) {
            results.put("status", "FAIL").put("error", error.toString())
            try { shot("error") } catch (_: Throwable) {}
            throw error
        } finally {
            recorderPid?.takeIf { it.matches(Regex("[0-9]+")) }?.let { shell("kill -2 $it") }
            shell("cmd connectivity airplane-mode disable")
            shell("svc wifi enable")
            FirebaseFirestore.getInstance().enableNetwork()
            Thread.sleep(1_000)
            File(output, "results.json").writeText(results.toString(2))
        }
    }

    @Test fun captureColdStartOfflineHistory() {
        org.junit.Assume.assumeTrue("Requires an offline emulator with a previously signed-in test account", InstrumentationRegistry.getArguments().getString("coldStartEvidence") == "true")
        try {
            assertEquals("1", shell("settings get global airplane_mode_on").trim())
            waitText("Ana Morales", 30_000)
            click("Buscar")
            waitText("Búsquedas recientes")
            rule.onNodeWithText("Ana").assertExists()
            shot("10-historial-tras-cierre-completo-sin-red")
        } finally {
            shell("cmd connectivity airplane-mode disable")
            shell("svc wifi enable")
        }
    }
}
