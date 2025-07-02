package com.imkhalid.composefieldproject.composeField.fields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.mask.FieldMaskTransformation
import com.imkhalid.composefield.composeField.Patterns
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.model.ComposeFieldStyle
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.responsiveVPaddings
import com.imkhalid.composefield.composeField.util.ErrorView
import com.imkhalid.composefield.composeField.util.ShowToolTip
import java.util.regex.Pattern

class ComposeTextField : ComposeField() {

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

    @Composable
    fun MyBuild(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val toolbar =
            if (state.field.keyboardType is ComposeKeyboardTypeAdv.SENSITIVE)
                LocalTextToolbar provides EmptyTextToolbar
            else LocalTextToolbar provides LocalTextToolbar.current

        val colors = getColors(ComposeFieldTheme.fieldStyle)

        Column(modifier = modifier.bringIntoViewRequester(localRequester)) {
            CompositionLocalProvider(toolbar) {
                when (ComposeFieldTheme.fieldStyle) {
                    ComposeFieldTheme.FieldStyle.OUTLINE ->
                        OutlineField(
                            modifier = Modifier.fillMaxWidth(),
                            colors=colors,
                            state = state,
                            newValue = newValue
                        )
                    ComposeFieldTheme.FieldStyle.CONTAINER ->
                        ContainerField(
                            modifier = Modifier.fillMaxWidth(),
                            colors=colors,
                            state = state,
                            newValue = newValue
                        )
                    ComposeFieldTheme.FieldStyle.NORMAL ->
                        NormalField(
                            modifier = Modifier.fillMaxWidth(),
                            colors=colors,
                            state = state,
                            newValue = newValue
                        )
                    ComposeFieldTheme.FieldStyle.STICK_LABEL->
                        StickLabelField(
                            modifier = Modifier.fillMaxWidth(),
                            colors=colors,
                            state = state,
                            newValue = newValue
                        )
                }
            }
        }
    }

