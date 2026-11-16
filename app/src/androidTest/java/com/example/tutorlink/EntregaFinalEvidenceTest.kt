package com.example.tutorlink

import android.graphics.Bitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate

/** Opt-in evidence against the unmodified, release-signed 1.2 (3) APK.
 * The demo may create a dedicated account and one real booking in Firebase.
 * Re-sign the instrumentation APK with the release certificate before installing.
 */
@RunWith(AndroidJUnit4::class)
class EntregaFinalEvidenceTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val output get() = File(instrumentation.targetContext.getExternalFilesDir(null), "entrega-final").apply { mkdirs() }
    private fun enabled() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("finalEvidence") == "true")
        val info = instrumentation.targetContext.packageManager.getPackageInfo("com.example.app_tutorias", 0)
        assertEquals("1.2", info.versionName)
        assertEquals(3L, info.longVersionCode)
    }
    private fun waitText(text: String, timeout: Long = 45_000) = rule.waitUntil(timeout) {
        rule.onAllNodes(hasText(text, substring = true)).fetchSemanticsNodes().isNotEmpty()
    }
    private fun click(text: String) {
        rule.onNode(hasText(text) and hasClickAction()).performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitForIdle()
        Thread.sleep(2_000)
    }
    private fun field(label: String, value: String, scroll: Boolean = true) {
        val node = rule.onNode(hasSetTextAction() and hasText(label))
        if (scroll) node.performScrollTo()
        node.performTextReplacement(value)
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        Thread.sleep(1_000)
    }
    private fun shot(name: String) {
        rule.waitForIdle()
        Thread.sleep(2_000)
        instrumentation.uiAutomation.takeScreenshot().let { bitmap ->
            File(output, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }
    @Test fun captureNonFatalFromRepository() {
        enabled()
        val app = instrumentation.targetContext.applicationContext as TutorLinkApplication
        val result = runBlocking {
            withTimeout(45_000) {
                app.container.authRepository.signIn("evidencia-no-registrada-20261115@example.com", "CredencialInvalida2026")
            }
        }
        assertTrue("Expected invalid-credentials failure", result.isFailure)
        File(output, "non-fatal.txt").writeText(
            "version=1.2 (3)\noperation=auth.signIn\nexception=${result.exceptionOrNull()?.javaClass?.name}\n" +
                "expected=true\nobservedAt=${java.time.Instant.now()}\n"
        )
        Thread.sleep(8_000)
        shot("01-app-tras-error-no-fatal")
    }
    @Test fun recordFinalFlow() {
        enabled()
        if (FirebaseAuth.getInstance().currentUser == null) {
            val args = InstrumentationRegistry.getArguments()
            val email = requireNotNull(args.getString("evidenceEmail"))
            val password = requireNotNull(args.getString("evidencePassword"))
            waitText("Crear una cuenta")
            click("Crear una cuenta")
            field("Nombre", "Estudiante Demo", scroll = false)
            field("Correo", email, scroll = false)
            field("Contraseña", password, scroll = false)
            click("Registrarme")
        }
        waitText("Ana Morales", 60_000)
        shot("02-inicio")
        click("Buscar")
        waitText("Ana Morales")
        shot("03-busqueda")
        click("Ana Morales")
        field("Materia o tema", "Resolución de ecuaciones cuadráticas")
        field("Fecha", LocalDate.now().plusDays(2).toString())
        field("Hora", "16:00")
        rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText("Reservar tutoría"))
        shot("04-formulario")
        click("Reservar tutoría")
        waitText("Tutoría reservada")
        shot("05-confirmacion")
        rule.onAllNodes(hasScrollAction()).onFirst().performScrollToNode(hasText("Ver calendario"))
        click("Ver calendario")
        waitText("Mis tutorías")
        waitText("Resolución de ecuaciones cuadráticas")
        shot("06-calendario")
        File(output, "demo.txt").writeText("version=1.2 (3)\nstatus=PASS\nrecordedAt=${java.time.Instant.now()}\n")
        Thread.sleep(4_000)
    }
}
