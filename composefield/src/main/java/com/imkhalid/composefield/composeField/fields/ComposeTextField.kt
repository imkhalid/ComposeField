package com.imkhalid.composefieldproject.composeField.fields

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.FieldMaskTransformation
import com.imkhalid.composefield.composeField.Patterns
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.ozonedDigital.jhk.ui.common.responsiveTextSize
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
        val toolbar = if (state.field.keyboardType == ComposeKeyboardType.SENSITIVE)
            LocalTextToolbar provides EmptyTextToolbar
        else LocalTextToolbar provides LocalTextToolbar.current

        Column(modifier=modifier) {
            CompositionLocalProvider(toolbar) {
                when (ComposeFieldTheme.fieldStyle) {
                    ComposeFieldTheme.FieldStyle.OUTLINE -> OutlineField(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        newValue = newValue
                    )

                    ComposeFieldTheme.FieldStyle.CONTAINER -> ContainerField(
                        modifier = Modifier.fillMaxWidth(),
                        state = state,
                        newValue = newValue
                    )

                    ComposeFieldTheme.FieldStyle.NORMAL -> NormalField(
                        modifier = Modifier.fillMaxWidth(),
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
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val mask = getFieldMask(state.field)
        var passwordVisible by remember { mutableStateOf(false) }
        val visualTransformation =
            getVisualTransformation(mask, state.field.keyboardType, passwordVisible)

        TextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                if (mask != Patterns.NONE && mask.value.isNotEmpty()) {
                    if (curVal.length <= mask.length) {
                        builtinValidations(curVal, state) { validated, newVal ->
                            newValue.invoke(validated, newVal)
                        }
                    }
                } else {
                    builtinValidations(curVal, state) { validated, newVal ->
                        newValue.invoke(validated, newVal)
                    }
                }

            },
            prefix = {
                if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                    Text(text = "+1", modifier = Modifier.clickable {

                    })
                else null
            },
            keyboardOptions = getKeyboardOptions(state.field),
            isError = state.hasError,
            label = { Text(state.field.label) },
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorLabelColor = ComposeFieldTheme.errorColor,
                focusedLabelColor = ComposeFieldTheme.hintColor,
                focusedTextColor = ComposeFieldTheme.textColor,
                unfocusedTextColor = ComposeFieldTheme.textColor,
                focusedSupportingTextColor = ComposeFieldTheme.infoColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .padding(5.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
            trailingIcon = {
                TrailingIcon(state.field, passwordVisible = passwordVisible) {
                    passwordVisible = passwordVisible.not()
                }
            }
        )
        if (state.hasError) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    @Composable
    private fun ContainerField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val mask = getFieldMask(state.field)
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }
        val visualTransformation =
            getVisualTransformation(mask, state.field.keyboardType, passwordVisible)


        TextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                if (mask != Patterns.NONE && mask.value.isNotEmpty()) {
                    if (curVal.length <= mask.length) {
                        builtinValidations(curVal, state) { validated, newVal ->
                            newValue.invoke(validated, newVal)
                        }
                    }
                } else {
                    builtinValidations(curVal, state) { validated, newVal ->
                        newValue.invoke(validated, newVal)
                    }
                }

            },
            prefix = {
                if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                    Text(text = "+1", modifier = Modifier.clickable {

                    })
                else null
            },
            keyboardOptions = getKeyboardOptions(state.field),
            isError = state.hasError,
            label = {
                Text(
                    state.field.label,
                    fontSize = responsiveTextSize(size = 13).sp
                )
            },
            textStyle = TextStyle.Default.copy(
                fontSize = responsiveTextSize(size = 15).sp
            ),
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.colors(
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
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged { s ->
                    isFocused = s.isFocused
                }
                .padding(5.dp)
                .border(
                    width = if (isFocused) 1.dp else 0.dp,
                    color = if (isFocused) ComposeFieldTheme.focusedBorderColor else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                )
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
            trailingIcon = {
                TrailingIcon(state.field, passwordVisible = passwordVisible) {
                    passwordVisible = passwordVisible.not()
                }
            }
        )
        if (state.hasError) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }


    @Composable
    private fun OutlineField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val mask = getFieldMask(state.field)
        var passwordVisible by remember { mutableStateOf(false) }
        val visualTransformation =
            getVisualTransformation(mask, state.field.keyboardType, passwordVisible)

        OutlinedTextField(
            value = state.text,
            enabled = state.field.isEditable.value,
            onValueChange = { curVal ->
                if (mask != Patterns.NONE && mask.value.isNotEmpty()) {
                    if (curVal.length <= mask.length) {
                        builtinValidations(curVal, state) { validated, newVal ->
                            newValue.invoke(validated, newVal)
                        }
                    }
                } else {
                    builtinValidations(curVal, state) { validated, newVal ->
                        newValue.invoke(validated, newVal)
                    }
                }

            },
            prefix = {
                if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                    Text(text = "+1", modifier = Modifier.clickable {

                    })
                else null
            },
            keyboardOptions = getKeyboardOptions(state.field),
            isError = state.hasError,
            label = {
                val label =buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = responsiveTextSize(size = 13).sp
                        )
                    ) {
                        append(state.field.label)
                    }
                    if (state.field.required==ComposeFieldYesNo.YES){
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
                Text(
                    label,
                    fontSize = responsiveTextSize(size = 13).sp
                )
            },
            textStyle = TextStyle.Default.copy(
                fontSize = responsiveTextSize(size = 16).sp
            ),
            minLines = getMinLine(state.field.type),
            maxLines = getMaxLine(state.field.type),
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ComposeFieldTheme.focusedBorderColor,
                unfocusedBorderColor = ComposeFieldTheme.unfocusedBorderColor,
                errorBorderColor = ComposeFieldTheme.errorColor,
                errorLabelColor = ComposeFieldTheme.errorColor,
                focusedLabelColor = ComposeFieldTheme.focusedBorderColor,
                unfocusedPlaceholderColor = ComposeFieldTheme.hintColor,
                focusedTextColor = ComposeFieldTheme.textColor,
                unfocusedTextColor = ComposeFieldTheme.textColor,
                focusedSupportingTextColor = ComposeFieldTheme.infoColor,
            ),
            modifier = modifier,
            trailingIcon = {
                TrailingIcon(field = state.field, passwordVisible = passwordVisible) {
                    passwordVisible = passwordVisible.not()
                }
            }
        )
        if (state.hasError) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }


    private fun getMinLine(type: ComposeFieldType): Int {
        return when (type) {
            ComposeFieldType.TEXT_BOX -> 1
            ComposeFieldType.TEXT_AREA -> 3
            else -> 1
        }
    }

    private fun getMaxLine(type: ComposeFieldType): Int {
        return when (type) {
            ComposeFieldType.TEXT_BOX -> 1
            ComposeFieldType.TEXT_AREA -> 4
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
        ) {
        }
    }

    private fun getKeyboardOptions(fieldState: ComposeFieldModule): KeyboardOptions {
        val type = when (fieldState.keyboardType) {
            ComposeKeyboardType.CNIC,
            ComposeKeyboardType.ID_NO,
            ComposeKeyboardType.MOBILE_NO,
            ComposeKeyboardType.NUMBER -> KeyboardType.Number

            ComposeKeyboardType.EMAIL -> KeyboardType.Email
            ComposeKeyboardType.TEXT,
            ComposeKeyboardType.SENSITIVE,
            ComposeKeyboardType.NONE -> KeyboardType.Text

            ComposeKeyboardType.PASSWORD -> KeyboardType.Password
        }
        return KeyboardOptions(
            keyboardType = type,
            autoCorrect = false,
            imeAction = ImeAction.Next
        )
    }

    private fun getVisualTransformation(
        mask: Patterns,
        keyboardType: ComposeKeyboardType,
        passwordVisible: Boolean
    ): VisualTransformation {
        return if (mask != Patterns.MOBILE && mask != Patterns.NONE && mask.value.isNotEmpty())
            FieldMaskTransformation(mask.value)
        else if (keyboardType == ComposeKeyboardType.PASSWORD)
            if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(mask = '●')
        else
            VisualTransformation.None
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
            ComposeKeyboardType.CNIC -> {
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
            ComposeKeyboardType.ID_NO->{
                val pattern = Patterns.ID_NO.pattern.toList()
                if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                    bool = false
                    message = "ID No must 8 Character Long"
                }else {
                    message = ""
                }
            }

            ComposeKeyboardType.EMAIL -> {
                val pattern = Patterns.EMAIL.pattern.toList()
                if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                    bool = false
                    message = "Please enter valid Email Address"
                }
            }

            ComposeKeyboardType.TEXT -> {
                if (state.field.name.contains("email", true)) {
                    val pattern = Patterns.EMAIL.pattern.toList()
                    if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                        bool = false
                        message = "Please enter valid Email Address"
                    }
                } else if (state.field.pattern.isNotEmpty() && Pattern.matches(
                        state.field.pattern,
                        valueToBeUsed
                    ).not()
                ) {
                    bool = false
                    message = state.field.patternMessage
                }
            }

            else -> {
                if (state.field.pattern.isNotEmpty() && Pattern.matches(
                        state.field.pattern,
                        valueToBeUsed
                    ).not()
                ) {
                    bool = false
                    message = state.field.patternMessage
                }

            }
        }
        newValue.invoke(Pair(bool, message), curVal)
    }

    private fun getFieldMask(module: ComposeFieldModule): Patterns {
        val keyboardType = module.keyboardType

        return when (keyboardType) {
            ComposeKeyboardType.CNIC -> Patterns.CNIC
            ComposeKeyboardType.ID_NO -> Patterns.ID_NO
            ComposeKeyboardType.MOBILE_NO ->
                Patterns.MOBILE.apply {
                    value = ""
                    pattern = arrayOf("")
                }

            ComposeKeyboardType.EMAIL -> Patterns.EMAIL
            ComposeKeyboardType.NUMBER -> {
//                module.maxValue.takeIf { x->x?.isNotEmpty()==true }?.let {
//                    var mask = ""
//                    it.indices.forEachIndexed { index, i ->
//                        if (index < it.lastIndex)
//                            mask+="#"
//                        if (index.plus(1)/3==1 && index<it.lastIndex)
//                            mask+=","
//                    }
//                    return@let mask.reversed()
//                }?:run {
//                    ""
//                }
                Patterns.NONE
            }

            ComposeKeyboardType.SENSITIVE,
            ComposeKeyboardType.PASSWORD,
            ComposeKeyboardType.TEXT,
            ComposeKeyboardType.NONE -> Patterns.NONE
        }
    }

    private fun getValueWithMask(currValue: String, field: ComposeFieldModule): String {
        val mask = when (field.keyboardType) {
            ComposeKeyboardType.CNIC -> Patterns.CNIC.value
            ComposeKeyboardType.ID_NO -> Patterns.ID_NO.value
            ComposeKeyboardType.MOBILE_NO -> Patterns.MOBILE.value
            ComposeKeyboardType.EMAIL -> Patterns.EMAIL.value
            ComposeKeyboardType.TEXT,
            ComposeKeyboardType.NUMBER,
            ComposeKeyboardType.SENSITIVE,
            ComposeKeyboardType.PASSWORD,
            ComposeKeyboardType.NONE -> Patterns.NONE.value
        }

        val transforation = FieldMaskTransformation(mask)
        return if (Patterns.NONE.value == mask) {
            currValue
        } else
            transforation.applyMaskAndGetResult(currValue)
    }


}

@Preview
@Composable
private fun ShadowSample() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Shadow Order")

        Box(
            modifier = Modifier
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
            modifier = Modifier
                .background(Color.Red)
                .size(100.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello World")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(Color.Red)
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Hello World")
        }
    }
}