    @Composable
    private fun NormalField(
        modifier: Modifier = Modifier,
        colors:TextFieldColors,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val mask = getFieldMask(state.field)
        var passwordVisible by remember { mutableStateOf(false) }


        TextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                handleValueChange(curVal,mask,state,newValue)
            },
            keyboardOptions = getKeyboardOptions(state.field),
            isError = state.hasError,
            label = { GetLabel(field = state.field) },
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = getVisualTransformation(mask, state.field, passwordVisible),
            colors =colors,
            shape = RoundedCornerShape(8.dp),
            modifier =
            modifier
                .padding(5.dp)
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp)),
            trailingIcon = trailingIcon(state.field, passwordVisible = passwordVisible) {
                passwordVisible = passwordVisible.not()
            }
        )
        ErrorView(
            modifier =  Modifier.padding(start = 16.dp),
            state = state
        )
    }

    @Composable
    private fun ContainerField(
        modifier: Modifier = Modifier,
        colors: TextFieldColors,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val mask = getFieldMask(state.field)
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        TextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                handleValueChange(curVal,mask,state,newValue)
            },
            prefix = {
                if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO)
                    Text(text = "+1", modifier = Modifier.clickable {})
                else null
            },
            keyboardOptions = getKeyboardOptions(state.field),
            isError = state.hasError,
            label = { GetLabel(field = state.field) },
            textStyle = TextStyle.Default.copy(fontSize = responsiveTextSize(size = 15).sp),
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = getVisualTransformation(mask, state.field, passwordVisible),
            colors =colors,
            shape = RoundedCornerShape(8.dp),
            modifier =
            modifier
                .focusRequester(focusRequester)
                .onFocusChanged { s -> isFocused = s.isFocused }
                .padding(5.dp)
                .border(
                    width = if (isFocused) 1.dp else 0.dp,
                    color =
                        if (isFocused) ComposeFieldTheme.focusedBorderColor
                        else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp)),
            trailingIcon = trailingIcon(state.field, passwordVisible = passwordVisible) {
                passwordVisible = passwordVisible.not()
            }
        )
        ErrorView(
            modifier =  Modifier.padding(start = 16.dp),
            state = state
        )
    }

    @Composable
    private fun OutlineField(
        modifier: Modifier = Modifier,
        colors:TextFieldColors,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val mask = getFieldMask(state.field)
        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                handleValueChange(curVal,mask,state,newValue)
            },
            keyboardOptions = getKeyboardOptions(state.field),
            isError = state.hasError,
            label = { GetLabel(field = state.field) },
            textStyle = TextStyle.Default.copy(fontSize = responsiveTextSize(size = 16).sp),
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = getVisualTransformation(mask, state.field, passwordVisible),
            colors =colors,
            modifier = modifier,
            trailingIcon = trailingIcon(state.field, passwordVisible = passwordVisible) {
                passwordVisible = passwordVisible.not()
            }
        )
        ErrorView(
            modifier =  Modifier.padding(start = 16.dp),
            state = state
        )
    }

    @Preview
    @Composable
    private fun StickLabelField(
        modifier: Modifier = Modifier,
        colors:TextFieldColors = TextFieldDefaults.colors(),
        state: ComposeFieldState = ComposeFieldState(
            field = ComposeFieldModule(
                label = "Email"
            )
        ),
        newValue: (Pair<Boolean, String>, String) -> Unit = {pair,s->}
    ) {
        val modifier =  if (state.field.type== ComposeFieldType.TEXT_BOX)
            Modifier
            .fillMaxWidth()
            .height(TextFieldDefaults.MinHeight)
            .padding(horizontal = 8.dp)
        else
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)

        Box(
            modifier =modifier,
            contentAlignment = Alignment.Center
        ) {
            if (state.field.type == ComposeFieldType.TEXT_AREA){
                Column {
                    Text(
                        text = state.field.label,
                        style = state.field.fieldStyle.getLabelTextStyle(),
                    )
                    StickyBasicField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        state = state,
                        newValue = newValue
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(responsiveSize(5))
                ) {
                    Text(
                        text = state.field.label,
                        style = state.field.fieldStyle.getLabelTextStyle()
                    )
                    if (state.field.hint.isNotEmpty())
                        ShowToolTip(state=state, modifier = Modifier)

                    StickyBasicField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        state = state,
                        newValue = newValue
                    )
                }
            }
            if (state.field.keyboardType !is ComposeKeyboardTypeAdv.PASSWORD || state.field.hint.isEmpty())
                ErrorView(
                    modifier =  Modifier.align(Alignment.BottomEnd),
                    state = state
                )
        }

    }

    @Composable
    private fun StickyBasicField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ){
        val mask = getFieldMask(state.field)
        var passwordVisible by remember { mutableStateOf(false) }
        val fieldStyle = state.field.fieldStyle
        BasicTextField(
            modifier = modifier
            ,
            value = state.text,
            onValueChange = { curVal ->
                handleValueChange(curVal,mask,state,newValue)
            },
            enabled = state.field.isEditable.value,
            keyboardOptions = getKeyboardOptions(state.field),
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = getVisualTransformation(mask, state.field, passwordVisible),
            textStyle = fieldStyle.getTextStyle().copy(
                textAlign = if (state.field.type == ComposeFieldType.TEXT_AREA)
                    TextAlign.Start
                else
                    TextAlign.End,
            ),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // TextField content
                    val boxModifier = if (state.field.type == ComposeFieldType.TEXT_AREA)
                        Modifier.padding(top = responsiveVPaddings(10))
                    else
                        Modifier.weight(1f)
                    val alignment = if (state.field.type == ComposeFieldType.TEXT_AREA)
                        Alignment.TopStart
                    else
                        Alignment.CenterEnd
                    Box(
                        modifier = boxModifier,
                        contentAlignment = alignment
                    ) {
                        if (state.text.isEmpty()) {
                            GetPlaceHolder(
                                fieldStyle = fieldStyle,
                                label = if (state.field.hint.isEmpty()){
                                    if (state.field.required == ComposeFieldYesNo.YES)
                                        "Required"
                                    else
                                        "Optional"
                                }else
                                    state.field.hint
                            )
                        }
                        innerTextField()
                    }

                    TrailingIconBasic(
                        state.field,
                        passwordVisible,
                        onClick = {
                            passwordVisible = passwordVisible.not()
                        }
                    )

                }
            }
        )
    }

    private fun getMinLine(type: ComposeFieldType): Int {
        return when (type) {
            ComposeFieldType.TEXT_BOX -> 1
            ComposeFieldType.TEXT_AREA -> 5
            else -> 1
        }
    }

    private fun getMaxLine(type: ComposeFieldType): Int {
        return when (type) {
            ComposeFieldType.TEXT_BOX -> 1
            ComposeFieldType.TEXT_AREA -> 5
            else -> 1
        }
    }

    object EmptyTextToolbar : TextToolbar {
        override val status: TextToolbarStatus = TextToolbarStatus.Hidden

        override fun hide() {}

        override fun showMenu(
            rect: Rect,
            onCopyRequested: (() -> Unit)?,
            onPasteRequested: (() -> Unit)?,
            onCutRequested: (() -> Unit)?,
            onSelectAllRequested: (() -> Unit)?,
        ) {}
    }

    private fun getKeyboardOptions(fieldState: ComposeFieldModule): KeyboardOptions {
        val type =
            when (fieldState.keyboardType) {
                is ComposeKeyboardTypeAdv.CNIC,
                is ComposeKeyboardTypeAdv.ID_NO,
                is ComposeKeyboardTypeAdv.MOBILE_NO,
                is ComposeKeyboardTypeAdv.CURRENCY,
                is ComposeKeyboardTypeAdv.NUMBER -> KeyboardType.Number
                is ComposeKeyboardTypeAdv.EMAIL -> KeyboardType.Email
                is ComposeKeyboardTypeAdv.TEXT,
                is ComposeKeyboardTypeAdv.SENSITIVE,
                is ComposeKeyboardTypeAdv.DATE,
                is ComposeKeyboardTypeAdv.NONE -> KeyboardType.Text
                is ComposeKeyboardTypeAdv.PASSWORD -> KeyboardType.Password
            }
        return KeyboardOptions(keyboardType = type, autoCorrect = false, imeAction = ImeAction.Next)
    }

    private fun getVisualTransformation(
        mask: Patterns,
        field: ComposeFieldModule,
        passwordVisible: Boolean
    ): VisualTransformation {
        return if (field.visualTransformation.isNotEmpty())
            FieldMaskTransformation(field.visualTransformation)
        else if (mask != Patterns.MOBILE && mask != Patterns.NONE && mask.value.isNotEmpty())
            FieldMaskTransformation(mask.value)
        else if (field.keyboardType is ComposeKeyboardTypeAdv.PASSWORD)
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(mask = '●')
        else VisualTransformation.None
    }

    private fun builtinValidations(
        curVal: String,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        /*we will be using curVal for getValueWithMask and on final callback-> newValue
         * operations will be performed on value collected from getValueWithMask method*/

        var bool = true
        var message = ""
        val valueToBeUsed = getValueWithMask(curVal, state.field)
        when (state.field.keyboardType) {
            is ComposeKeyboardTypeAdv.CNIC -> {
                val pattern = Patterns.CNIC.pattern.toList()
                if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                    bool = false
                    message = "CNIC must follow xxxxx-xxxxxxx-x pattern"
                } else if (!Pattern.matches(pattern[1], valueToBeUsed)) {
                    bool = false
                    message = "Provide Valid CNIC"
                } else {
                    message = ""
                }
            }
            is ComposeKeyboardTypeAdv.ID_NO -> {
                val pattern = Patterns.ID_NO.pattern.toList()
                if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                    bool = false
                    message = "ID No must 8 Character Long"
                } else {
                    message = ""
                }
            }
            is ComposeKeyboardTypeAdv.EMAIL -> {
                val pattern = Patterns.EMAIL.pattern.toList()
                if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                    bool = false
                    message = "Please enter valid Email Address"
                }
            }
            is ComposeKeyboardTypeAdv.TEXT -> {
                if (state.field.name.contains("email", true)) {
                    val pattern = Patterns.EMAIL.pattern.toList()
                    if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                        bool = false
                        message = "Please enter valid Email Address"
                    }
                } else if (valueToBeUsed.isNotEmpty() &&
                    state.field.pattern.isNotEmpty() &&
                        Pattern.matches(state.field.pattern, valueToBeUsed).not()
                ) {
                    bool = false
                    message = state.field.patternMessage
                }else if (state.field.required==ComposeFieldYesNo.YES && valueToBeUsed.isEmpty()){
                    bool = false
                    message = "Required Field"
                }
            }
            else -> {
                if (valueToBeUsed.isNotEmpty() &&
                    state.field.pattern.isNotEmpty() &&
                        Pattern.matches(state.field.pattern, valueToBeUsed).not()
                ) {
                    bool = false
                    message = state.field.patternMessage
                }else if (state.field.required==ComposeFieldYesNo.YES && valueToBeUsed.isEmpty()){
                    bool = false
                    message = "Required Field"
                }
            }
        }
        newValue.invoke(Pair(bool, message), curVal)
    }

    private fun getFieldMask(module: ComposeFieldModule): Patterns {
        val keyboardType = module.keyboardType

        return when (keyboardType) {
            is ComposeKeyboardTypeAdv.CNIC -> Patterns.CNIC
            is ComposeKeyboardTypeAdv.ID_NO -> Patterns.ID_NO
            is ComposeKeyboardTypeAdv.MOBILE_NO ->
                Patterns.MOBILE.apply {
                    value = ""
                    pattern = arrayOf("")
                }
            is ComposeKeyboardTypeAdv.EMAIL -> Patterns.EMAIL
            is ComposeKeyboardTypeAdv.NUMBER -> {
                Patterns.NONE
            }
            else -> Patterns.NONE
        }
    }

    private fun getValueWithMask(currValue: String, field: ComposeFieldModule): String {
        val mask =
            when (field.keyboardType) {
                is ComposeKeyboardTypeAdv.CNIC -> Patterns.CNIC.value
                is ComposeKeyboardTypeAdv.ID_NO -> Patterns.ID_NO.value
                is ComposeKeyboardTypeAdv.MOBILE_NO -> Patterns.MOBILE.value
                is ComposeKeyboardTypeAdv.EMAIL -> Patterns.EMAIL.value
                else -> Patterns.NONE.value
            }

        val transforation = FieldMaskTransformation(mask)
        return if (Patterns.NONE.value == mask) {
            if (field.visualTransformation.isNotEmpty())
                FieldMaskTransformation(field.visualTransformation).applyMaskAndGetResult(currValue)
            else
                currValue
        } else transforation.applyMaskAndGetResult(currValue)
    }

    private fun handleValueChange(
        currentText: String,
        mask: Patterns,
        state: ComposeFieldState,
        onValidated: (Pair<Boolean, String>, String) -> Unit
    ) {
        if (mask != Patterns.NONE && mask.value.isNotEmpty()) {
            if (currentText.length <= mask.length) {
                builtinValidations(currentText, state) { validated, newVal ->
                    onValidated.invoke(validated, newVal)
                }
            }
        } else {
            builtinValidations(currentText, state) { validated, newVal ->
                onValidated.invoke(validated, newVal)
            }
        }
    }
}

