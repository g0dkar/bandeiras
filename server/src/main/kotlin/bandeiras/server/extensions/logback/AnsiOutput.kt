package bandeiras.server.extensions.logback

object AnsiOutput {
    private val OS_NAME = System.getProperty("os.name").lowercase()
    private const val ENCODE_JOIN = ";"
    private const val ENCODE_START = "\u001b["
    private const val ENCODE_END = "m"
    private val RESET = "0;${AnsiCode.DEFAULT}"

    private val consoleAvailable: Boolean = System.console() != null
    private val ansiCapable: Boolean = detectAnsi()

    fun encode(element: AnsiCode): String {
        return if (ansiCapable) {
            "$ENCODE_START$element$ENCODE_END"
        } else {
            ""
        }
    }

    fun toString(vararg elements: Any?): String {
        val sb = StringBuilder()

        if (ansiCapable) {
            buildEnabled(sb, *elements)
        } else {
            buildDisabled(sb, *elements)
        }

        return sb.toString()
    }

    private fun buildEnabled(sb: StringBuilder, vararg elements: Any?) {
        var writingAnsi = false
        var containsEncoding = false

        for (element in elements) {
            if (element is AnsiCode) {
                containsEncoding = true
                if (!writingAnsi) {
                    sb.append(ENCODE_START)
                    writingAnsi = true
                } else {
                    sb.append(ENCODE_JOIN)
                }
            } else if (writingAnsi) {
                sb.append(ENCODE_END)
                writingAnsi = false
            }

            sb.append(element)
        }

        if (containsEncoding) {
            sb.append(if (writingAnsi) ENCODE_JOIN else ENCODE_START)
            sb.append(RESET)
            sb.append(ENCODE_END)
        }
    }

    private fun buildDisabled(sb: StringBuilder, vararg elements: Any?) {
        for (element in elements) {
            if (element != null && element !is AnsiCode) {
                sb.append(element)
            }
        }
    }

    private fun detectAnsi() =
        try {
            if (consoleAvailable) {
                !OS_NAME.contains("win")
            } else {
                false
            }
        } catch (ex: Throwable) {
            false
        }
}
