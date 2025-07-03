package com.imkhalid.composefield.composeField.fields

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.PhoneNumberUtil
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.util.ErrorView
import com.imkhalid.composefield.composeField.util.ShowToolTip
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import com.imkhalid.composefieldproject.composeField.fields.GetPlaceHolder

class ComposeMobileField : ComposeField() {

    @Composable
    override fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val phoneNumberUtil: MutableState<PhoneNumberUtil> = remember {
            mutableStateOf(PhoneNumberUtil().apply {
                 if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO) {
                     state.field.keyboardType.let {
                         shouldShowPicker=it.isSelectionDisabled.not()
                             currentCountryCode = it.countryCode.takeIf { x -> x.isNotEmpty() }
                                 ?: currentUserCountryCode
                     }
                 }
                setDefaultCountry(currentCountryCode)
                setCountryOfSelectedText(state.text, this) }
            )
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
        val context = LocalContext.current
        val launcher= if(state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO && state.field.keyboardType.showPicker){
            rememberLauncherForActivityResult(PickContact()) { uri ->
                uri?.let { contactUri ->
                    val projection = arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                    )

                    context.contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)

                            val name = cursor.getString(nameIndex)
                            var phone = cursor.getString(phoneIndex)

                            phone = phone?.replace("+92", "")?.removePrefix("0") ?: ""
                            selectedContactCallback?.invoke(
                                name ?: "",
                                phone
                            )
                        }
                    }
                }
            }
        } else null

        when (state.field.fieldStyle.fieldStyle) {
            ComposeFieldTheme.FieldStyle.OUTLINE ->
                OutlineField(
                    modifier = modifier,
                    state = state,
                    newValue = newValue,
                    phoneNumberUtil = phoneNumberUtil,
                    launcher= launcher
                )
            ComposeFieldTheme.FieldStyle.CONTAINER ->
                ContainerField(
                    modifier = modifier,
                    state = state,
                    newValue = newValue,
                    phoneNumberUtil = phoneNumberUtil,
                    launcher= launcher
                )
            ComposeFieldTheme.FieldStyle.NORMAL ->
                NormalField(
                    modifier = modifier,
                    state = state,
                    newValue = newValue,
                    phoneNumberUtil = phoneNumberUtil,
                    launcher= launcher
                )

            ComposeFieldTheme.FieldStyle.STICK_LABEL->
                StickyLabelField(
                    modifier = modifier,
                    state = state,
                    newValue = newValue,
                    phoneNumberUtil = phoneNumberUtil,
                    launcher= launcher
                )
        }
    }

    @Composable
    private fun NormalField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil,
        launcher: ManagedActivityResultLauncher<Unit, Uri?>?
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val fieldStyle = state.field.fieldStyle
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
                    if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO)
                        Text(
                            text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}",
                            modifier = Modifier.clickable { toggleDropdown() },
                            style = fieldStyle.getTextStyle()
                        )
                    else null
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                isError = state.hasError,
                label = { Text(state.field.label) },
                minLines = 1,
                maxLines = 1,
                textStyle = fieldStyle.getTextStyle(),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorLabelColor = fieldStyle.colors.errorColor,
                        focusedLabelColor = fieldStyle.colors.focusedBorderColor,
                        unfocusedPlaceholderColor = fieldStyle.colors.hintColor,
                        focusedTextColor = fieldStyle.colors.textColor,
                        unfocusedTextColor = fieldStyle.colors.textColor,
                        focusedSupportingTextColor = fieldStyle.colors.infoColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorContainerColor = Color(0xFFfaebeb),
                        errorTextColor = fieldStyle.colors.textColor
                    ),
                shape = RoundedCornerShape(8.dp),
                modifier =
                    Modifier.then(modifier)
                        .padding(5.dp)
                        .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp)),
            )
            ErrorView(
                state = state,
                modifier = Modifier.padding(start = 16.dp)
            )
            if (expanded && phoneNumberUtil.shouldShowPicker) {
                CountryPickerDialog(
                    onDone = { toggleDropdown() },
                    onOptionSelected = { countryModel ->
                        phoneNumberUtil.apply {
                            minLength = countryModel.length
                            maxLength = countryModel.maxLength
                            currentCountryFlag = countryModel.emoji
                            currentCountryCode = countryModel.code
                            prefix = countryModel.dialCode
                        }
                        newValue.invoke(Pair(true, ""), "")
                        toggleDropdown()
                    }
                )
            }
        }
    }


    @Composable
    private fun ContainerField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil,
        launcher: ManagedActivityResultLauncher<Unit, Uri?>?
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        val fieldStyle = state.field.fieldStyle
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        Column {
            TextField(
                value = state.text.removePrefix("+"+phoneNumberUtil.prefix),
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if (curVal.length <= phoneNumberUtil.maxLength) {
                        builtinValidations(curVal, phoneNumberUtil) { validated, newVal ->
                            newValue.invoke(validated, "+"+phoneNumberUtil.prefix.plus(newVal))
                        }
                    }
                },
                prefix = {
                    if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO)
                        Text(
                            text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}",
                            modifier = Modifier.clickable { toggleDropdown() },
                            style =fieldStyle.getTextStyle()
                        )
                    else null
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                isError = state.hasError,
                label = {
                    val label = buildAnnotatedString {
                        append(state.field.label)
                        if (state.field.required == ComposeFieldYesNo.YES) {
                            withStyle(style = SpanStyle(color = Color.Red)) {
                                append("*")
                            }
                        }
                    }
                    Text(
                        text = label,
                        style = fieldStyle.getLabelTextStyle()
                    )
                },
                trailingIcon = trailingIcon(field = state.field,false){
                    launcher?.launch(Unit)
                },
                textStyle = fieldStyle.getTextStyle(),
                minLines = 1,
                maxLines = 1,
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        errorLabelColor = fieldStyle.colors.errorColor,
                        focusedLabelColor = fieldStyle.colors.focusedLabelColor,
                        unfocusedLabelColor = fieldStyle.colors.unfocusedLabelColor,
                        unfocusedPlaceholderColor = fieldStyle.colors.hintColor,
                        focusedTextColor = fieldStyle.colors.textColor,
                        unfocusedTextColor = fieldStyle.colors.textColor,
                        focusedSupportingTextColor = fieldStyle.colors.infoColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorContainerColor = Color(0xFFfaebeb),
                        errorTextColor = fieldStyle.colors.textColor
                    ),
                shape = RoundedCornerShape(8.dp),
                modifier =
                    modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(localRequester)
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
            )
            ErrorView(
                state = state,
                modifier = Modifier.padding(start = 16.dp)
            )
            if(expanded && phoneNumberUtil.shouldShowPicker) {
                CountryPickerDialog(
                    onDone = { toggleDropdown() },
                    onOptionSelected = { countryModel ->
                        phoneNumberUtil.apply {
                            minLength = countryModel.length
                            maxLength = countryModel.maxLength
                            currentCountryFlag = countryModel.emoji
                            currentCountryCode = countryModel.code
                            prefix = countryModel.dialCode
                        }
                        newValue.invoke(Pair(true, ""), "")
                        toggleDropdown()
                    }
                )
            }
        }
    }


    @Composable
    private fun StickyLabelField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil,
        launcher: ManagedActivityResultLauncher<Unit, Uri?>?
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(responsiveSize(5))
                ) {
                    Text(
                        modifier=Modifier,
                        text = state.field.label,
                        style = fieldStyle.getLabelTextStyle()
                    )

                    if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO && state.field.keyboardType.showPicker) {
                        Image(
                            painter = painterResource(R.drawable.ic_lib_contact),
                            contentDescription = "",
                            modifier = Modifier.size(responsiveSize(31))
                                .clip(CircleShape)
                                .clickable {
                                    launcher?.launch(Unit)
                                }
                        )
                    }
                }
                if (state.field.hint.isNotEmpty())
                    ShowToolTip(state = state, modifier = Modifier)

                val prefix = "+"+phoneNumberUtil.prefix
                val userInput = state.text

                var textFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = userInput.ifEmpty { prefix },
                            selection = TextRange(userInput.length)
                        )
                    )
                }

                BasicTextField(
                    modifier = Modifier
                        .then(modifier)
                        .bringIntoViewRequester(localRequester),
                    value = textFieldValue,
                    onValueChange = { newVal ->
                        if (textFieldValue.text==prefix && newVal.text.length <= prefix.length) {
                            return@BasicTextField
                        }
                        val curVal = newVal.text.removePrefix(prefix)
                        if (curVal.length <= phoneNumberUtil.maxLength) {
                            builtinValidations(curVal, phoneNumberUtil) { validated, newVal ->
                                val finalValue =  "+" + phoneNumberUtil.prefix.plus(newVal)
                                textFieldValue = textFieldValue.copy(
                                    text = finalValue,
                                    selection = TextRange(finalValue.length)
                                )
                                newValue.invoke(validated, finalValue)
                            }
                        }
                    },
                    enabled = state.field.isEditable.value,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    minLines = 1,
                    maxLines = 1,
                    singleLine = true,
                    textStyle = fieldStyle.getTextStyle().copy(
                        textAlign = TextAlign.End,
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
//                            //Prefix
//                            Text(
//                                text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}",
//                                modifier = Modifier.clickable { toggleDropdown() },
//                                textAlign = TextAlign.End,
//                                style = fieldStyle.getTextStyle()
//                            )
                            // TextField content
                            Box(
                                modifier = Modifier.wrapContentWidth(),
                                contentAlignment = Alignment.CenterEnd,

                            ) {
                                if (state.text.isEmpty()) {
                                    GetPlaceHolder(
                                        fieldStyle = fieldStyle,
                                        label = if (state.field.required == ComposeFieldYesNo.YES) "Required" else "Optional"
                                    )
                                }
                                innerTextField()
                            }

                        }
                    }
                )
            }
            ErrorView(
                modifier =  Modifier
                    .align(Alignment.BottomEnd),
                state = state
            )
            if(expanded && phoneNumberUtil.shouldShowPicker) {
                CountryPickerDialog(
                    onDone = { toggleDropdown() },
                    onOptionSelected = { countryModel ->
                        phoneNumberUtil.apply {
                            minLength = countryModel.length
                            maxLength = countryModel.maxLength
                            currentCountryFlag = countryModel.emoji
                            currentCountryCode = countryModel.code
                            prefix = countryModel.dialCode
                        }
                        newValue.invoke(Pair(true, ""), "")
                        toggleDropdown()
                    }
                )
            }
        }
    }

    private fun setCountryOfSelectedText(text: String, phoneNumberUtil: PhoneNumberUtil) {
        PhoneNumberUtil.numbers
            .find { x -> text.startsWith("+"+x.dialCode) }
            ?.let {
                phoneNumberUtil.apply {
                    prefix = it.dialCode
                    currentCountryCode = it.code
                    currentCountryFlag = it.emoji
                }
            }
    }

    @Composable
    private fun OutlineField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        phoneNumberUtil: PhoneNumberUtil,
        launcher: ManagedActivityResultLauncher<Unit, Uri?>?
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        val fieldStyle = state.field.fieldStyle
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
                    if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO)
                        Text(
                            text = "${phoneNumberUtil.currentCountryFlag}${phoneNumberUtil.prefix}",
                            modifier = Modifier.clickable { toggleDropdown() }
                        )
                    else null
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                isError = state.hasError,
                label = {
                    Text(
                        state.field.label,
                        style = fieldStyle.getLabelTextStyle()
                    )
                },
                minLines = 1,
                maxLines = 1,
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = fieldStyle.colors.focusedBorderColor,
                        unfocusedBorderColor = fieldStyle.colors.unfocusedBorderColor,
                        errorBorderColor = fieldStyle.colors.errorColor,
                        errorLabelColor = fieldStyle.colors.errorColor,
                        focusedLabelColor = fieldStyle.colors.focusedLabelColor,
                        unfocusedPlaceholderColor = fieldStyle.colors.hintColor,
                        focusedTextColor = fieldStyle.colors.textColor,
                        unfocusedTextColor = fieldStyle.colors.textColor,
                        focusedSupportingTextColor = fieldStyle.colors.infoColor,
                        errorContainerColor = Color(0xFFfaebeb),
                        errorTextColor = fieldStyle.colors.errorColor
                    ),
                modifier = Modifier.then(modifier),
            )
            ErrorView(
                state = state,
                modifier = Modifier.padding(start = 16.dp)
            )
            if (expanded) {
                CountryPickerDialog(
                    onDone = { toggleDropdown() },
                    onOptionSelected = { countryModel ->
                        phoneNumberUtil.apply {
                            minLength = countryModel.length
                            maxLength = countryModel.maxLength
                            currentCountryFlag = countryModel.emoji
                            currentCountryCode = countryModel.code
                            prefix = countryModel.dialCode
                        }
                        newValue.invoke(Pair(true, ""), "")
                        toggleDropdown()
                    }
                )
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
        val filteredOptions =
            countries.filter {
                it.name.contains(searchText, ignoreCase = true) || it.dialCode.contains(searchText)
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
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                        items(filteredOptions.size) { index ->
                            val item = filteredOptions[index]
                            TextButton(onClick = { onOptionSelected(item) }) {
                                Text(item.emoji + item.dialCode + " " + item.name)
                            }
                        }
                    }
                }
            },
            confirmButton = { Text(text = "Done", Modifier.clickable { onDone() }) }
        )
    }

    class PickContact : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) intent?.data else null
        }
    }

}
