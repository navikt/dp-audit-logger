package no.nav.dagpenger.audit.logger.cef

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

internal class CefAuditMeldingTest {

    @Test
    fun `skal ha CEF format`() {
        val appNavn = "app1"
        val fødselsnummer = "12345678901"
        val navIdent = "X123456"
        val eventName = "les-behandling"
        val id = UUID.randomUUID()
        val begrunnelse = "begrunnelse"
        val auditMelding = AuditMelding(
            navIdent = navIdent,
            app = appNavn,
            fødselsnummer = fødselsnummer,
            tidspunkt = LocalDateTime.now(),
            eventName = eventName,
            id = id,
            begrunnelse = begrunnelse,

        )

        assertSoftly {
            val cefFormat = auditMelding.formatterTilCef()
            val deler = cefFormat.split("|")

            deler.size shouldBe 8
            deler[0] shouldBe "CEF:0"
            deler[1] shouldBe "Vedtaksløsning for dagpenger"
            deler[2] shouldBe appNavn
            deler[3] shouldBe "1.0"
            deler[4] shouldBe "audit:update" // todo: burde parameteriseres? Type of the event (CRUD, audit:create, audit:access, audit:update, audit:delete)
            deler[5] shouldBe "Sporingslogg" // todo: burde parameteriseres?
            deler[6] shouldBe "INFO" // todo: burde parameteriseres (level) ? : Severity of the event, INFO or WARN
            val extensions = deler[7].split(" ").associate {
                val par = it.split("=")
                par[0] to par[1]
            }
            extensions["end"].shouldNotBeBlank()
            extensions["duid"] shouldBe fødselsnummer
            extensions["suid"] shouldBe navIdent
            extensions["request"] shouldBe eventName
            extensions["sproc"] shouldBe id.toString()
            extensions["msg"] shouldBe begrunnelse
        }
    }
}
