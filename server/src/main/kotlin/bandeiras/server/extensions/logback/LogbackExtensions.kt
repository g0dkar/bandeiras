package bandeiras.server.extensions.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter
import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.CoreConstants.LINE_SEPARATOR
import ch.qos.logback.core.pattern.CompositeConverter

/**
 * Built from:
 * - `org.springframework.boot.logging.logback.ColorConverter`
 * - `org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter`
 * - `org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter`.
 *
 * @author Phillip Webb
 */
class ColorConverter : CompositeConverter<ILoggingEvent>() {
    companion object {
        private val ELEMENTS: Map<String, AnsiCode> = mapOf(
            "faint" to AnsiCode.STYLE_FAINT,
            "red" to AnsiCode.RED,
            "green" to AnsiCode.GREEN,
            "yellow" to AnsiCode.YELLOW,
            "blue" to AnsiCode.BLUE,
            "magenta" to AnsiCode.MAGENTA,
            "cyan" to AnsiCode.CYAN,
        )

        private val LEVELS: Map<Int, AnsiCode> = mapOf(
            Level.ERROR_INTEGER to AnsiCode.RED,
            Level.WARN_INTEGER to AnsiCode.YELLOW,
        )
    }

    override fun transform(event: ILoggingEvent, input: String): String {
        val element: AnsiCode = ELEMENTS[firstOption] ?: LEVELS[event.level.toInteger()] ?: AnsiCode.GREEN
        return AnsiOutput.toString(element, input)
    }
}

class WhitespaceThrowableProxyConverter : ThrowableProxyConverter() {
    override fun throwableProxyToString(tp: IThrowableProxy?): String =
        "$LINE_SEPARATOR${super.throwableProxyToString(tp)}$LINE_SEPARATOR"
}

class ExtendedWhitespaceThrowableProxyConverter : ExtendedThrowableProxyConverter() {
    override fun throwableProxyToString(tp: IThrowableProxy?): String =
        "$LINE_SEPARATOR${super.throwableProxyToString(tp)}$LINE_SEPARATOR"
}
