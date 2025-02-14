package com.imkhalid.composefield.composeField.fields

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.states.rememberFieldState
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.imkhalid.composefield.composeField.responsiveTextSize

class ComposeCheckBoxField : ComposeField() {

    /** value will look like this 132::234:: */
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
        val selectedIDs = state.text.split("::")
        val selectedvalues =
            state.field.defaultValues.filter { x -> selectedIDs.contains(x.id) }.map { x -> x.text }
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val options = state.field.defaultValues.map { it.text }
        val values = state.field.defaultValues.map { it.id }
        // Create ScrollState to own it and be able to control scroll behaviour of scrollable Row
        // below
        val scrollState = rememberScrollState()

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

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (state.field.defaultValues.size > 3) {
                CheckboxField(
                    onClick = toggleDropdown,
                    enabled = state.field.isEditable==ComposeFieldYesNo.YES
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(5.dp)
                    )
                    Column {
                        Text(text = label, modifier = Modifier.padding(start = 20.dp, top = 7.dp, end = 10.dp))
                        Row(
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState)
                                .padding(start = 20.dp, top = 2.dp, bottom = 7.dp),
                        ) {
                            if (selectedvalues.isNotEmpty()) {
                                for (element in selectedvalues) {
                                    ElevatedFilterChip(
                                        modifier = Modifier.height(20.dp),
                                        selected = false,
                                        onClick = toggleDropdown,
                                        colors =
                                        FilterChipDefaults.elevatedFilterChipColors(
                                            containerColor = ComposeFieldTheme.unfocusedBorderColor,
                                            selectedContainerColor =
                                            ComposeFieldTheme.unfocusedBorderColor,
                                            disabledSelectedContainerColor =
                                            ComposeFieldTheme.unfocusedBorderColor,
                                            selectedLabelColor = ComposeFieldTheme.textColor,
                                        ),
                                        label = {
                                            Text(
                                                text = element,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = ComposeFieldTheme.textColor
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.padding(3.dp))
                                }
                            } else {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = state.field.label,
                                    color = ComposeFieldTheme.textColor
                                )
                            }
                        }
                    }
                    if (expanded && options.size > 3) {
                        CheckboxDialog(
                            options = options,
                            values = values,
                            state = state,
                            onDismiss = {
                                expanded=false
                            },
                            onOptionSelected = {
                                if (selectedIDs.contains(it)) {
                                    newValue(Pair(true, ""), state.text.replace("$it::", ""))
                                } else {
                                    newValue(Pair(true, ""), state.text.plus("$it::"))
                                }
                            },
                            onConfirm = { expanded = false }
                        )
                    }
                }
            } else {
                CheckBoxField3Options(enabled = state.field.isEditable==ComposeFieldYesNo.YES) {
                    val label = buildAnnotatedString {
                        withStyle(
                            style =
                            SpanStyle(
                                fontSize = responsiveTextSize(size = 13).sp,
                                color = ComposeFieldTheme.focusedBorderColor,
                            )
                        ) {
                            append(state.field.label)
                        }
                        if (state.field.required == ComposeFieldYesNo.YES) {
                            withStyle(
                                style =
                                SpanStyle(
                                    fontSize = responsiveTextSize(size = 13).sp,
                                    color = Color.Red
                                )
                            ) {
                                append("*")
                            }
                        }
                    }
                    Text(text = label, modifier = Modifier.padding(start = 15.dp, end = 20.dp, top = 2.dp))
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    state.field.defaultValues.forEach { defV ->
                        RoundedCornerCheckbox(
                            modifier = Modifier.padding(start = 20.dp),
                            isChecked = selectedIDs.contains(defV.id),
                            label = defV.text,
                            checkedColor = ComposeFieldTheme.focusedBorderColor,
                            enabled= state.field.isEditable==ComposeFieldYesNo.YES,
                            onValueChange = {
                                if (selectedIDs.contains(defV.id)) {
                                    newValue(Pair(true, ""), state.text.replace("${defV.id}::", ""))
                                } else {
                                    newValue(Pair(true, ""), state.text.plus(defV.id + "::"))
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CheckboxField(
        onClick: () -> Unit,
        enabled:Boolean=true,
        content: @Composable BoxScope.() -> Unit,
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
                        }
                ) {
                    content.invoke(this)
                }
            ComposeFieldTheme.FieldStyle.CONTAINER,
            ComposeFieldTheme.FieldStyle.NORMAL ->
                Box(
                    modifier =
                    Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .heightIn(min = TextFieldDefaults.MinHeight)
                        .focusRequester(focusRequester)
                        .onFocusChanged { s -> isFocused = s.isFocused }
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                        .background(
                            color = if (enabled) Color.White else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = if (isFocused) 1.dp else 0.dp,
                            color =
                            if (isFocused) ComposeFieldTheme.focusedBorderColor
                            else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (enabled)
                                onClick()
                        },
                ) {
                    content.invoke(this)
                }
        }
    }

    @Composable
    private fun CheckboxDialog(
        options: List<String>,
        values: List<String>,
        state: ComposeFieldState,
        onDismiss:()->Unit,
        onOptionSelected: (String) -> Unit,
        onConfirm: () -> Unit,
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
        val selectedIDs = state.text.split("::")

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Select an Option", color = ComposeFieldTheme.textColor) },
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
                        itemsIndexed(filteredOptions) { inde, option ->
                            RoundedCornerCheckbox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                isChecked = selectedIDs.contains(filteredValues[inde]),
                                label = option,
                                enabled = state.field.isEditable==ComposeFieldYesNo.YES,
                                checkedColor = ComposeFieldTheme.focusedBorderColor,
                                onValueChange = {
                                    onOptionSelected(filteredValues[inde])
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { onConfirm() }) { Text(text = "Done") } }
        )
    }
}

@Composable
fun RoundedCornerCheckbox(
    label: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    size: Float = 20f,
    checkedColor: Color = Color.Blue,
    uncheckedColor: Color = Color.White,
    enabled: Boolean=true,
    onValueChange: ((Boolean) -> Unit)? = null
) {
    val checkboxColor: Color by animateColorAsState(if (isChecked) checkedColor else uncheckedColor)
    val density = LocalDensity.current
    val duration = 200

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        modifier
            .heightIn(30.dp) // height of 48dp to comply with minimum touch target size
            .toggleable(
                value = isChecked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = { onValueChange?.invoke(it) }
            )
    ) {
        Box(
            modifier =
            Modifier
                .size(size.dp)
                .background(color = checkboxColor, shape = RoundedCornerShape(4.dp))
                .border(width = 1.dp, color = checkedColor, shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isChecked,
                enter =
                    slideInHorizontally(animationSpec = tween(duration)) {
                        with(density) { (size * -0.5).dp.roundToPx() }
                    } +
                        expandHorizontally(
                            expandFrom = Alignment.Start,
                            animationSpec = tween(duration)
                        ),
                exit = fadeOut()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = uncheckedColor)
            }
        }
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = label,
            color = ComposeFieldTheme.textColor
        )
    }
}

@Composable
private fun CheckBoxField3Options(modifier: Modifier = Modifier,enabled: Boolean=true, content: @Composable () -> Unit) {
    when (ComposeFieldTheme.fieldStyle) {
        ComposeFieldTheme.FieldStyle.OUTLINE ->
            Column(
                modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                content = { content.invoke() }
            )
        ComposeFieldTheme.FieldStyle.CONTAINER,
        ComposeFieldTheme.FieldStyle.NORMAL ->
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier =
                modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .border(
                        width = 0.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                    .background(color = if (enabled) Color.White else Color(0xFFE0E0E0), shape = RoundedCornerShape(8.dp))
                    .padding(5.dp),
                content = { content.invoke() }
            )
    }
}


@Preview
@Composable
fun PreviewCheckBOx(){
    ComposeCheckBoxField()
        .Build(
            modifier = Modifier,
            state = ComposeFieldState(
                field = ComposeFieldModule(
                    label ="Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,Khalid Saeed,",
                    type = ComposeFieldType.CHECK_BOX,
                    keyboardType = ComposeKeyboardTypeAdv.TEXT,
                    isEditable = ComposeFieldYesNo.YES,
                    required = ComposeFieldYesNo.YES
                )
            )
        ) { pair, value->

    }
}