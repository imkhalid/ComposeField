package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.responsiveHPaddings
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.util.ErrorView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ComposeDatePickerField : ComposeField() {

    @Composable
    override fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        MyBuild(state = state, newValue = newValue, modifier = modifier)
    }

    @Composable
    private fun MyBuild(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val style = state.field.fieldStyle
        val label = buildAnnotatedString {
            append(state.field.label)
            if (state.field.required == ComposeFieldYesNo.YES) {
                withStyle(
                    style =
                    SpanStyle(fontSize = responsiveTextSize(size = 13).sp, color = Color.Red)
                ) {
                    append("*")
                }
            }
        }

        val dropDownText =
            if (state.text.isEmpty()) ComposeFieldTheme.datePickerHint
            else {
                changeDateFormat(date = state.text)
            }
        val showDialog = rememberSaveable { mutableStateOf(false) }
        DatePickDialog(showDialog,state){
            newValue(Pair(true,""),it)
        }
        Column(modifier = modifier) {
            Box(modifier = Modifier.fillMaxWidth().bringIntoViewRequester(localRequester)) {
                DatePickerField(
                    fieldStyle = style.fieldStyle,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog.value = true },
                    enabled = state.field.isEditable == ComposeFieldYesNo.YES
                ) {
                    if (style.fieldStyle== ComposeFieldTheme.FieldStyle.STICK_LABEL){
                        Row(
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(responsiveSize(5))
                        ) {
                            Text(
                                text = state.field.label,
                                style= style.getLabelTextStyle()
                            )

                            val style =if (state.text.isEmpty())
                                state.field.fieldStyle.getHintTextStyle()
                            else
                                state.field.fieldStyle.getTextStyle()
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = dropDownText,
                                style = style.copy(textAlign = TextAlign.End,),
                            )
                        }
                    }else {
                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, top = 7.dp)
                                    .align(Alignment.CenterStart),
                            style = style.getTextStyle(),
                            color =
                                if (state.text.isEmpty()) style.colors.unfocusedLabelColor
                                else style.colors.textColor,
                            text = dropDownText,
                        )
                        Text(
                            text = label,
                            style = style.getLabelTextStyle(),
                            modifier = Modifier.padding(start = 20.dp, top = 7.dp)
                        )
                        EndIcons(state){
                            newValue(Pair(true,""),"")
                        }
                    }
                }
            }
            ErrorView(
                state = state,
                modifier = Modifier.padding(start = 16.dp)
            )
            HelperText(state = state)
        }
    }

    private @Composable
    fun BoxScope.EndIcons(state: ComposeFieldState,onClearCallback:()->Unit) {
        Row(
            modifier =  Modifier
            .align(Alignment.CenterEnd)
            .padding(horizontal = 10.dp)
        ) {
            if (
                state.field.keyboardType is ComposeKeyboardTypeAdv.DATE &&
                state.field.keyboardType.showEndClear && state.text.isNotEmpty()
                ){
                Image(
                    imageVector =  Icons.Rounded.Clear,
                    contentDescription = "",
                    modifier =Modifier.padding(horizontal = responsiveHPaddings(5))
                        .clickable { onClearCallback.invoke() }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = "",
                modifier =Modifier.padding(start = responsiveHPaddings(5))
            )
        }
    }

    @Composable
    private fun DatePickerField(
        modifier: Modifier,
        fieldStyle: ComposeFieldTheme.FieldStyle,
        onClick: () -> Unit,
        enabled: Boolean = true,
        content: @Composable (BoxScope.() -> Unit)? = null
    ) {
        when (fieldStyle) {
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
                        .clickable {
                            if (enabled)
                                onClick()
                        },
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
                        .background(
                            color = if (enabled.not()) Color(0xFFE0E0E0) else Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (enabled)
                                onClick()
                        },
                ) {
                    content?.invoke(this)
                }
            ComposeFieldTheme.FieldStyle.STICK_LABEL->
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = responsiveSize(8))
                        .height(OutlinedTextFieldDefaults.MinHeight)
                        .clickable {
                            if (enabled)
                                onClick()
                        },
                ){
                    content?.invoke(this)
                }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DatePickDialog(showDialog: MutableState<Boolean>,state: ComposeFieldState,onConfirm:(selectedDate:String)->Unit){
        val minDate = parseToDate(to = "yyyy-MM-dd", date = state.field.minValue ?: "")
        val maxDate = parseToDate(to = "yyyy-MM-dd", date = state.field.maxValue ?: "")
        val minMil: Long? = minDate?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
        val maxMil: Long? = maxDate?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        }

        val rangeMin =
            if (minDate != null) {
                Calendar.getInstance().apply { time = minDate }.get(Calendar.YEAR)
            } else {
                DatePickerDefaults.YearRange.first
            }
        val rangeMax =
            if (maxDate != null) {
                Calendar.getInstance().apply { time = maxDate }.get(Calendar.YEAR)
            } else {
                DatePickerDefaults.YearRange.last
            }
        val calendar = Calendar.getInstance()
        val datePickerState =
            DatePickerState(
                locale = CalendarLocale.ENGLISH,
                initialSelectedDateMillis =
                if (state.text.isNotEmpty()) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        .apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        .parse(state.text)?.time
                } else if (maxMil != null && maxMil < calendar.timeInMillis)
                    maxMil
                else if (minMil != null && minMil >= calendar.timeInMillis)
                    minMil
                else
                    calendar.timeInMillis,
                yearRange = rangeMin..rangeMax,
                selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return (minMil == null || (minMil < utcTimeMillis)) &&
                                (maxMil == null || (maxMil > utcTimeMillis))
                    }
                }
            )
        if (showDialog.value) {
            val colors = DatePickerDefaults.colors(
                containerColor = ComposeFieldTheme.containerColor,  // Background color
                titleContentColor = ComposeFieldTheme.focusedLabelColor,  // Title color
                headlineContentColor = ComposeFieldTheme.focusedLabelColor,  // Headline color
                weekdayContentColor = ComposeFieldTheme.focusedLabelColor,  // Weekday text color
                navigationContentColor = Color.Black,  // Subhead (month, year) text color
                yearContentColor = Color.Black,  // Year text color
                selectedDayContentColor = Color.White,  // Selected day text color
                selectedYearContentColor = Color.White,
                selectedDayContainerColor = ComposeFieldTheme.focusedBorderColor,  // Selected day background color
                dayContentColor = Color.Black,
                disabledYearContentColor = Color.Black.copy(0.4f),
                disabledDayContentColor = Color.Black.copy(0.4f),
                todayDateBorderColor = ComposeFieldTheme.focusedBorderColor,
                dateTextFieldColors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = ComposeFieldTheme.textColor,
                    unfocusedTextColor = ComposeFieldTheme.textColor,
                    focusedIndicatorColor = ComposeFieldTheme.textColor.copy(0.7f),
                    unfocusedIndicatorColor = ComposeFieldTheme.textColor.copy(0.7f),
                    focusedContainerColor = Color.Transparent
                )
            )
            DatePickerDialog(
                onDismissRequest = { showDialog.value = false },
                colors = DatePickerDefaults.colors().copy(
                    containerColor = ComposeFieldTheme.containerColor,
                ),
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog.value = false
                            datePickerState.selectedDateMillis?.let {
                                calendar.timeInMillis = it
                                val result =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        .format(calendar.time)
                                onConfirm.invoke(result)
                            }
                        }
                    ) {
                        Text("Ok")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = colors
                )
            }
        }
    }

    @Composable
    private fun HelperText(state:ComposeFieldState){
        var showText:Boolean by remember{ mutableStateOf(false) }
        val helperText = buildAnnotatedString {
            if (state.field.keyboardType is ComposeKeyboardTypeAdv.DATE){
                if (state.field.keyboardType.ageCalculation && state.text.isNotEmpty()) {
                    showText=true
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontSize = responsiveTextSize(size = 12).sp
                        )
                    ) {
                        append(getAgeStr(state.text)+"\n")
                    }
                }
                val helperText = state.field.keyboardType.helperText.ifEmpty {state.field.helperText}
                if (helperText.isNotEmpty()){
                    if (showText.not()) showText=true
                    withStyle(style = SpanStyle(
                        color = Color.Gray.copy(0.8f),
                        fontSize = responsiveTextSize(size = 11).sp
                    )){
                        append(helperText)
                    }
                }

            }
        }
        if (showText) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 2.dp),
                text = helperText,
                style = TextStyle(
                    lineHeight = 10.sp
                ),
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

fun changeDateFormat(
    from: String = "yyyy-MM-dd",
    to: String = "dd-MMM-yyyy",
    date: String
): String {
    val date1 = parseToDate(from, date)
    return date1?.let { SimpleDateFormat(to, Locale.getDefault()).format(it) } ?: run { date }
}

fun parseToDate(to: String, date: String): Date? {
    return try {
        SimpleDateFormat(to, Locale.getDefault())
            .apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            .parse(date)
    } catch (e: Exception) {
        null
    }
}

fun matchAny(str:String,vararg with:String):Boolean{
    return with.any { x->str.contains(x,true) }
}

fun getAgeStr(str:String):String{
    var age = ""
    try {
        str.takeIf { x -> x.isNotEmpty() }?.let {
            val date = LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val period = Period.between(date, LocalDate.now())
            val year = if (period.years != 0) "${period.years} years " else ""
            val month = if (period.months != 0) "${period.months} months " else ""
            val days = if (period.days > 0) "${period.days} days." else ""
            age = "Current Age is $year$month$days"
        }
    }catch (_:Exception){}
    return age
}
