package no.nav.dagpenger.audit.logger.cef

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

/**
 * CEF = ArcSight Common Event Format
 *
 *
 * https://github.com/navikt/naudit
 *
 *
 * The log string needs to follow this format: CEF:Version|Device Vendor|Device Product|Device Version|Device Event Class ID|Name|Severity|[Extension] where Extension holds key-value pairs, some of which are standard, and others that may be specific to your context.
 *
 *
 */
internal class AuditMelding(
    private val navIdent: String,
    private val app: String,
    private val fødselsnummer: String,
    tidspunkt: LocalDateTime,
    private val eventName: String,
    private val id: UUID,
    private val begrunnelse: String,
) {

    private val end = ZonedDateTime.of(tidspunkt, ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli()

    fun formatterTilCef(): String {
        return "CEF:0|Vedtaksløsning for dagpenger|$app|1.0|audit:update|Sporingslogg|INFO|end=$end duid=$fødselsnummer suid=$navIdent request=$eventName sproc=$id msg=$begrunnelse"
    }
}
