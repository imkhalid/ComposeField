package com.imkhalid.composefield.composeField.fields


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.mask.rememberCurrencyVisualTransformation
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.util.EnglishNumberToWords
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.util.ErrorView
import com.imkhalid.composefield.composeField.util.ShowToolTipField
import com.imkhalid.composefieldproject.composeField.fields.GetPlaceHolder
import java.text.DecimalFormat


class ComposeCurrencyField : ComposeField() {

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
        val fieldStyle = state.field.fieldStyle

        val colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorLabelColor = fieldStyle.colors.errorColor,
            focusedLabelColor = fieldStyle.colors.labelColor,
            focusedTextColor = fieldStyle.colors.textColor,
            unfocusedTextColor = fieldStyle.colors.textColor,
            focusedSupportingTextColor = fieldStyle.colors.infoColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledTextColor = Color(0xFFBDBDBD),
            disabledLabelColor = Color(0xFF9E9E9E),
            disabledPlaceholderColor = Color(0xFFBDBDBD),
            disabledContainerColor = Color(0xFFE0E0E0),
            errorContainerColor = Color(0xFFfaebeb),
            errorTextColor = fieldStyle.colors.textColor
        )

        Column {
            when (fieldStyle.fieldStyle) {
                ComposeFieldTheme.FieldStyle.OUTLINE ->
                    OutlineField(
                        modifier = Modifier.fillMaxWidth(),
                        colors = colors,
                        state = state,
                        newValue = newValue
                    )

                ComposeFieldTheme.FieldStyle.STICK_LABEL->
                    StickLabelField(
                        state = state,
                        newValue = newValue
                    )
                ComposeFieldTheme.FieldStyle.CONTAINER ->
                    ContainerField(
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            errorLabelColor = fieldStyle.colors.errorColor,
                            focusedLabelColor = fieldStyle.colors.focusedLabelColor,
                            unfocusedLabelColor = fieldStyle.colors.unfocusedLabelColor,
                            unfocusedPlaceholderColor = fieldStyle.colors.unfocusedLabelColor,
                            focusedTextColor = fieldStyle.colors.textColor,
                            unfocusedTextColor = fieldStyle.colors.textColor,
                            focusedSupportingTextColor = fieldStyle.colors.infoColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledTextColor = Color(0xFFBDBDBD),
                            disabledLabelColor = Color(0xFF9E9E9E),
                            disabledPlaceholderColor = Color(0xFFBDBDBD),
                            disabledContainerColor = Color(0xFFE0E0E0),
                            errorContainerColor = Color(0xFFfaebeb),
                            errorTextColor = fieldStyle.colors.textColor,
                        ),
                        state = state,
                        newValue = newValue
                    )

                ComposeFieldTheme.FieldStyle.NORMAL ->
                    NormalField(
                        modifier = Modifier.fillMaxWidth(),
                        colors = colors,
                        state = state,
                        newValue = newValue
                    )
            }
        }
    }

    @Composable
    private fun NormalField(
        modifier: Modifier = Modifier,
        colors: TextFieldColors,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        var passwordVisible by remember { mutableStateOf(false) }
        val currency =
            if (currentUserCountryCode == "2") "KES" else if (currentUserCountryCode == "3") "TZS" else "UGX"
        val visualTransformation = rememberCurrencyVisualTransformation(currency = currency)
        var helper by remember {
            mutableStateOf("")
        }

        TextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                builtinValidations(
                    curVal,
                    state,
                    helperCallback = {
                        helper= it
                    }
                ) { validated, newVal ->
                    newValue.invoke(validated, newVal)
                }
            },
            prefix = {
                if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO)
                    Text(text = "+1", modifier = Modifier.clickable {})
                else null
            },
            keyboardOptions = getKeyboardOptions(),
            isError = state.hasError,
            label = { Text(state.field.label) },
            maxLines = 1,
            visualTransformation = visualTransformation,
            colors = colors,
            shape = RoundedCornerShape(8.dp),
            modifier =
            modifier
                .padding(5.dp)
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp)),
            trailingIcon = trailingIcon(state.field, passwordVisible = passwordVisible) {
                passwordVisible = passwordVisible.not()
            }
        )
        if (state.hasError) {
            Text(
                text = state.errorMessage,
                color = ComposeFieldTheme.errorMessageColor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }


    @Composable
    private fun ContainerField(
        modifier: Modifier = Modifier,
        colors: TextFieldColors,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }
        val currency =
            if (currentUserCountryCode == "2") "KES" else if (currentUserCountryCode == "3") "TZS" else "UGX"
        val visualTransformation = rememberCurrencyVisualTransformation(currency = currency)
        var helper by remember {
            mutableStateOf("")
        }


        TextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                builtinValidations(
                    curVal,
                    state,
                    helperCallback = {
                        helper=it
                    }
                ) { validated, newVal ->
                    newValue.invoke(validated, newVal)
                }
            },

            keyboardOptions = getKeyboardOptions(),
            isError = state.hasError,
            label = {
                val label = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = responsiveTextSize(size = 13).sp)) {
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
                Text(label, style = state.field.fieldStyle.getLabelTextStyle())
            },
            textStyle = state.field.fieldStyle.getTextStyle(),
            maxLines = 1,
            visualTransformation = visualTransformation,
            colors = colors,
            shape = RoundedCornerShape(8.dp),
            modifier =
            modifier
                .bringIntoViewRequester(localRequester)
                .focusRequester(focusRequester)
                .onFocusChanged { s -> isFocused = s.isFocused }
                .padding(start = 5.dp, top = 5.dp, end = 5.dp)
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
        if (helper.isNotEmpty())
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, bottom = 5.dp),
                text =helper,
                style = state.field.fieldStyle.getHelderTextStyle()
            )
        ErrorView(
            state = state,
            modifier = Modifier.padding(start = 16.dp)
        )
    }

    @Composable
    private fun OutlineField(
        modifier: Modifier = Modifier,
        colors: TextFieldColors,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        var passwordVisible by remember { mutableStateOf(false) }
        val currency =
            if (currentUserCountryCode == "2") "KES" else if (currentUserCountryCode == "3") "TZS" else "UGX"
        val visualTransformation = rememberCurrencyVisualTransformation(currency = currency)
        var helper by remember {
            mutableStateOf("")
        }

        val style = state.field.fieldStyle
        OutlinedTextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                builtinValidations(
                    curVal, state, helperCallback = { helper = it }
                ) { validated, newVal ->
                    newValue.invoke(validated, newVal)
                }
            },
            keyboardOptions = getKeyboardOptions(),
            isError = state.hasError,
            label = {
                val label = buildAnnotatedString {
                    append(state.field.label)
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
                Text(label, style = style.getLabelTextStyle())
            },
            textStyle = style.getTextStyle(),
            maxLines = 1,
            visualTransformation = visualTransformation,
            colors = colors,
            modifier = modifier,
            trailingIcon = trailingIcon(state.field, passwordVisible = passwordVisible) {
                passwordVisible = passwordVisible.not()
            }
        )
        ErrorView(
            state = state,
            modifier = Modifier.padding(start = 16.dp)
        )
    }

    @Preview
    @Composable
    private fun StickLabelField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit = {pair,s->}
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }
        val currency =
            if (currentUserCountryCode == "2") "KES" else if (currentUserCountryCode == "3") "TZS" else "UGX"
        val visualTransformation = rememberCurrencyVisualTransformation(currency = currency)
        var helper by remember {
            mutableStateOf("")
        }

        val fieldStyle = state.field.fieldStyle

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(TextFieldDefaults.MinHeight)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(responsiveSize(5))
            ) {
                Text(
                    text = state.field.label,
                    style = fieldStyle.getLabelTextStyle()
                )
                if (state.field.hint.isNotEmpty())
                    ShowToolTipField(state=state, modifier = Modifier)

                BasicTextField(
                    modifier = Modifier
                        .then(modifier)
                        .bringIntoViewRequester(localRequester)
                        .focusRequester(focusRequester)
                        .onFocusChanged { s -> isFocused = s.isFocused }
                        .fillMaxWidth()
                        .fillMaxHeight()
                    ,
                    value = state.text,
                    onValueChange = { curVal ->
                        handleValueChange(curVal,state,newValue, onHelper = {
                            helper= it
                        })
                    },
                    enabled = state.field.isEditable.value,
                    keyboardOptions = getKeyboardOptions(),
                    maxLines = 1,
                    singleLine = true,
                    visualTransformation = visualTransformation,
                    textStyle = fieldStyle.getTextStyle().copy(
                        textAlign = TextAlign.End,
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // TextField content
                            Box(
                                modifier = Modifier
                                    .weight(1f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (state.text.isEmpty()) {
                                    GetPlaceHolder(
                                        fieldStyle = fieldStyle,
                                        label = if (state.field.required == ComposeFieldYesNo.YES) "Required" else "Optional"
                                    )
                                }
                                innerTextField()
                            }

                            TrailingIconBasic(
                                state,
                                passwordVisible,
                                onClick = {
                                    passwordVisible = passwordVisible.not()
                                }
                            )

                        }
                    }
                )
            }
            if (helper.isNotEmpty())
                Text(
                    text = helper,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    style = state.field.fieldStyle.getHelderTextStyle()
                )
            ErrorView(
                modifier =  Modifier.padding(start = 16.dp),
                state = state
            )
        }

    }


    private fun getKeyboardOptions(): KeyboardOptions {
        val type = KeyboardType.NumberPassword
        return KeyboardOptions(keyboardType = type, autoCorrect = false, imeAction = ImeAction.Next)
    }

    private fun builtinValidations(
        currVal: String,
        state: ComposeFieldState,
        helperCallback:(String)->Unit,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        /*we will be using curVal for getValueWithMask and on final callback-> newValue
         * operations will be performed on value collected from getValueWithMask method*/

        val curVal = currVal.replace(",","")
        var bool = true
        var message = ""
        val formatter = DecimalFormat("#,###")

        val max = (state.field.maxValue?.takeIf { x->x.isNotEmpty() } ?: "-1").toLong()
        val min = (state.field.minValue?.takeIf { x->x.isNotEmpty() } ?: "-1").toLong()

        if (curVal.length <= 17) {
            if (min != -1L && (curVal.toLongOrNull()?:0L) < min) {
                bool = false
                message = "Minimum Value must be greater or equal to ${formatter.format(min)}"
            }
            if (max != -1L && (curVal.toLongOrNull()?:0L) > max) {
                bool = false
                message = "Maximum Value must be lesser or equal to ${formatter.format(max)}"
            }
            if (state.field.keyboardType is ComposeKeyboardTypeAdv.CURRENCY &&
                state.field.keyboardType.showEnglishWords &&
                curVal.replace(",","").isNotEmpty()
                ){
                helperCallback.invoke(EnglishNumberToWords.convert(curVal.toLong()))
            }else{
                helperCallback.invoke("")
            }
            newValue.invoke(Pair(bool, message), curVal)
        }
    }


    private fun handleValueChange(
        currentText: String,
        state: ComposeFieldState,
        onValidated: (Pair<Boolean, String>, String) -> Unit,
        onHelper: (String) -> Unit
    ) {
        builtinValidations(
            currentText,
            state,
            helperCallback = {
                onHelper.invoke(it)
            }
        ) { validated, newVal ->
            onValidated.invoke(validated, newVal)
        }
    }


}