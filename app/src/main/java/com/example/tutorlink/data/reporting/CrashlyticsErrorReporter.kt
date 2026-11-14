package com.example.tutorlink.data.reporting

import com.example.tutorlink.domain.reporting.ErrorReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsErrorReporter(private val crashlytics: FirebaseCrashlytics) : ErrorReporter {
    override fun recordNonFatal(
        throwable: Throwable,
        operation: String
    ) {
        crashlytics.setCustomKey("operation", operation)
        crashlytics.log("Error no fatal capturado en $operation")
        crashlytics.recordException(throwable)
    }
}