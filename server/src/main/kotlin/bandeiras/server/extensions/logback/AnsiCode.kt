package bandeiras.server.extensions.logback

enum class AnsiCode(private val code: String) {
    // Styles
    STYLE_NORMAL("0"),
    STYLE_BOLD("1"),
    STYLE_FAINT("2"),
    STYLE_ITALIC("3"),
    STYLE_UNDERLINE("4"),

    // Colors
    DEFAULT("39"),
    BLACK("30"),
    RED("31"),
    GREEN("32"),
    YELLOW("33"),
    BLUE("34"),
    MAGENTA("35"),
    CYAN("36"),
    WHITE("37"),
    BRIGHT_BLACK("90"),
    BRIGHT_RED("91"),
    BRIGHT_GREEN("92"),
    BRIGHT_YELLOW("93"),
    BRIGHT_BLUE("94"),
    BRIGHT_MAGENTA("95"),
    BRIGHT_CYAN("96"),
    BRIGHT_WHITE("97");

    override fun toString(): String = code
}
