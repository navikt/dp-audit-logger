package no.nav.dagpenger.audit.logger.mottak.cef

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class CefAuditLogger {
    private val auditlogger: KLogger = KotlinLogging.logger("auditLogger")
    private val sikkerLogger = KotlinLogging.logger("tjenestekall.CefAuditLogger")

    fun logEvent(event: String) {
        auditlogger.info { event }
        sikkerLogger.info { event }
    }
}
