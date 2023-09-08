package no.nav.dagpenger.audit.logger

import mu.KotlinLogging
import no.nav.dagpenger.audit.logger.cef.mottak.AuditLoggerMottak
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(config: Map<String, String>) : RapidsConnection.StatusListener {

    private val rapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(config)).build()

    private val logger = KotlinLogging.logger { }
    private val auditLogger = KotlinLogging.logger("auditlogger")
    init {
        AuditLoggerMottak(rapidsConnection, auditLogger)
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onReady(rapidsConnection: RapidsConnection) {
        logger.info { "Startet" }
    }
}
