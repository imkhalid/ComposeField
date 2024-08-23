package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.ozonedDigital.jhk.ui.common.responsiveTextSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComposeTimePickerField : ComposeField() {

    @Composable
    override fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        MyBuild(state = state, newValue = newValue, modifier = modifier)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MyBuild(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier
    ) {

        val dropDownText =
            if (state.text.isEmpty()) ComposeFieldTheme.timePickerHint
            else {
                changeDateFormat(date = state.text)
            }

        val label = buildAnnotatedString {
            withStyle(
                style =
                SpanStyle(
                    fontSize = responsiveTextSize(size = 13).sp,
                    color = ComposeFieldTheme.focusedLabelColor,
                )
            ) {
                append(state.field.label)
            }
            if (state.field.required == ComposeFieldYesNo.YES) {
                withStyle(
                    style =
                    SpanStyle(fontSize = responsiveTextSize(size = 13).sp, color = Color.Red)
                ) {
                    append("*")
                }
            }
        }

        val timePickerState = rememberTimePickerState()
        val showDialog = rememberSaveable { mutableStateOf(false) }
        if (showDialog.value) {
            Dialog(
                onDismissRequest = { showDialog.value = false },
            ) {
                Box(
                    modifier =
                    Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    val defColors = TimePickerDefaults.colors()
                    TimePicker(
                        modifier = Modifier.padding(vertical = 10.dp),
                        state = timePickerState,
                        colors =
                        TimePickerDefaults.colors(
                            clockDialColor =Color.White,
                            selectorColor = if (isSystemInDarkTheme())
                                defColors.selectorColor
                            else
                                ComposeFieldTheme.focusedBorderColor,
                            containerColor = ComposeFieldTheme.containerColor,
                            clockDialSelectedContentColor =Color.White,
                            clockDialUnselectedContentColor = ComposeFieldTheme.textColor,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorSelectedContainerColor = ComposeFieldTheme.focusedLabelColor,
                            timeSelectorUnselectedContentColor = Color.White,
                            timeSelectorUnselectedContainerColor = ComposeFieldTheme.focusedLabelColor.copy(0.5f)

                        )
                    )

                    TextButton(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onClick = {
                            val result =
                                "${
                                    String.format(
                                        "%02d",
                                        timePickerState.hour
                                    )
                                }:${String.format("%02d", timePickerState.minute)}:00"
                            newValue(Pair(true, ""), result)
                            showDialog.value = false
                        }
                    ) {
                        Text("Done")
                    }
                }
            }
        }

        Column(modifier = modifier) {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Box {
                TimePickerField(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog.value = true }
                ) {
                    Text(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 7.dp)
                            .align(Alignment.CenterStart),
                        color =
                        if (state.text.isEmpty()) ComposeFieldTheme.unfocusedLabelColor
                        else ComposeFieldTheme.textColor,
                        text = dropDownText,
                        fontWeight =
                        if (state.text.isEmpty()) FontWeight.Normal else FontWeight.Medium,
                        fontSize = responsiveTextSize(size = 15).sp
                    )
                    Text(
                        text = label,
                        fontSize = responsiveTextSize(size = 13).sp,
                        modifier = Modifier.padding(start = 20.dp, top = 7.dp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_clock_),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 15.dp)
                )
            }
        }
    }

    @Composable
    private fun TimePickerField(
        modifier: Modifier,
        onClick: () -> Unit,
        content: @Composable (BoxScope.() -> Unit)? = null
    ) {
        when (ComposeFieldTheme.fieldStyle) {
            ComposeFieldTheme.FieldStyle.OUTLINE ->
                Box(
                    modifier =
                    Modifier
                        .border(
                            border = BorderStroke(1.dp, ComposeFieldTheme.unfocusedBorderColor),
                            shape = OutlinedTextFieldDefaults.shape
                        )
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                        .height(OutlinedTextFieldDefaults.MinHeight)
                        .clickable { onClick() },
                ) {
                    content?.invoke(this)
                }

            ComposeFieldTheme.FieldStyle.CONTAINER,
            ComposeFieldTheme.FieldStyle.NORMAL ->
                Box(
                    modifier =
                    Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(TextFieldDefaults.MinHeight)
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .clickable { onClick() },
                ) {
                    content?.invoke(this)
                }
        }
    }

    fun changeDateFormat(from: String = "HH:mm:ss", to: String = "hh:mm aa", date: String): String {
        val date1 = parseToDate(from, date)
        return date1?.let { SimpleDateFormat(to, Locale.getDefault()).format(it) } ?: run { date }
    }

    fun parseToDate(to: String, date: String): Date? {
        return try {
            SimpleDateFormat(to, Locale.getDefault()).parse(date)
        } catch (e: Exception) {
            null
        }
    }
}
