package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.ozonedDigital.jhk.ui.common.responsiveTextSize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ComposeDatePickerField : ComposeField() {


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

        val minDate = parseToDate(to = "yyyy-MM-dd", date = state.field.minValue ?: "")
        val maxDate = parseToDate(to = "yyyy-MM-dd", date = state.field.maxValue ?: "")
        val minMil: Long? = minDate?.let {
            Calendar.getInstance().apply {
                time = it
            }.timeInMillis
        }
        val maxMil: Long? = maxDate?.let {
            Calendar.getInstance().apply {
                time = it
            }.timeInMillis
        }

        val rangeMin = if (minDate!=null){
            Calendar.getInstance().apply { time = minDate }.get(Calendar.YEAR)
        }else{
            DatePickerDefaults.YearRange.first
        }
        val rangeMax = if (maxDate!=null){
            Calendar.getInstance().apply { time = maxDate }.get(Calendar.YEAR)
        }else{
            DatePickerDefaults.YearRange.last
        }

        val label = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = responsiveTextSize(size = 13).sp,
                    color = ComposeFieldTheme.focusedLabelColor
                )
            ) {
                append(state.field.label)
            }
            if (state.field.required == ComposeFieldYesNo.YES) {
                withStyle(
                    style = SpanStyle(
                        fontSize = responsiveTextSize(size = 13).sp,
                        color = Color.Red
                    )
                ) {
                    append("*")
                }
            }
        }

        val calendar = Calendar.getInstance()
        val dropDownText = if (state.text.isEmpty())
            ComposeFieldTheme.datePickerHint
        else {
            changeDateFormat(date = state.text)
        }


        val datePickerState = DatePickerState(
            locale = CalendarLocale.getDefault(),
            initialSelectedDateMillis = if (maxMil != null && maxMil < calendar.timeInMillis) maxMil else calendar.timeInMillis,
            yearRange = rangeMin..rangeMax,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return (minMil == null || (minMil < utcTimeMillis)) &&
                            (maxMil == null || (maxMil > utcTimeMillis))
                }
            }
        )
        val showDialog = rememberSaveable { mutableStateOf(false) }
        if (showDialog.value) {
            DatePickerDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog.value = false
                        datePickerState.selectedDateMillis?.let {
                            calendar.timeInMillis = it
                            val result = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(calendar.time)
                            newValue(Pair(true, ""), result)
                        }
                    }) {
                        Text("Ok")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                )
            }
        }

        Column(modifier = modifier) {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                DatePickerField(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog.value = true },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 7.dp)
                            .align(Alignment.CenterStart),
                        color = if (state.text.isEmpty()) ComposeFieldTheme.unfocusedLabelColor else ComposeFieldTheme.textColor,
                        text = dropDownText,
                        fontWeight = if (state.text.isEmpty()) FontWeight.Normal else FontWeight.Medium,
                        fontSize = responsiveTextSize(size = 15).sp
                    )
                    Text(
                        text = label,
                        color = ComposeFieldTheme.hintColor,
                        fontSize = responsiveTextSize(size = 13).sp,
                        modifier = Modifier.padding(start = 20.dp, top = 7.dp)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 10.dp)
                )
            }
            if (state.hasError) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            } else if (state.field.helperText.isNotEmpty())
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 7.dp),
                    color = ComposeFieldTheme.unfocusedLabelColor,
                    text = state.field.helperText,
                    fontWeight = FontWeight.Normal,
                    fontSize = responsiveTextSize(size = 11).sp
                )
        }

    }

    @Composable
    private fun DatePickerField(
        modifier: Modifier,
        onClick: () -> Unit,
        content: @Composable (BoxScope.() -> Unit)? = null
    ) {
        when (ComposeFieldTheme.fieldStyle) {
            ComposeFieldTheme.FieldStyle.OUTLINE -> Box(
                modifier = Modifier
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
            ComposeFieldTheme.FieldStyle.NORMAL -> Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .clickable { onClick() },
            ) {
                content?.invoke(this)
            }
        }
    }

}

fun changeDateFormat(
    from: String = "yyyy-MM-dd",
    to: String = "dd-MMM-yyyy",
    date: String
): String {
    val date1 = parseToDate(from, date)
    return date1?.let {
        SimpleDateFormat(to, Locale.getDefault()).format(it)
    } ?: run {
        date
    }

}

fun parseToDate(to: String, date: String): Date? {
    return try {
        SimpleDateFormat(to, Locale.getDefault()).parse(date)
    } catch (e: Exception) {
        null
    }
}