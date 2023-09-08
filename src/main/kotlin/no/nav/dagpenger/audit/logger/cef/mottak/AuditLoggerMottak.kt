package no.nav.dagpenger.audit.logger.cef.mottak

import mu.KLogger
import no.nav.dagpenger.audit.logger.cef.AuditMelding
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.time.LocalDateTime

internal class AuditLoggerMottak(rapidsConnection: RapidsConnection, private val auditlogger: KLogger) :
    River.PacketListener {

    init {
        River(rapidsConnection).validate {
            it.demandValue("@event_name", "aktivitetslogg")
            it.requireKey("@id")
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        AuditMelding(
            navIdent = "quod",
            app = "explicari",
            fødselsnummer = "ut",
            tidspunkt = LocalDateTime.now(),
            eventName = "Lucille Armstrong",
            id = packet["@id"].asText(),
            begrunnelse = "expetenda",

        ).also {
            auditlogger.info(it.formatterTilCef())
        }
    }
}
