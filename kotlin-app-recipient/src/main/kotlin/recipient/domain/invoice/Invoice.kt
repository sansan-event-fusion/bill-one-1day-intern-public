package recipient.domain.invoice

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import recipient.domain.recipient.RecipientUUID
import recipient.domain.sender.SenderUUID
import java.time.OffsetDateTime
import java.util.*

data class Invoice(
    val invoiceUUID: InvoiceUUID,
    val senderInvoiceUUID: SenderInvoiceUUID,
    val recipientUUID: RecipientUUID,
    val senderUUID: SenderUUID,
    val registeredAt: RegisteredAt
) {
    companion object {
        fun of(
            senderInvoiceUUID: SenderInvoiceUUID,
            recipientUUID: RecipientUUID,
            senderUUID: SenderUUID
        ) = Invoice(
            InvoiceUUID.random(),
            senderInvoiceUUID,
            recipientUUID,
            senderUUID,
            RegisteredAt.now()
        )
    }
}

data class InvoiceUUID @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: UUID) {
    companion object {
        fun random() = InvoiceUUID(UUID.randomUUID())
    }
}

data class SenderInvoiceUUID @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: UUID)
data class RegisteredAt @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: OffsetDateTime) {
    companion object {
        fun now() = RegisteredAt(OffsetDateTime.now())
    }
}
