package bandeiras.server.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Adds a `log` field on all types
 */
internal inline val <reified T> T.log: Logger
    get() = LoggerFactory.getLogger(T::class.java.name)
