package com.imkhalid.composefield.composeField.fields

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.techInfo.composefieldproject.composeField.ComposeFieldState
import com.techInfo.composefieldproject.composeField.fields.ComposeField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ComposeDatePickerField :ComposeField(){

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Build(state: ComposeFieldState, newValue: (Pair<Boolean,String>, String) -> Unit) {

        val calendar = Calendar.getInstance()
        val dropDownText = if (state.text.isEmpty())
            "Choose an Date"
        else {
            changeDateFormat(date=state.text)
        }


        val datePickerState = rememberDatePickerState()
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
                    dateValidator = {current->
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
                        (minMil==null || (minMil<current) ) &&
                                (maxMil==null ||(maxMil>current))
                    }
                )
            }
        }

        Column {
            Spacer(modifier = Modifier.padding(top=8.dp))
            Box {
                TextButton(
                    modifier = Modifier
                        .width(OutlinedTextFieldDefaults.MinWidth)
                        .height(OutlinedTextFieldDefaults.MinHeight),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
                    shape = OutlinedTextFieldDefaults.shape,
                    onClick = {showDialog.value=true}
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground,
                        text = dropDownText
                    )
                }
                Text(
                    text = state.field.label,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier=Modifier.padding(10.dp)
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier= Modifier
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                )
            }
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