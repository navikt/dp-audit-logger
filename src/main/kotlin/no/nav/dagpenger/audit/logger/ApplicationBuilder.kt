package no.nav.dagpenger.audit.logger

import mu.KotlinLogging
import no.nav.dagpenger.audit.logger.mottak.cef.CefAuditLoggerMottak
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(config: Map<String, String>) : RapidsConnection.StatusListener {
    private val rapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(config)).build()

    init {
        CefAuditLoggerMottak(rapidsConnection)
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onReady(rapidsConnection: RapidsConnection) {
        logger.info { "Startet" }
    }

    private companion object {
        private val logger = KotlinLogging.logger { }
    }
}
