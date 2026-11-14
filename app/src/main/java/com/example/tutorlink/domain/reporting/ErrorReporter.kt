package com.example.tutorlink.domain.reporting

interface ErrorReporter {
    fun recordNonFatal(
        throwable: Throwable,
        operation: String
    )
}