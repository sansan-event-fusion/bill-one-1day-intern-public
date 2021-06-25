package sender.application_service

import sender.domain_event.DomainEventContext
import sender.util.runInTransaction

object SenderInvoiceRegisterService {
    // 課題2
    fun register(domainEventContext: DomainEventContext) {
        runInTransaction(domainEventContext) {}
    }
}
