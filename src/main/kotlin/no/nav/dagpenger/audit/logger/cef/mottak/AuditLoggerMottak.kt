package no.nav.dagpenger.audit.logger.cef.mottak

import mu.KLogger
import no.nav.common.audit_log.cef.AuthorizationDecision
import no.nav.common.audit_log.cef.CefMessage
import no.nav.common.audit_log.cef.CefMessageEvent
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class AuditLoggerMottak(rapidsConnection: RapidsConnection, private val auditlogger: KLogger) :
    River.PacketListener {

    init {
        River(rapidsConnection).validate {
            it.demandValue("@event_name", "aktivitetslogg")
            it.requireKey("@id")
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        CefMessage.builder()
            .event(CefMessageEvent.CREATE)
            .applicationName("Vedtaksløsning for dagpenger")
            .name("Aktivitetslogg")
            .authorizationDecision(AuthorizationDecision.DENY)
            .sourceUserId("Z123456")
            .destinationUserId("1234567891")
            .timeEnded(LocalDateTime.now().tilEpochMilli())
            .build().also {
                auditlogger.info(it.toString())
            }
    }

    private fun LocalDateTime.tilEpochMilli() =
        ZonedDateTime.of(this, ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli()
}
