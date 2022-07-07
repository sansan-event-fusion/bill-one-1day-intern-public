package sender.util.logback

import billone.network.util.logback.ArgumentAsJSONMarker
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.contrib.json.classic.JsonLayout

class StackdriverLayout : JsonLayout() {
    override fun addCustomDataToJsonMap(map: MutableMap<String, Any>, event: ILoggingEvent) {
        // 参考
        // https://cloud.google.com/logging/docs/agent/configuration?hl=ja#special-fields
        // https://k11i.biz/blog/2018/10/03/stackdriver-logging-friendly-layout-java/

        // removeするキーは常に存在するはずだが、例外にするのも怖いので、万一nullの場合は適当な文字列を代入する。
        map["severity"] = map.remove("level") ?: "Something wrong"
        map["time"] = map.remove("timestamp") ?: "Something wrong"

        val mdc = map[MDC_ATTR_NAME]
        if (mdc is Map<*, *>) {
            mdc.forEach { (key, value) ->
                if (key is String && value != null) { // 型チェックのためにifで囲んでいるが常にtrueになる想定。
                    map[convertMdcKey(key)] = value
                }
            }
            map.remove(MDC_ATTR_NAME)
        }

        // ArgumentAsJSONMarker がついてるログは、引数のPairをJSONオブジェクトに追加する
        if (event.marker == ArgumentAsJSONMarker) {
            event.argumentArray.forEach { argument ->
                if (argument is Pair<*, *>) {
                    val (key, value) = argument
                    if (key is String && value != null) {
                        map[key] = value
                    }
                }
            }
        }

        // https://cloud.google.com/error-reporting/docs/formatting-error-messages?hl=ja
        // スタックトレースが存在しなくても、GCPのError Reportingに検知させる
        if (event.level == Level.ERROR) {
            map["@type"] = "type.googleapis.com/google.devtools.clouderrorreporting.v1beta1.ReportedErrorEvent"
        }
    }
}

private fun convertMdcKey(key: String): String {
    return when (key) {
        "trace" -> "logging.googleapis.com/trace"
        "spanId" -> "logging.googleapis.com/spanId"
        "traceSampled" -> "logging.googleapis.com/trace_sampled"
        else -> key
    }
}
