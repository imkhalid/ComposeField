package com.imkhalid.composefield.composeField.fieldTypes

import androidx.compose.ui.text.input.KeyboardCapitalization

sealed class ComposeKeyboardCapitalOption(val value: Int){
    /** Capitalization behavior is not specified. */
    object UnSpecified: ComposeKeyboardCapitalOption(-1)

    /** Capitalize the first character of each sentence. */
    object Sentence: ComposeKeyboardCapitalOption(3)

    /** Capitalize the first character of every word. */
    object Words: ComposeKeyboardCapitalOption(2)

    /** Capitalize all characters. */
    object Characters: ComposeKeyboardCapitalOption(1)
    /** Do not auto-capitalize text. */
    object None: ComposeKeyboardCapitalOption(0)

    fun getKeyboardCapitalization(): KeyboardCapitalization {
        return when(value){
            0 -> KeyboardCapitalization.None
            1 -> KeyboardCapitalization.Characters
            2 -> KeyboardCapitalization.Words
            3 -> KeyboardCapitalization.Sentences
            else -> KeyboardCapitalization.Unspecified
        }
    }

    companion object{
        fun getOption(value: Int): ComposeKeyboardCapitalOption{
            return when(value){
                0 -> None
                1 -> Characters
                2 -> Words
                3 -> Sentence
                else -> UnSpecified
            }
        }
    }
}