@Preview
@Composable
private fun ShadowSample() {

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Shadow Order")

        Box(
            modifier =
                Modifier
                    .size(100.dp)
                    .shadow(
                        elevation = 10.dp,
                        spotColor = Color.Red,
                        shape = RoundedCornerShape(8.dp)
                    ),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello World")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier =
                Modifier
                    .background(Color.Red)
                    .size(100.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello World")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier =
                Modifier
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp))
                    .background(Color.Red)
                    .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello World")
        }
    }
}


@Composable
fun getColors(type: ComposeFieldTheme.FieldStyle): TextFieldColors{
    return when(type){
        ComposeFieldTheme.FieldStyle.STICK_LABEL-> TextFieldDefaults.colors().copy(
            errorContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = ComposeFieldTheme.textColor,
            errorTextColor = ComposeFieldTheme.textColor,
            disabledTextColor = ComposeFieldTheme.textColor,
            unfocusedTextColor = ComposeFieldTheme.textColor,
            disabledIndicatorColor = Color.Transparent,

        )
        ComposeFieldTheme.FieldStyle.NORMAL,
        ComposeFieldTheme.FieldStyle.OUTLINE -> TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorLabelColor = ComposeFieldTheme.errorColor,
            focusedLabelColor = ComposeFieldTheme.hintColor,
            focusedTextColor = ComposeFieldTheme.textColor,
            unfocusedTextColor = ComposeFieldTheme.textColor,
            focusedSupportingTextColor = ComposeFieldTheme.infoColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledTextColor = Color(0xFFBDBDBD),
            disabledLabelColor = Color(0xFF9E9E9E),
            disabledPlaceholderColor = Color(0xFFBDBDBD),
            disabledContainerColor = Color(0xFFE0E0E0),
            errorContainerColor = Color(0xFFfaebeb),
            errorTextColor = ComposeFieldTheme.textColor
        )
        ComposeFieldTheme.FieldStyle.CONTAINER -> TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorLabelColor = ComposeFieldTheme.errorColor,
            focusedLabelColor = ComposeFieldTheme.focusedLabelColor,
            unfocusedLabelColor = ComposeFieldTheme.unfocusedLabelColor,
            unfocusedPlaceholderColor = ComposeFieldTheme.unfocusedLabelColor,
            focusedTextColor = ComposeFieldTheme.textColor,
            unfocusedTextColor = ComposeFieldTheme.textColor,
            focusedSupportingTextColor = ComposeFieldTheme.infoColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledTextColor = Color(0xFFBDBDBD),
            disabledLabelColor = Color(0xFF9E9E9E),
            disabledPlaceholderColor = Color(0xFFBDBDBD),
            disabledContainerColor = Color(0xFFE0E0E0),
            errorContainerColor = Color(0xFFfaebeb),
            errorTextColor = ComposeFieldTheme.textColor
        )
    }

}

