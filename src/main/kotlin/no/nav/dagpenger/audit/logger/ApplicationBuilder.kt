package no.nav.dagpenger.audit.logger

import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import mu.KotlinLogging
import no.nav.dagpenger.audit.logger.mottak.cef.CefAuditLoggerMottak
import no.nav.helse.rapids_rivers.RapidApplication

internal class ApplicationBuilder(config: Map<String, String>) : RapidsConnection.StatusListener {
    private val rapidsConnection = RapidApplication.create(config)

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
