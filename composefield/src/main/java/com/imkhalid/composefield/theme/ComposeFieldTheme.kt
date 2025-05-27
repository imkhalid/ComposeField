package com.imkhalid.composefield.theme

import androidx.compose.ui.graphics.Color

object ComposeFieldTheme {

    var focusedBorderColor = Color.Black
    var unfocusedBorderColor = Color.LightGray
    var errorColor = Color.Red
    var errorMessageColor = Color.Red
    var hintColor = Color.Gray
    var infoColor = Color.Gray
    var textColor = Color.Black
    var unfocusedLabelColor = Color.Gray
    var focusedLabelColor = Color.Gray
    var fieldStyle = FieldStyle.STICK_LABEL
    var datePickerHint = "Choose Date"
    var dropDownHint = "Choose an Option"
    var timePickerHint = "Choose Time"
    var containerColor = Color.LightGray
    var stickLabelFontSize = 20
    var stickFontSize = 16

    enum class FieldStyle {
        OUTLINE,
        CONTAINER,
        NORMAL,
        STICK_LABEL
    }
}