@Composable
fun GetPlaceHolder(modifier: Modifier = Modifier, label:String,fieldStyle: ComposeFieldStyle)  {
    when(ComposeFieldTheme.fieldStyle){
        ComposeFieldTheme.FieldStyle.OUTLINE -> null
        ComposeFieldTheme.FieldStyle.CONTAINER -> null
        ComposeFieldTheme.FieldStyle.NORMAL -> null
        ComposeFieldTheme.FieldStyle.STICK_LABEL -> Text(
            modifier = Modifier.then(modifier),
            text = label,
            style = fieldStyle.getHintTextStyle(),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun GetLabel(modifier: Modifier = Modifier, field: ComposeFieldModule) {
    when(ComposeFieldTheme.fieldStyle){
        ComposeFieldTheme.FieldStyle.OUTLINE ,
        ComposeFieldTheme.FieldStyle.CONTAINER -> {
            val label = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = responsiveTextSize(size = 13).sp)) {
                    append(field.label)
                }
                if (field.required == ComposeFieldYesNo.YES) {
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
            Text(label, style = field.fieldStyle.getLabelTextStyle())
        }
        ComposeFieldTheme.FieldStyle.NORMAL -> Text(field.label, style = field.fieldStyle.getLabelTextStyle())
        ComposeFieldTheme.FieldStyle.STICK_LABEL -> null
    }
}