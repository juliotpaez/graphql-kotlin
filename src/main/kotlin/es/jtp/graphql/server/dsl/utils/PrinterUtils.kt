package es.jtp.graphql.server.dsl.utils

internal object PrinterUtils {
    /**
     * Indents the input text.
     */
    internal fun indent(text: String) = text.lineSequence().map { "   $it" }.joinToString("\n")
}
