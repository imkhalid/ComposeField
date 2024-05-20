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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

class ComposeCheckBoxField :ComposeField(){

    /**
     * value will look like this
     * 132::234::*/
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
        // Create ScrollState to own it and be able to control scroll behaviour of scrollable Row below
        val scrollState = rememberScrollState()

        val label = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = responsiveTextSize(size = 13).sp,
                    color = ComposeFieldTheme.hintColor,
                )
            ) {
                append(state.field.label)
            }
            if (state.field.required== ComposeFieldYesNo.YES){
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

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (state.field.defaultValues.size > 3) {
                CheckboxField(
                    onClick = toggleDropdown
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                            .horizontalScroll(scrollState)
                            .padding(start = 20.dp, top = 7.dp),
                    ) {
                        if (selectedvalues.isNotEmpty()) {
                            for (element in selectedvalues) {
                                ElevatedFilterChip(
                                    modifier = Modifier.height(20.dp),
                                    selected = false,
                                    onClick = toggleDropdown,
                                    colors = FilterChipDefaults.elevatedFilterChipColors(
                                        containerColor = ComposeFieldTheme.unfocusedBorderColor,
                                        selectedContainerColor = ComposeFieldTheme.unfocusedBorderColor,
                                        disabledSelectedContainerColor = ComposeFieldTheme.unfocusedBorderColor,
                                        selectedLabelColor = ComposeFieldTheme.textColor,
                                    ),
                                    label = {
                                        Text(
                                            text = element,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ComposeFieldTheme.textColor
                                        )
                                    })
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
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                    )
                    Text(
                        text = label,
                        modifier = Modifier.padding(start = 20.dp, top = 7.dp)
                    )
                    if (expanded && options.size > 3) {
                        CheckboxDialog(
                            options = options,
                            values = values,
                            state = state,
                            onOptionSelected = {
                                if (selectedIDs.contains(it)) {
                                    newValue(Pair(true, ""), state.text.replace("$it::", ""))
                                } else {
                                    newValue(Pair(true, ""), state.text.plus("$it::"))
                                }
                            },
                            onConfirm = {
                                expanded = false
                            }
                        )
                    }
                }
            } else {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp),
                    text = state.field.label,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = ComposeFieldTheme.hintColor
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                state.field.defaultValues.forEach { defV ->
                    RoundedCornerCheckbox(
                        modifier = Modifier.padding(8.dp),
                        isChecked = selectedIDs.contains(defV.id),
                        label = defV.text,
                        checkedColor = ComposeFieldTheme.focusedBorderColor,
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


    @Composable
    private fun CheckboxField(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
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
                    .clickable { onClick() }
            ) {
                content.invoke(this)
            }

            ComposeFieldTheme.FieldStyle.CONTAINER,
            ComposeFieldTheme.FieldStyle.NORMAL -> Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight)
                    .focusRequester(focusRequester)
                    .onFocusChanged { s ->
                        isFocused = s.isFocused
                    }
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(
                        width = if (isFocused) 1.dp else 0.dp,
                        color = if (isFocused) ComposeFieldTheme.focusedBorderColor else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onClick() },
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                    ) {
                        itemsIndexed(filteredOptions) { inde, option ->

                            RoundedCornerCheckbox(
                                modifier = Modifier.padding(8.dp),
                                isChecked = selectedIDs.contains(values[inde]),
                                label = option,
                                checkedColor = ComposeFieldTheme.focusedBorderColor,
                                onValueChange = {
                                    options.indexOf(option).takeIf { it != -1 }?.let {
                                        onOptionSelected(values[it])
                                    }
                                }
                            )
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

@Composable
fun RoundedCornerCheckbox(
    label: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    size: Float = 20f,
    checkedColor: Color = Color.Blue,
    uncheckedColor: Color = Color.White,
    onValueChange: ((Boolean) -> Unit)? = null
) {
    val checkboxColor: Color by animateColorAsState(if (isChecked) checkedColor else uncheckedColor)
    val density = LocalDensity.current
    val duration = 200

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(30.dp) // height of 48dp to comply with minimum touch target size
            .toggleable(
                value = isChecked,
                role = Role.Checkbox,
                onValueChange = { onValueChange?.invoke(it) }
            )
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(color = checkboxColor, shape = RoundedCornerShape(4.dp))
                .border(width = 1.dp, color = checkedColor, shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isChecked,
                enter = slideInHorizontally(animationSpec = tween(duration)) {
                    with(density) { (size * -0.5).dp.roundToPx() }
                } + expandHorizontally(
                    expandFrom = Alignment.Start,
                    animationSpec = tween(duration)
                ),
                exit = fadeOut()
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = uncheckedColor
                )
            }
        }
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = label,
        )
    }
}