package com.imkhalid.composefield.composeField.fields

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.PhoneNumberUtil
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.ozonedDigital.jhk.ui.common.responsiveTextSize


class ComposeMobileField : ComposeField() {

    @Composable
    override fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val phoneNumberUtil: MutableState<PhoneNumberUtil> = remember {
            mutableStateOf(PhoneNumberUtil())
        }

        MyBuild(
            state = state,
            newValue = newValue,
            modifier = modifier,
            phoneNumberUtil = phoneNumberUtil.value
        )
    }

    @Composable
    private fun MyBuild(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier,
        phoneNumberUtil: PhoneNumberUtil
    ) {
        when (ComposeFieldTheme.fieldStyle) {
            ComposeFieldTheme.FieldStyle.OUTLINE -> OutlineField(
                modifier = modifier,
                state = state,
                newValue = newValue,
                phoneNumberUtil = phoneNumberUtil
            )

            ComposeFieldTheme.FieldStyle.CONTAINER -> ContainerField(
                modifier = modifier,
                state = state,
                newValue = newValue,
                phoneNumberUtil = phoneNumberUtil
            )

            ComposeFieldTheme.FieldStyle.NORMAL -> NormalField(
                modifier = modifier,
                state = state,
                newValue = newValue,
                phoneNumberUtil = phoneNumberUtil
            )
        }

    }

    @Composable
    private fun NormalField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        Column {
            TextField(
                value = state.text.removePrefix(phoneNumberUtil.prefix),
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if ( curVal.length <= phoneNumberUtil.maxLength) {
                        builtinValidations(curVal, phoneNumberUtil) { validated, newVal ->
                            newValue.invoke(validated, phoneNumberUtil.prefix.plus(newVal))
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                        Text(
                            text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}",
                            modifier = Modifier.clickable {
                                toggleDropdown()
                            },
                            fontSize = responsiveTextSize(size = 15).sp
                        )
                    else null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                isError = state.hasError,
                label = { Text(state.field.label) },
                minLines = 1,
                maxLines = 1,
                textStyle = TextStyle.Default.copy(
                    fontSize = responsiveTextSize(size = 15).sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorLabelColor = ComposeFieldTheme.errorColor,
                    focusedLabelColor = ComposeFieldTheme.focusedBorderColor,
                    unfocusedPlaceholderColor = ComposeFieldTheme.hintColor,
                    focusedTextColor = ComposeFieldTheme.textColor,
                    unfocusedTextColor = ComposeFieldTheme.textColor,
                    focusedSupportingTextColor = ComposeFieldTheme.infoColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .then(modifier)
                    .padding(5.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(8.dp)
                    ),
                trailingIcon = {
                    TrailingIcon(state.field, passwordVisible = false) {
//                        passwordVisible = passwordVisible.not()
                    }
                }
            )
            if (state.hasError) {
                Text(
                    text = state.errorMessage,
                    color = ComposeFieldTheme.errorColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            if (expanded) {
                CountryPickerDialog(onDone = {
                    toggleDropdown()
                }, onOptionSelected = { countryModel ->
                    phoneNumberUtil.apply {
                        minLength = countryModel.length
                        maxLength = countryModel.maxLength
                        currentCountryFlag = countryModel.emoji
                        currentCountryCode = countryModel.code
                        prefix = countryModel.dialCode
                    }
                    newValue.invoke(Pair(true, ""), "")
                    toggleDropdown()
                })
            }
        }
    }


    @Composable
    private fun ContainerField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        Column {
            TextField(
                value = state.text.removePrefix(phoneNumberUtil.prefix),
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if (curVal.length <= phoneNumberUtil.maxLength) {
                        builtinValidations(curVal, phoneNumberUtil) { validated, newVal ->
                            newValue.invoke(validated, phoneNumberUtil.prefix.plus(newVal))
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                        Text(
                            text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}",
                            modifier = Modifier.clickable {
                                toggleDropdown()
                            },
                            fontSize = responsiveTextSize(size = 15).sp
                        )
                    else null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                isError = state.hasError,
                label = {
                    val label = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = responsiveTextSize(size = 13).sp
                            )
                        ) {
                            append(state.field.label)
                        }
                        if (state.field.required == ComposeFieldYesNo.YES) {
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
                    fontSize = responsiveTextSize(size = 15).sp
                ),
                minLines = 1,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorLabelColor = ComposeFieldTheme.errorColor,
                    focusedLabelColor = ComposeFieldTheme.focusedLabelColor,
                    unfocusedLabelColor = ComposeFieldTheme.unfocusedLabelColor,
                    unfocusedPlaceholderColor = ComposeFieldTheme.hintColor,
                    focusedTextColor = ComposeFieldTheme.textColor,
                    unfocusedTextColor = ComposeFieldTheme.textColor,
                    focusedSupportingTextColor = ComposeFieldTheme.infoColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = modifier
                    .fillMaxWidth()
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
                    TrailingIcon(state.field, passwordVisible = false) {
//                        passwordVisible = passwordVisible.not()
                    }
                }
            )
            if (state.hasError) {
                Text(
                    text = state.errorMessage,
                    color = ComposeFieldTheme.errorColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            if (expanded) {
                CountryPickerDialog(onDone = {
                    toggleDropdown()
                }, onOptionSelected = { countryModel ->
                    phoneNumberUtil.apply {
                        minLength = countryModel.length
                        maxLength = countryModel.maxLength
                        currentCountryFlag = countryModel.emoji
                        currentCountryCode = countryModel.code
                        prefix = countryModel.dialCode
                    }
                    newValue.invoke(Pair(true, ""), "")
                    toggleDropdown()
                })
            }
        }
    }

    @Composable
    private fun OutlineField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        Column {
            OutlinedTextField(
                value = state.text.removePrefix(phoneNumberUtil.prefix),
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if (curVal.length <= phoneNumberUtil.maxLength) {
                        builtinValidations(curVal, phoneNumberUtil) { validated, newVal ->
                            newValue.invoke(validated, phoneNumberUtil.prefix.plus(newVal))
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                        Text(text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}", modifier = Modifier.clickable {
                            toggleDropdown()
                        })
                    else null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                isError = state.hasError,
                label = { Text(state.field.label) },
                minLines = 1,
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ComposeFieldTheme.focusedBorderColor,
                    unfocusedBorderColor = ComposeFieldTheme.unfocusedBorderColor,
                    errorBorderColor = ComposeFieldTheme.errorColor,
                    errorLabelColor = ComposeFieldTheme.errorColor,
                    focusedLabelColor = ComposeFieldTheme.focusedBorderColor,
                    unfocusedPlaceholderColor = ComposeFieldTheme.hintColor,
                    focusedTextColor = ComposeFieldTheme.textColor,
                    unfocusedTextColor = ComposeFieldTheme.textColor,
                    focusedSupportingTextColor = ComposeFieldTheme.infoColor
                ),
                modifier = Modifier
                    .then(modifier),
                trailingIcon = {
                    TrailingIcon(field = state.field, passwordVisible = false) {
//                        passwordVisible = passwordVisible.not()
                    }
                }
            )
            if (state.hasError) {
                Text(
                    text = state.errorMessage,
                    color = ComposeFieldTheme.errorColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            if (expanded) {
                CountryPickerDialog(onDone = {
                    toggleDropdown()
                }, onOptionSelected = {countryModel ->
                    phoneNumberUtil.apply {
                        minLength = countryModel.length
                        maxLength = countryModel.maxLength
                        currentCountryFlag = countryModel.emoji
                        currentCountryCode = countryModel.code
                        prefix = countryModel.dialCode
                    }
                    newValue.invoke(Pair(true, ""), "")
                    toggleDropdown()
                })
            }
        }
    }


    fun builtinValidations(
        curVal: String,
        phoneNumberUtil: PhoneNumberUtil,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        var bool = true
        var message = ""
        bool = phoneNumberUtil.validateNumbers(curVal)
        message = "Please enter valid Phone Number"
        newValue.invoke(Pair(bool, message), curVal)
    }


    @Composable
    private fun CountryPickerDialog(
        onDone: () -> Unit,
        onOptionSelected: (PhoneNumberUtil.CountryModel) -> Unit
    ) {

        val countries = PhoneNumberUtil.numbers
        var searchText by remember { mutableStateOf("") }
        val filteredOptions = countries.filter {
            it.name.contains(
                searchText,
                ignoreCase = true
            ) || it.dialCode.contains(searchText)
        }

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
                        items(filteredOptions.size) { index ->
                            val item = filteredOptions[index]
                            TextButton(onClick = {
                                onOptionSelected(item)
                            }) {
                                Text(item.emoji  + item.dialCode + " " + item.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Text(text = "Done", Modifier.clickable {
                    onDone()
                })
            }
        )
    }

}