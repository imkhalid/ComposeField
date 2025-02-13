package com.imkhalid.composefield.composeField

import androidx.compose.ui.graphics.Color

data class TableColors(
    val sectionTitleColor: Color = Color.Black,
    val sectionLimitColor: Color = Color.Gray.copy(alpha = 0.5f),
    val sectionDesColor: Color = Color.Gray.copy(alpha = 0.5f),
    val headerTextColor: Color = Color.Black,
    val headerBackgroundColor: Color = Color.White,
    val headerBorderColor: Color = Color.Black,
    val itemsLabelColor: Color = Color.Black.copy(0.5f),
    val itemsValueColor: Color = Color.Black,
)