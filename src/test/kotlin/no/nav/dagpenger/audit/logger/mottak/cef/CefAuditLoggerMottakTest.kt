package no.nav.dagpenger.audit.logger.mottak.cef

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import mu.KLogger
import no.nav.dagpenger.aktivitetslogg.Aktivitetslogg
import no.nav.dagpenger.aktivitetslogg.AuditOperasjon
import no.nav.dagpenger.aktivitetslogg.serde.AktivitetsloggJsonBuilder
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

internal class CefAuditLoggerMottakTest {
    private val auditlogger = mockk<KLogger>(relaxed = true)
    private lateinit var aktivitetslogg: Aktivitetslogg
    private val rapid by lazy {
        TestRapid().apply {
            CefAuditLoggerMottak(this, auditlogger)
        }
    }

    @BeforeEach
    fun setup() {
        aktivitetslogg = Aktivitetslogg()
    }

    @AfterEach
    fun tearDown() {
        rapid.reset()
    }

    @Test
    fun `skal lese aktivitetslogger og lage auditloggmeldinger i CEF format `() {
        aktivitetslogg.info(
            "Dette er en audit melding",
            borgerIdent = "12345678911",
            saksbehandlerNavIdent = "X123456",
            operasjon = AuditOperasjon.READ,
        )

        val loggMelding = slot<String>()
        every { auditlogger.info(capture(loggMelding)) } returns Unit
        val aktivitet =
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "ident" to "ident",
                    "hendelse" to mapOf("type" to "bar", "meldingsreferanseId" to UUID.randomUUID()),
                    "aktiviteter" to AktivitetsloggJsonBuilder(aktivitetslogg).asList(),
                ),
            )

        rapid.sendTestMessage(aktivitet.toJson())

        loggMelding.isCaptured shouldBe true
        loggMelding.captured shouldContain "CEF:0|DAGPENGER|AuditLogger|1.0|audit:access|dagpenger-aktivitetslogg-ukjent|INFO|flexString1=Permit msg=Dette er en audit melding duid=12345678911 flexString1Label=Decision"
        verify(exactly = 1) { auditlogger.info(loggMelding.captured) }
    }

    @Test
    fun `Skal kun lese aktivitetslogg med AUDIT aktivitet`() {
        val aktivitet =
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "ident" to "ident",
                    "hendelse" to mapOf("type" to "bar", "meldingsreferanseId" to UUID.randomUUID()),
                    "aktiviteter" to AktivitetsloggJsonBuilder(aktivitetslogg).asList(),
                ),
            )

        rapid.sendTestMessage(aktivitet.toJson())
        verify(exactly = 0) { auditlogger.info(any() as String) }
    }
}
