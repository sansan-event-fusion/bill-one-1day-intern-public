package recipient.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun configureObjectMapper(objectMapper: ObjectMapper) {
    with(objectMapper) {
        registerModule(JavaTimeModule()) // OffsetDateTimeなどをシリアライズ・デシリアライズできるようにする。
        configure(SerializationFeature.INDENT_OUTPUT, true) // 出力をインデントする。
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // OffsetDateTimeなどはUnix Timestampではなく、文字列を使う。
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // パース時に未知のプロパティがあっても例外にしない。
    }
}

fun configuredJacksonObjectMapper(): ObjectMapper {
    val objectMapper = jacksonObjectMapper()
    configureObjectMapper(objectMapper)
    return objectMapper
}
