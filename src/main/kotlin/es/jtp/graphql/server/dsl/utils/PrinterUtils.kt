package es.jtp.graphql.server.dsl.utils

import kotlin.reflect.*

internal object PrinterUtils {
    /**
     * Indents the input text.
     */
    internal fun indent(text: String) = text.lineSequence().map { "   $it" }.joinToString("\n")

    /**
     * Prints a [KType] using reflection.
     */
    internal fun printType(kType: KType): String = StringBuilder().apply {
        val classifier = kType.classifier as KClass<*>
        if (classifier.java === List::class.java) {
            // List type
            val internalType = kType.arguments[0].type!!

            append("[")
            append(printType(internalType))
            append("]")
        } else {
            // Type name
            append(classifier.simpleName)
        }

        if (!kType.isMarkedNullable) {
            append("!")
        }
    }.toString()
}
