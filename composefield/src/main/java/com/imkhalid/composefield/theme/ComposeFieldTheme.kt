package com.imkhalid.composefield.theme

import androidx.compose.ui.graphics.Color

object ComposeFieldTheme {

    var focusedBorderColor = Color.Black
    var unfocusedBorderColor = Color.LightGray
    var errorColor = Color.Red
    var hintColor = Color.Gray
    var infoColor = Color.Gray
    var textColor = Color.Black
    var unfocusedLabelColor = Color.Gray
    var focusedLabelColor = Color.Gray
    var fieldStyle =FieldStyle.NORMAL

    enum class FieldStyle {
        OUTLINE,
        CONTAINER,
        NORMAL
    }
}