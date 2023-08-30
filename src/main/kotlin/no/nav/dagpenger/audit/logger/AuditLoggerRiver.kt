package no.nav.dagpenger.audit.logger

import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection

internal class AuditLoggerRiver(rapidsConnection: RapidsConnection) : RapidsConnection.MessageListener {
    override fun onMessage(message: String, context: MessageContext) {
        TODO("Not yet implemented")
    }
}
