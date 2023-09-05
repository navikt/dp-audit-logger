package no.nav.dagpenger.audit.logger.cef

import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
/**
 * CEF = ArcSight Common Event Format*/

internal class CefAuditLoggerRiver(rapidsConnection: RapidsConnection) : RapidsConnection.MessageListener {
    override fun onMessage(message: String, context: MessageContext) {
        TODO("Not yet implemented")
    }
}
