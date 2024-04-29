package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.sp
import com.techInfo.composefieldproject.composeField.ComposeFieldState

class ComposeCheckBoxField {

    /**
     * value will look like this
     * 132::234::*/

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Build(state: ComposeFieldState, newValue: (Pair<Boolean,String>, String) -> Unit) {
        val selectedIDs = state.text.split("::")
        val selectedvalues = state.field.defaultValues.filter { x->selectedIDs.contains(x.id) }.map { x->x.text }
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val options = state.field.defaultValues.map { it.text }
        val values = state.field.defaultValues.map { it.id }
        // Create ScrollState to own it and be able to control scroll behaviour of scrollable Row below
        val scrollState = rememberScrollState()
        Column(
            Modifier.width(OutlinedTextFieldDefaults.MinWidth),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (state.field.defaultValues.size>6){
                Box {
                    TextButton(
                        modifier = Modifier
                            .width(OutlinedTextFieldDefaults.MinWidth)
                            .height(OutlinedTextFieldDefaults.MinHeight),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onBackground),
                        shape = OutlinedTextFieldDefaults.shape,
                        onClick = toggleDropdown
                    ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(scrollState),
                        ) {
                            if (selectedvalues.isNotEmpty()) {
                                for (element in selectedvalues) {
                                    ElevatedFilterChip(
                                        modifier=Modifier.height(20.dp),
                                        selected = true,
                                        onClick = { },
                                        label = {
                                            Text(
                                                text = element,
                                                fontSize = 10.sp
                                            )
                                        })
                                    Spacer(modifier = Modifier.padding(3.dp))
                                }
                            }else {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = state.field.label
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier= Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                    )
                    Text(
                        text = state.field.label,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        modifier=Modifier.padding(10.dp)
                    )
                    if (expanded && options.size > 6) {
                        CheckboxDialog(
                            options = options,
                            values = values,
                            state = state,
                            onOptionSelected = {
                                if (selectedIDs.contains(it)){
                                    newValue(Pair(true, ""), state.text.replace("$it::",""))
                                }else{
                                    newValue(Pair(true, ""), state.text.plus("$it::"))
                                }
                            },
                            onConfirm = {
                                expanded=false
                            }
                        )
                    }
                }
            }else {
                Text(
                    text = state.field.label,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                state.field.defaultValues.forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clickable {
                                if (selectedIDs.contains(it.id)) {
                                    newValue(Pair(true, ""), state.text.replace("${it.id}::", ""))
                                } else {
                                    newValue(Pair(true, ""), state.text.plus(it.id + "::"))
                                }
                            }) {
                        Checkbox(
                            modifier = Modifier.padding(8.dp),
                            checked = selectedIDs.contains(it.id),
                            onCheckedChange = null
                        )
                        Text(
                            text = it.text,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    }
                }
            }
        }
    }


    @Composable
    private fun CheckboxDialog(
        options: List<String>,
        values: List<String>,
        state:ComposeFieldState,
        onOptionSelected: (String) -> Unit,
        onConfirm: () -> Unit,
    ) {
        var searchText by remember { mutableStateOf("") }
        val filteredOptions = options.filter { it.contains(searchText, ignoreCase = true) }
        val selectedIDs = state.text.split("::")

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
                        itemsIndexed(filteredOptions) { inde,option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clickable {
                                        options.indexOf(option).takeIf { it != -1 }?.let {
                                            onOptionSelected(values[it])
                                        }
                                    }) {
                                Checkbox(
                                    modifier = Modifier.padding(8.dp),
                                    checked = selectedIDs.contains(values[inde]),
                                    onCheckedChange = null
                                )
                                Text(
                                    text = option,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = "Done")
                }
            }
        )
    }

}