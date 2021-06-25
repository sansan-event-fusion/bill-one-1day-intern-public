package billone.network.util.logback

import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Loggerの引数で渡した Pair<String, Any> をJSONの一部として含めるためのMarker。
 */
val ArgumentAsJSONMarker: Marker = MarkerFactory.getMarker("ARGUMENT_AS_JSON")
