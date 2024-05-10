package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ComposeDatePickerField :ComposeField(){

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Build(state: ComposeFieldState, newValue: (Pair<Boolean,String>, String) -> Unit,modifier: Modifier=Modifier) {

        val minMil:Long? = parseToDate(to="yyyy-MM-dd",date = state.field.minValue?:"")?.let {
            Calendar.getInstance().apply {
                time= it
            }.timeInMillis
        }
        val maxMil:Long? = parseToDate(to="yyyy-MM-dd",date = state.field.maxValue?:"")?.let {
            Calendar.getInstance().apply {
                time= it
            }.timeInMillis
        }

        val calendar = Calendar.getInstance()
        val dropDownText = if (state.text.isEmpty())
            "Choose an Date"
        else {
            changeDateFormat(date=state.text)
        }


        val datePickerState = rememberDatePickerState(
            selectableDates = object :SelectableDates{
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return (minMil==null || (minMil<utcTimeMillis) ) &&
                            (maxMil==null ||(maxMil>utcTimeMillis))
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
                            val result = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                            newValue(Pair(true,""),result)
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

        Column {
            Spacer(modifier = Modifier.padding(top=8.dp))
            Box {
                DatePickerField(
                    modifier = modifier,
                    onClick = {showDialog.value=true}
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 5.dp),
                        color = ComposeFieldTheme.textColor,
                        text = dropDownText
                    )
                }
                Text(
                    text = state.field.label,
                    color = ComposeFieldTheme.hintColor,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier=Modifier.padding(start = 20.dp, top = 10.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "",
                    modifier= Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 10.dp))
            }
        }

    }

    @Composable
    private fun DatePickerField(
        modifier: Modifier,
        onClick:()->Unit,
        content:@Composable ()->Unit
    ){
        when(ComposeFieldTheme.fieldStyle){
            ComposeFieldTheme.FieldStyle.OUTLINE -> TextButton(
                modifier = Modifier
                    .width(OutlinedTextFieldDefaults.MinWidth)
                    .height(OutlinedTextFieldDefaults.MinHeight)
                    .then(modifier),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
                shape = OutlinedTextFieldDefaults.shape,
                onClick = {onClick.invoke()},
                content = {content.invoke()}
            )
            ComposeFieldTheme.FieldStyle.CONTAINER ,
            ComposeFieldTheme.FieldStyle.NORMAL ->TextButton(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .padding(5.dp)
                    .width(TextFieldDefaults.MinWidth)
                    .height(TextFieldDefaults.MinHeight)
                    .then(modifier)
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(8.dp)
                    ),
                onClick = { onClick() },
                content = {content.invoke()}
            )
        }
    }


    fun changeDateFormat(from:String="yyyy-MM-dd",to:String="dd-MMM-yyyy",date: String):String{
        val date1 = parseToDate(from,date)
        return date1?.let {
            SimpleDateFormat(to,Locale.getDefault()).format(it)
        }?:run {
            date
        }

    }

    fun parseToDate(to:String,date: String): Date? {
        return try {
            SimpleDateFormat(to, Locale.getDefault()).parse(date)
        }catch (e:Exception){
            null
        }
    }
}