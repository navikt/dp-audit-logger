package no.nav.dagpenger.audit.logger.cef.mottak

import com.fasterxml.jackson.databind.JsonNode
import mu.KLogger
import no.nav.common.audit_log.cef.AuthorizationDecision
import no.nav.common.audit_log.cef.CefMessage
import no.nav.common.audit_log.cef.CefMessageEvent
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class AuditLoggerMottak(rapidsConnection: RapidsConnection, private val auditlogger: KLogger) :
    River.PacketListener {

    init {
        River(rapidsConnection).validate {
            it.demandValue("@event_name", "aktivitetslogg")
            it.requireKey("@id", "@opprettet")
            it.requireArray("aktiviteter") {
                demandValue("alvorlighetsgrad", "AUDIT")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val cefMessages = packet["aktiviteter"].filter {
            it["alvorlighetsgrad"].asText() == "AUDIT"
        }.flatMap { aktivitet ->
            val melding = aktivitet["melding"]
            aktivitet["kontekster"].filter { it["kontekstType"].asText() == "audit" }.map { kontekst ->
                val kontekstMap = kontekst["kontekstMap"]
                CefMessage.builder()
                    .event(kontekstMap.operasjon())
                    .applicationName("Dagpenger")
                    .name(kontekstMap["appName"].asText())
                    .authorizationDecision(kontekstMap.alvorlighetsgrad())
                    .sourceUserId(kontekstMap["saksbehandlerNavIdent"].asText())
                    .destinationUserId(kontekstMap["borgerIdent"].asText())
                    .timeEnded(packet["@opprettet"].asLocalDateTime().tilEpochMilli())
                    .extension("msg", melding.asText())
                    .build()
            }
        }

        cefMessages.forEach {
            auditlogger.info(it.toString())
        }
    }

    private fun LocalDateTime.tilEpochMilli() =
        ZonedDateTime.of(this, ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli()

    private fun JsonNode.alvorlighetsgrad(): AuthorizationDecision {
        return when (this["alvorlighetsgrad"].asText()) {
            "INFO" -> AuthorizationDecision.PERMIT
            "WARN" -> AuthorizationDecision.DENY
            else -> throw IllegalArgumentException("Kjenner ikke alvorlighetsgrad ${this["alvorlighetsgrad"].asText()} ")
        }
    }

    private fun JsonNode.operasjon(): CefMessageEvent {
        return when (this["operasjon"].asText()) {
            "READ" -> CefMessageEvent.ACCESS
            "CREATE" -> CefMessageEvent.CREATE
            "UPDATE" -> CefMessageEvent.UPDATE
            "DELETE" -> CefMessageEvent.DELETE
            else -> throw IllegalArgumentException("Kjenner ikke operasjon ${this["operasjon"].asText()} ")
        }
    }
}
