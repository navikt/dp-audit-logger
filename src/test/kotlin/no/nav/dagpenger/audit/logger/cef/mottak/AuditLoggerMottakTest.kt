package no.nav.dagpenger.audit.logger.cef.mottak

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import mu.KLogger
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.UUID

class AuditLoggerMottakTest {
    private val auditlogger = mockk<KLogger>(relaxed = true)
    private val rapid by lazy {
        TestRapid().apply {
            AuditLoggerMottak(this, auditlogger)
        }
    }

    @AfterEach
    fun tearDown() {
        rapid.reset()
    }

    @Test
    fun `skal lese aktivitetslogger og lage auditloggmeldinger i CEF format `() {
        val loggMelding = slot<String>()
        every { auditlogger.info(capture(loggMelding)) } returns Unit
        val newMessage =
            JsonMessage.newMessage(
                "aktivitetslogg",
                mapOf(
                    "ident" to "ident",
                    "hendelse" to mapOf("type" to "bar", "meldingsreferanseId" to UUID.randomUUID()),
                    "aktiviteter" to emptyList<Any>(),
                ),
            )
        rapid.sendTestMessage(newMessage.toJson())
        loggMelding.isCaptured shouldBe true
        loggMelding.captured shouldContain "CEF:0|Vedtaksløsning for dagpenger|"
        println(loggMelding.captured)
        verify(exactly = 1) { auditlogger.info(loggMelding.captured) }
    }
}
