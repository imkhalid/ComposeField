package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.model.ComposeFieldStyle
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.states.rememberFieldState
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.util.ErrorView
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

    @Preview
    @Composable
    private fun MyBuild(
        state: ComposeFieldState = ComposeFieldState(
            field = ComposeFieldModule(
                label = "Email",
                type = ComposeFieldType.DROP_DOWN,
                defaultValues = listOf()
            )
        ),
        newValue: (Pair<Boolean, String>, String) -> Unit = {pair,s->},
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val options = state.field.defaultValues.map { it.text }
        val values = state.field.defaultValues.map { it.id }
        val dropDownText =
            if (state.text.isEmpty())
                if (state.field.required== ComposeFieldYesNo.YES)
                    "Required"
                else
                    "Optional"
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

        Column(modifier = modifier.bringIntoViewRequester(localRequester)) {
            DropDownField(
                fieldStyle = state.field.fieldStyle.fieldStyle,
                onClick = toggleDropdown,
                enabled = state.field.isEditable==ComposeFieldYesNo.YES
            ) {
                if (state.field.fieldStyle.fieldStyle== ComposeFieldTheme.FieldStyle.STICK_LABEL){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(responsiveSize(5))
                    ) {
                        Text(
                            text = state.field.label,
                            style = state.field.fieldStyle.getLabelTextStyle()
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = dropDownText,
                            style = if (state.text.isEmpty())
                                state.field.fieldStyle.getHintTextStyle()
                            else
                                state.field.fieldStyle.getTextStyle(),
                            textAlign = TextAlign.End,
                        )
                        Box(
                            contentAlignment = Alignment.BottomEnd
                        ){
                            DropdownOptions(
                                modifier=  Modifier.align(Alignment.CenterEnd),
                                selectedText = state.text,
                                expanded = expanded,
                                options = options,
                                values = values,
                                style = state.field.fieldStyle.getDropDownTextStyle(),
                                onOptionSelected = { pair, s ->
                                    focusCallback?.invoke(true, state.field.name)
                                    newValue.invoke(pair, s)
                                },
                                onDismiss = {
                                    expanded=false
                                }
                            )
                        }
                    }
                }
                else {
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
                    Column {
                        Text(
                            text = label,
                            modifier = Modifier.padding(start = 20.dp, top = 7.dp),
                            style = state.field.fieldStyle.getLabelTextStyle()
                        )
                        Text(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, bottom = 7.dp),
                            text = dropDownText,
                            style = state.field.fieldStyle.getTextStyle()
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                    )
                    DropdownOptions(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        expanded = expanded,
                        options = options,
                        values = values,
                        style = state.field.fieldStyle.getLabelTextStyle(),
                        onOptionSelected = { pair, s ->
                            focusCallback?.invoke(true, state.field.name)
                            newValue.invoke(pair, s)
                        },
                        onDismiss = {
                            expanded=false
                        }
                    )
                }

            }
            ErrorView(
                modifier = Modifier.padding(start = 16.dp),
                state = state
            )
        }
    }

    @Composable
    private fun DropDownField(
        fieldStyle: ComposeFieldTheme.FieldStyle,
        onClick: () -> Unit={},
        enabled:Boolean=true,
        content: @Composable (BoxScope.() -> Unit)? = null
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
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
                        .heightIn(TextFieldDefaults.MinHeight)
                        .focusRequester(focusRequester)
                        .onFocusChanged { s -> isFocused = s.isFocused }
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .border(
                            width = if (isFocused) 1.dp else 0.dp,
                            color = if (enabled.not()) {
                                Color(0xFFE0E0E0)
                            } else if (isFocused)
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
            ComposeFieldTheme.FieldStyle.STICK_LABEL ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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


    @Composable
    fun DropdownOptions(
        modifier: Modifier = Modifier,
        selectedText: String = "",
        style: TextStyle,
        expanded: Boolean,
        options: List<String>,
        values: List<String>,
        onOptionSelected: (Pair<Boolean, String>, String) -> Unit,
        onDismiss: () -> Unit
    ) {
        if (expanded && options.size > 6) {
            DropdownDialog(
                style = style,
                selectedText=selectedText,
                options = options,
                values = values,
                onDismiss = onDismiss,
                onOptionSelected = {
                    onOptionSelected(Pair(true, ""), it)
                    onDismiss.invoke()
                }
            )
        }else{
            DropdownMenu(
                modifier = Modifier.then(modifier),
                expanded = expanded && options.size <= 6,
                containerColor = Color(0xffFBFBFB),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, color = Color(0x33A1A1A1)),
                onDismissRequest = onDismiss,
            ) {
                options.take(6).forEachIndexed { index,option ->

                    DropdownMenuItem(
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(responsiveSize(40)),
                        text = {
                            val modifier = if (values[index]==selectedText)
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = responsiveSize(5), vertical = 5.dp)
                                    .background(
                                        color = Color(0xffFDF0F5),
                                        shape = RoundedCornerShape(responsiveSize(7))
                                    )
                                    .padding(horizontal = 7.dp, vertical = 5.dp)
                            else
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = responsiveSize(12))
                            Column {
                                Box(modifier= modifier){
                                    Text(
                                        text = option,
                                        modifier = Modifier.align(Alignment.CenterStart),
                                        style=style.copy(
                                            fontSize = responsiveTextSize(12).sp,
                                            fontWeight = if (values[index]==selectedText)
                                                FontWeight.Medium
                                            else
                                                FontWeight.Normal,
                                            color = if (values[index]==selectedText)
                                                Color(0xffC4285D)
                                            else
                                                Color(0xff919191),
                                        )
                                    )
                                }
                                if (option!=options.last())
                                    HorizontalDivider(
                                        modifier= Modifier.padding(horizontal = 5.dp),
                                        thickness = 0.4.dp,
                                        color = Color(0xffE8E8E8),
                                    )
                            }

                        },
                        onClick = {
                            options
                                .indexOf(option)
                                .takeIf { it != -1 }
                                ?.let { onOptionSelected(Pair(true, ""), values[it]) }
                            onDismiss.invoke()
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun DropdownDialog(
        style: TextStyle,
        selectedText: String = "",
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
                            keyboardType = ComposeKeyboardTypeAdv.TEXT(),
                            value = "",
                        ).apply {
                            this.updatedField(state.field.copy(
                                fieldStyle =state.field.fieldStyle.copy(
                                    fieldStyle = ComposeFieldTheme.FieldStyle.CONTAINER
                                )
                            ))
                        },
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
                                Text(
                                    text = option,
                                    style=style.copy(
                                        fontWeight = if (values[index]==selectedText)
                                            FontWeight.Medium
                                        else
                                            FontWeight.Normal,
                                        color = if (values[index]==selectedText)
                                            Color(0xffC4285D)
                                        else
                                            Color(0xff919191),
                                    )
                                )

                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
