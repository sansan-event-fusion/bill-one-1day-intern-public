package sender.domain.sender_invoice

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import java.time.OffsetDateTime
import java.util.*

data class SenderInvoice(
    val senderInvoiceUUID: SenderInvoiceUUID,
    val recipientUUID: RecipientUUID,
    val senderUUID: SenderUUID,
    val registeredAt: RegisteredAt,
){
    companion object {
        fun of(recipientUUID: RecipientUUID, senderUUID: SenderUUID) = SenderInvoice(
            SenderInvoiceUUID.random(),
            recipientUUID,
            senderUUID,
            RegisteredAt.now()
        )
    }
}

data class SenderInvoiceUUID @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: UUID){
    companion object {
        fun random() = SenderInvoiceUUID(UUID.randomUUID())
    }
}
data class RegisteredAt  @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: OffsetDateTime){
    companion object{
        fun now() = RegisteredAt(OffsetDateTime.now())
    }
}


