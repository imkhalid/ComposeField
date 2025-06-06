package com.imkhalid.composefield.composeField

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class TableConfig(
    val tableColors: TableColors = TableColors(),
    val showTitle: Boolean = false,
    val showLimit: Boolean = false,
    val showDescription:Boolean = false,
    val validationName:String = "Record(s)",
    val tableAddButton: @Composable (ColumnScope.(onClick: () -> Unit) -> Unit)? = null,
    val tablePopupButton: @Composable (BoxScope.(onClick: () -> Unit, data:HashMap<String,List<ComposeFieldStateHolder>>) -> Unit)?=null,
    val onChangeValue: (name:String, value:String, states: List<ComposeFieldStateHolder>, sectionName: String) -> Unit = { _, _, _, _-> },
)

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