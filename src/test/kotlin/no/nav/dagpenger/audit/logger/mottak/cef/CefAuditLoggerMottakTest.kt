package no.nav.dagpenger.audit.logger.mottak.cef

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
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
    fun `skal lese aktivitetslogger og lage INFO auditloggmeldinger i CEF format `() {
        val borgerIdent = "12345678911"
        val saksbehandlerNavIdent = "X123456"
        val melding = "infomelding"
        aktivitetslogg.info(
            melding,
            borgerIdent = borgerIdent,
            saksbehandlerNavIdent = saksbehandlerNavIdent,
            operasjon = AuditOperasjon.READ,
        )

        val loggMelding = slot<String>()
        every { auditlogger.info(capture(loggMelding)) } returns Unit
        val meldingsreferanseId = UUID.randomUUID()
        val aktivitet =
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "ident" to "ident",
                    "hendelse" to mapOf("type" to "bar", "meldingsreferanseId" to meldingsreferanseId),
                    "aktiviteter" to AktivitetsloggJsonBuilder(aktivitetslogg).asList(),
                ),
            )

        rapid.sendTestMessage(aktivitet.toJson())

        loggMelding.isCaptured shouldBe true
        val deler = loggMelding.captured.split("|")
        assertSoftly {
            deler.size shouldBe 8
            deler[0] shouldBe "CEF:0"
            deler[1] shouldBe "DAGPENGER"
            deler[2] shouldBe "AuditLogger"
            deler[3] shouldBe "1.0"
            deler[4] shouldBe "audit:access"
            deler[5] shouldBe "dagpenger-aktivitetslogg-ukjent"
            deler[6] shouldBe "INFO"
            val extensions =
                deler[7].split(" ").associate {
                    val par = it.split("=")
                    par[0] to par[1]
                }
            extensions["end"].shouldNotBeBlank()
            extensions["duid"] shouldBe borgerIdent
            extensions["suid"] shouldBe saksbehandlerNavIdent

            extensions["sproc"] shouldBe meldingsreferanseId.toString()
            extensions["msg"] shouldBe melding
        }

        verify(exactly = 1) { auditlogger.info(loggMelding.captured) }
    }

    @Test
    fun `Skal lese aktivitetslogger og lage WARN auditloggmeldinger i CEF format `() {
        val borgerIdent = "12345678911"
        val saksbehandlerNavIdent = "X123456"
        val melding = "Dette-er-en-audit-melding"
        aktivitetslogg.varsel(
            melding,
            borgerIdent = borgerIdent,
            saksbehandlerNavIdent = saksbehandlerNavIdent,
            operasjon = AuditOperasjon.READ,
        )

        val loggMelding = slot<String>()
        every { auditlogger.info(capture(loggMelding)) } returns Unit
        val meldingsreferanseId = UUID.randomUUID()
        val aktivitet =
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "ident" to "ident",
                    "hendelse" to mapOf("type" to "bar", "meldingsreferanseId" to meldingsreferanseId),
                    "aktiviteter" to AktivitetsloggJsonBuilder(aktivitetslogg).asList(),
                ),
            )

        rapid.sendTestMessage(aktivitet.toJson())

        loggMelding.isCaptured shouldBe true
        val deler = loggMelding.captured.split("|")
        assertSoftly {
            deler.size shouldBe 8
            deler[0] shouldBe "CEF:0"
            deler[1] shouldBe "DAGPENGER"
            deler[2] shouldBe "AuditLogger"
            deler[3] shouldBe "1.0"
            deler[4] shouldBe "audit:access"
            deler[5] shouldBe "dagpenger-aktivitetslogg-ukjent"
            deler[6] shouldBe "WARN"
            val extensions =
                deler[7].split(" ").associate {
                    val par = it.split("=")
                    par[0] to par[1]
                }
            extensions["end"].shouldNotBeBlank()
            extensions["duid"] shouldBe borgerIdent
            extensions["suid"] shouldBe saksbehandlerNavIdent

            extensions["sproc"] shouldBe meldingsreferanseId.toString()
            extensions["msg"] shouldBe melding
        }
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
