package no.nav.dagpenger.audit.logger

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

internal class ApplicationBuilder(config: Map<String, String>) : RapidsConnection.StatusListener {

    private val rapidsConnection =
        RapidApplication.Builder(RapidApplication.RapidApplicationConfig.fromEnv(config)).build()

    private val logger = KotlinLogging.logger { }
    init {
        AuditLoggerRiver(rapidsConnection)
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onReady(rapidsConnection: RapidsConnection) {
        logger.info { "Startet" }
    }
}
