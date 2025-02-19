package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.states.rememberFieldState
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder

class ComposeDropDownField : ComposeField() {

    fun setFocusCallback(callback: ((isValidated: Boolean, fieldName: String) -> Unit)?) = apply {
        focusCallback = callback
    }

    @Composable
    override fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        MyBuild(state = state, newValue = newValue, modifier = modifier)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun MyBuild(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val options = state.field.defaultValues.map { it.text }
        val values = state.field.defaultValues.map { it.id }
        val dropDownText =
            if (state.text.isEmpty()) ComposeFieldTheme.dropDownHint
            else {
                values
                    .indexOf(state.text)
                    .takeIf { it != -1 }
                    ?.let { options[it] }
                    ?: kotlin.run {
                        newValue.invoke(Pair(true,""),"")
                        ""
                    }
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

        Column(modifier = modifier.bringIntoViewRequester(localRequester)) {
            DropDownField(onClick = toggleDropdown, enabled = state.field.isEditable==ComposeFieldYesNo.YES) {
                Column {
                    Text(
                        text = label,
                        modifier = Modifier.padding(start = 20.dp, top = 7.dp),
                    )
                    Text(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, bottom = 7.dp),
                        color =
                        if (state.text.isEmpty()) ComposeFieldTheme.unfocusedLabelColor
                        else ComposeFieldTheme.textColor,
                        text = dropDownText,
                        fontSize = responsiveTextSize(size = 15).sp,
                        fontWeight = if (state.text.isEmpty()) FontWeight.Normal else FontWeight.Medium,
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(10.dp)
                )
                DropdownMenu(
                    modifier = Modifier.background(ComposeFieldTheme.containerColor),
                    expanded = expanded && options.size <= 6,
                    onDismissRequest = { expanded = false },
                ) {
                    options.take(6).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = ComposeFieldTheme.textColor) },
                            onClick = {
                                focusCallback?.invoke(true, state.field.name)
                                options
                                    .indexOf(option)
                                    .takeIf { it != -1 }
                                    ?.let { newValue(Pair(true, ""), values[it]) }
                                expanded = false
                            }
                        )
                    }
                }
                if (expanded && options.size > 6) {
                    DropdownDialog(
                        options = options,
                        values = values,
                        onDismiss = {
                            expanded=false
                        },
                        onOptionSelected = {
                            focusCallback?.invoke(true, state.field.name)
                            newValue(Pair(true, ""), it)
                            expanded = false
                        }
                    )
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
    private fun DropDownField(
        onClick: () -> Unit,
        enabled:Boolean=true,
        content: @Composable (BoxScope.() -> Unit)? = null
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
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
                        .heightIn(TextFieldDefaults.MinHeight)
                        .focusRequester(focusRequester)
                        .onFocusChanged { s -> isFocused = s.isFocused }
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .border(
                            width = if (isFocused) 1.dp else 0.dp,
                            color =if (enabled.not()){
                                Color(0xFFE0E0E0)
                            }else if (isFocused)
                                ComposeFieldTheme.focusedBorderColor
                            else
                                Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (enabled)
                                onClick()
                       },
                ) {
                    content?.invoke(this)
                }
        }
    }

    @Composable
    private fun DropdownDialog(
        options: List<String>,
        values: List<String>,
        onDismiss:()->Unit,
        onOptionSelected: (String) -> Unit
    ) {
        var searchText by remember { mutableStateOf("") }
        val filteredOptions = ArrayList<String>()
        val filteredValues = ArrayList<String>()
        options.forEachIndexed { index, s ->
            if (s.contains(searchText, ignoreCase = true)) {
                filteredOptions.add(s)
                filteredValues.add(values[index])
            }
        }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Select an Option",color=ComposeFieldTheme.textColor) },
            containerColor = ComposeFieldTheme.containerColor,
            text = {
                Column {
                    ComposeFieldBuilder().Build(
                        modifier = Modifier.fillMaxWidth(),
                        stateHolder = rememberFieldState(
                            name = "search",
                            label = "Search",
                            id = "",
                            type = ComposeFieldType.TEXT_BOX,
                            keyboardType = ComposeKeyboardTypeAdv.TEXT,
                            value = "",
                        ),
                        onValueChange = {_,value->
                            searchText = value
                        }
                    )
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)) {
                        itemsIndexed(filteredOptions) { index,option ->
                            TextButton(
                                onClick = {
                                     onOptionSelected(filteredValues[index])
                                }
                            ) {
                                Text(option, color = ComposeFieldTheme.textColor)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
