package no.nav.dagpenger.audit.logger.cef.mottak

import mu.KLogger
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

internal class AuditLoggerMottak(rapidsConnection: RapidsConnection, private val auditlogger: KLogger) :
    River.PacketListener {

    init {
        River(rapidsConnection).validate {
            it.demandValue("@event_name", "aktivitetslogg")
            it.requireKey("@id")
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        auditlogger.info("Mottok aktivitetslogg: ${packet["@id"]}")
    }
}
