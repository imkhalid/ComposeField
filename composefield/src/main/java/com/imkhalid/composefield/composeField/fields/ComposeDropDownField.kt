package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techInfo.composefieldproject.composeField.ComposeFieldState
import com.techInfo.composefieldproject.composeField.fields.ComposeField

class ComposeDropDownField : ComposeField() {


    fun setFocusCallback(callback:((isValidated:Boolean,fieldName:String)->Unit)?)= apply{
        focusCallback =callback
    }

    @Composable
    fun Build(
        state: ComposeFieldState, newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val options = state.field.defaultValues.map { it.text }
        val values = state.field.defaultValues.map { it.id }
        val dropDownText = if (state.text.isEmpty())
            "Choose an Option"
        else {
            values.indexOf(state.text).takeIf { it != -1 }?.let {
                options[it]
            } ?: ""
        }

        Column {
            Box {
                TextButton(
                    modifier = Modifier
                        .width(OutlinedTextFieldDefaults.MinWidth)
                        .height(OutlinedTextFieldDefaults.MinHeight),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
                    shape = OutlinedTextFieldDefaults.shape,
                    onClick = toggleDropdown
                ) {
                    Text(
                        modifier=Modifier.fillMaxWidth(),
                        text=dropDownText
                    )
                }
                DropdownMenu(
                    expanded = expanded && options.size <= 6,
                    onDismissRequest = { expanded = false },
                ) {
                    options.take(6).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                focusCallback?.invoke(true,state.field.name)
                                options.indexOf(option).takeIf { it != -1 }?.let {
                                    newValue(Pair(true, ""), values[it])
                                }
                                expanded = false
                            })
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier=Modifier
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                )
                Text(
                    text = state.field.label,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier=Modifier.padding(10.dp)
                )
                if (expanded && options.size > 6) {
                    DropdownDialog(options = options, values = values, onOptionSelected = {
                        focusCallback?.invoke(true,state.field.name)
                        newValue(Pair(true, ""), it)
                        expanded = false
                    })
                }
            }
            if (state.hasError) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    @Composable
    private fun DropdownDialog(
        options: List<String>,
        values: List<String>,
        onOptionSelected: (String) -> Unit
    ) {
        var searchText by remember { mutableStateOf("") }
        val filteredOptions = options.filter { it.contains(searchText, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = {},
            title = { Text("Select an Option") },
            text = {
                Column {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)) {
                        items(filteredOptions) { option ->
                            TextButton(onClick = {
                                options.indexOf(option).takeIf { it != -1 }?.let {
                                    onOptionSelected(values[it])
                                }
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

}