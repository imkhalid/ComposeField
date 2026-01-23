package com.imkhalid.composefield.composeField.mask

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.absoluteValue

class FieldMaskTransformation(val mask: String) : VisualTransformation {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != '#' }

    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (specialSymbolsIndices.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator())
    }

    private fun offsetTranslator() =
        object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val offsetValue = offset.absoluteValue
                if (offsetValue == 0) return 0
                var numberOfHashtags = 0
                val masked =
                    mask.takeWhile {
                        if (it == '#') numberOfHashtags++
                        numberOfHashtags < offsetValue
                    }
                return masked.length + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                return mask.take(offset.absoluteValue).count { it == '#' }
            }
        }

    fun applyMaskAndGetResult(input: String): String {
        // Create an instance of the transformation with your desired mask

        // Create an AnnotatedString from the input text
        val inputAnnotated = AnnotatedString(input)

        // Apply the transformation
        val transformed = this.filter(inputAnnotated)

        // Extract the masked text from the TransformedText object
        return transformed.text.text
    }
}

class FieldMaskInputTransformation(
    private val mask: String,
    private val allowed: (Char) -> Boolean = Char::isDigit
) : InputTransformation {

    private val maxRaw = mask.count { it == '#' }
    private val visual = FieldMaskTransformation(mask) // reuse your logic

    override fun TextFieldBuffer.transformInput() {
        val current = asCharSequence().toString()

        // Cursor in RAW space = how many allowed chars exist before current cursor
        val rawCursor = current
            .take(selection.start.coerceIn(0, current.length))
            .count(allowed)
            .coerceIn(0, maxRaw)

        // Raw text (remove separators), limited by # count
        val raw = current.filter(allowed).take(maxRaw)

        // Apply mask to produce the stored (masked) text
        val masked = visual.applyMaskAndGetResult(raw)

        // If no change, skip
        if (masked == current) return

        // Replace buffer with masked text
        replace(0, length, masked)

        // Map RAW cursor -> MASKED cursor (using same mapping logic as visual transformation)
        val newCursor = originalToTransformedOffset(rawCursor).coerceIn(0, masked.length)
        selection = TextRange(newCursor)
    }

    // Same idea as your offsetTranslator().originalToTransformed but without +1 bug risks.
    private fun originalToTransformedOffset(rawOffset: Int): Int {
        if (rawOffset <= 0) return 0
        var seenHashes = 0
        for (i in mask.indices) {
            if (mask[i] == '#') seenHashes++
            if (seenHashes == rawOffset) return i + 1
        }
        return mask.length
    }
}


