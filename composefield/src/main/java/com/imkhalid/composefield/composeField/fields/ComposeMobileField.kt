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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeField
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil


class ComposeMobileField:ComposeField() {
    companion object {
        var phoneNumberUtil: PhoneNumberUtil? = null
        var preFix: String = "+92"
        var currentCountryCode = "PK"
        var length = 15
    }

    val DEFAULT_FLAG_RES = ""

    data class CountryModel(
        val code: String,
        val dialCode: String,
        val name: String,
        var emoji: String
    )


    @Composable
    fun Build(
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        when (ComposeFieldTheme.fieldStyle) {
            ComposeFieldTheme.FieldStyle.OUTLINE -> OutlineField(
                modifier = modifier,
                state = state,
                newValue = newValue
            )

            ComposeFieldTheme.FieldStyle.CONTAINER -> ContainerField(
                modifier = modifier,
                state = state,
                newValue = newValue
            )

            ComposeFieldTheme.FieldStyle.NORMAL -> NormalField(
                modifier = modifier,
                state = state,
                newValue = newValue
            )
        }

    }

    @Composable
    private fun NormalField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        if (phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(LocalContext.current)
        }
        Column {
            TextField(
                value = state.text,
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if (curVal.length <= length) {
                        builtinValidations(curVal) { validated, newVal ->
                            newValue.invoke(validated, newVal)
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                        Text(text = preFix, modifier = Modifier.clickable {
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
                modifier = Modifier
                    .then(modifier)
                    .padding(5.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(8.dp)
                    ),
                trailingIcon = {
                    TrailingIcon(state.field, passwordVisible = false){
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
                }, onOptionSelected = { iosCode: String, code: String ->
                    currentCountryCode = iosCode
                    preFix = "+$code"
                    toggleDropdown()
                })
            }
        }
    }


    @Composable
    private fun ContainerField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        if (phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(LocalContext.current)
        }
        Column {
            TextField(
                value = state.text,
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if (curVal.length <= length) {
                        builtinValidations(curVal) { validated, newVal ->
                            newValue.invoke(validated, newVal)
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                        Text(text = preFix, modifier = Modifier.clickable {
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
                modifier = Modifier
                    .then(modifier)
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
                    TrailingIcon(state.field, passwordVisible = false){
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
                }, onOptionSelected = { iosCode: String, code: String ->
                    currentCountryCode = iosCode
                    preFix = "+$code"
                    toggleDropdown()
                })
            }
        }
    }

    @Composable
    private fun OutlineField(
        modifier: Modifier = Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        val toggleDropdown: () -> Unit = { expanded = !expanded }
        if (phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(LocalContext.current)
        }
        Column {
            OutlinedTextField(
                value = state.text,
                enabled = state.field.isEditable.value,
                onValueChange = { curVal ->
                    if (curVal.length <= length) {
                        builtinValidations(curVal) { validated, newVal ->
                            newValue.invoke(validated, newVal)
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType == ComposeKeyboardType.MOBILE_NO)
                        Text(text = preFix, modifier = Modifier.clickable {
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
                    focusedLabelColor = ComposeFieldTheme.hintColor,
                    focusedTextColor = ComposeFieldTheme.textColor,
                    unfocusedTextColor = ComposeFieldTheme.textColor,
                    focusedSupportingTextColor = ComposeFieldTheme.infoColor
                ),
                modifier= Modifier
                    .then(modifier),
                trailingIcon = {
                    TrailingIcon(field = state.field, passwordVisible = false){
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
                }, onOptionSelected = { iosCode: String, code: String ->
                    currentCountryCode = iosCode
                    preFix = "+$code"
                    toggleDropdown()
                })
            }
        }
    }


    fun builtinValidations(curVal: String, newValue: (Pair<Boolean, String>, String) -> Unit) {
        var bool = true
        var message = ""
        val phone = try {
            phoneNumberUtil?.parseAndKeepRawInput(curVal, currentCountryCode.uppercase())
        } catch (e: Exception) {
            message = e.message ?: ""
            null
        }
        phone?.let {
            bool = phoneNumberUtil?.isValidNumber(it) ?: true
            message = "Please enter valid Phone Number"
            if (bool)
                length = curVal.length
            else
                length = 15
        } ?: run {
            bool = false
            length = 15
        }
        newValue.invoke(Pair(bool, message), curVal)
    }


    @Composable
    private fun CountryPickerDialog(
        onDone: () -> Unit,
        onOptionSelected: (IOSCode: String, code: String) -> Unit
    ) {

        val countries = (getLibraryMasterCountriesEnglish() ?: emptyList()).onEach { x ->
            x.emoji = getFlagEmoji(x.code)
        }
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
                                onOptionSelected(item.code, item.dialCode)
                            }) {
                                Text(item.emoji + " " + item.dialCode + " " + item.name)
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


    /**
     * Returns image res based on country name code
     *
     * @param CountryModel
     * @return
     */
    fun getFlagEmoji(name: String): String {
        return when (name) {
            "ad" -> "🇦🇩"
            "ae" -> "🇦🇪"
            "af" -> "🇦🇫"
            "ag" -> "🇦🇬"
            "ai" -> "🇦🇮"
            "al" -> "🇦🇱"
            "am" -> "🇦🇲"
            "ao" -> "🇦🇴"
            "aq" -> "🇦🇶"
            "ar" -> "🇦🇷"
            "as" -> "🇦🇸"
            "at" -> "🇦🇹"
            "au" -> "🇦🇺"
            "aw" -> "🇦🇼"
            "ax" -> "🇦🇽"
            "az" -> "🇦🇿"
            "ba" -> "🇧🇦"
            "bb" -> "🇧🇧"
            "bd" -> "🇧🇩"
            "be" -> "🇧🇪"
            "bf" -> "🇧🇫"
            "bg" -> "🇧🇬"
            "bh" -> "🇧🇭"
            "bi" -> "🇧🇮"
            "bj" -> "🇧🇯"
            "bl" -> "🇧🇱"
            "bm" -> "🇧🇲"
            "bn" -> "🇧🇳"
            "bo" -> "🇧🇴"
            "bq" -> "🇧🇶"
            "br" -> "🇧🇷"
            "bs" -> "🇧🇸"
            "bt" -> "🇧🇹"
            "bv" -> "🇧🇻"
            "bw" -> "🇧🇼"
            "by" -> "🇧🇾"
            "bz" -> "🇧🇿"
            "ca" -> "🇨🇦"
            "cc" -> "🇨🇨"
            "cd" -> "🇨🇩"
            "cf" -> "🇨🇫"
            "cg" -> "🇨🇬"
            "ch" -> "🇨🇭"
            "ci" -> "🇨🇮"
            "ck" -> "🇨🇰"
            "cl" -> "🇨🇱"
            "cm" -> "🇨🇲"
            "cn" -> "🇨🇳"
            "co" -> "🇨🇴"
            "cr" -> "🇨🇷"
            "cu" -> "🇨🇺"
            "cv" -> "🇨🇻"
            "cw" -> "🇨🇼"
            "cx" -> "🇨🇽"
            "cy" -> "🇨🇾"
            "cz" -> "🇨🇿"
            "de" -> "🇩🇪"
            "dj" -> "🇩🇯"
            "dk" -> "🇩🇰"
            "dm" -> "🇩🇲"
            "do" -> "🇩🇴"
            "dz" -> "🇩🇿"
            "ec" -> "🇪🇨"
            "ee" -> "🇪🇪"
            "eg" -> "🇪🇬"
            "eh" -> "🇪🇭"
            "er" -> "🇪🇷"
            "es" -> "🇪🇸"
            "et" -> "🇪🇹"
            "fi" -> "🇫🇮"
            "fj" -> "🇫🇯"
            "fk" -> "🇫🇰"
            "fm" -> "🇫🇲"
            "fo" -> "🇫🇴"
            "fr" -> "🇫🇷"
            "ga" -> "🇬🇦"
            "gb" -> "🇬🇧"
            "gd" -> "🇬🇩"
            "ge" -> "🇬🇪"
            "gf" -> "🇬🇫"
            "gg" -> "🇬🇬"
            "gh" -> "🇬🇭"
            "gi" -> "🇬🇮"
            "gl" -> "🇬🇱"
            "gm" -> "🇬🇲"
            "gn" -> "🇬🇳"
            "gp" -> "🇬🇵"
            "gq" -> "🇬🇶"
            "gr" -> "🇬🇷"
            "gs" -> "🇬🇸"
            "gt" -> "🇬🇹"
            "gu" -> "🇬🇺"
            "gw" -> "🇬🇼"
            "gy" -> "🇬🇾"
            "hk" -> "🇭🇰"
            "hm" -> "🇭🇲"
            "hn" -> "🇭🇳"
            "hr" -> "🇭🇷"
            "ht" -> "🇭🇹"
            "hu" -> "🇭🇺"
            "id" -> "🇮🇩"
            "ie" -> "🇮🇪"
            "il" -> "🇮🇱"
            "im" -> "🇮🇲"
            "in" -> "🇮🇳"
            "io" -> "🇮🇴"
            "iq" -> "🇮🇶"
            "ir" -> "🇮🇷"
            "is" -> "🇮🇸"
            "it" -> "🇮🇹"
            "je" -> "🇯🇪"
            "jm" -> "🇯🇲"
            "jo" -> "🇯🇴"
            "jp" -> "🇯🇵"
            "ke" -> "🇰🇪"
            "kg" -> "🇰🇬"
            "kh" -> "🇰🇭"
            "ki" -> "🇰🇮"
            "km" -> "🇰🇲"
            "kn" -> "🇰🇳"
            "kp" -> "🇰🇵"
            "kr" -> "🇰🇷"
            "kw" -> "🇰🇼"
            "ky" -> "🇰🇾"
            "kz" -> "🇰🇿"
            "la" -> "🇱🇦"
            "lb" -> "🇱🇧"
            "lc" -> "🇱🇨"
            "li" -> "🇱🇮"
            "lk" -> "🇱🇰"
            "lr" -> "🇱🇷"
            "ls" -> "🇱🇸"
            "lt" -> "🇱🇹"
            "lu" -> "🇱🇺"
            "lv" -> "🇱🇻"
            "ly" -> "🇱🇾"
            "ma" -> "🇲🇦"
            "mc" -> "🇲🇨"
            "md" -> "🇲🇩"
            "me" -> "🇲🇪"
            "mf" -> "🇲🇫"
            "mg" -> "🇲🇬"
            "mh" -> "🇲🇭"
            "mk" -> "🇲🇰"
            "ml" -> "🇲🇱"
            "mm" -> "🇲🇲"
            "mn" -> "🇲🇳"
            "mo" -> "🇲🇴"
            "mp" -> "🇲🇵"
            "mq" -> "🇲🇶"
            "mr" -> "🇲🇷"
            "ms" -> "🇲🇸"
            "mt" -> "🇲🇹"
            "mu" -> "🇲🇺"
            "mv" -> "🇲🇻"
            "mw" -> "🇲🇼"
            "mx" -> "🇲🇽"
            "my" -> "🇲🇾"
            "mz" -> "🇲🇿"
            "na" -> "🇳🇦"
            "nc" -> "🇳🇨"
            "ne" -> "🇳🇪"
            "nf" -> "🇳🇫"
            "ng" -> "🇳🇬"
            "ni" -> "🇳🇮"
            "nl" -> "🇳🇱"
            "no" -> "🇳🇴"
            "np" -> "🇳🇵"
            "nr" -> "🇳🇷"
            "nu" -> "🇳🇺"
            "nz" -> "🇳🇿"
            "om" -> "🇴🇲"
            "pa" -> "🇵🇦"
            "pe" -> "🇵🇪"
            "pf" -> "🇵🇫"
            "pg" -> "🇵🇬"
            "ph" -> "🇵🇭"
            "pk" -> "🇵🇰"
            "pl" -> "🇵🇱"
            "pm" -> "🇵🇲"
            "pn" -> "🇵🇳"
            "pr" -> "🇵🇷"
            "ps" -> "🇵🇸"
            "pt" -> "🇵🇹"
            "pw" -> "🇵🇼"
            "py" -> "🇵🇾"
            "qa" -> "🇶🇦"
            "re" -> "🇷🇪"
            "ro" -> "🇷🇴"
            "rs" -> "🇷🇸"
            "ru" -> "🇷🇺"
            "rw" -> "🇷🇼"
            "sa" -> "🇸🇦"
            "sb" -> "🇸🇧"
            "sc" -> "🇸🇨"
            "sd" -> "🇸🇩"
            "se" -> "🇸🇪"
            "sg" -> "🇸🇬"
            "sh" -> "🇸🇭"
            "si" -> "🇸🇮"
            "sj" -> "🇸🇯"
            "sk" -> "🇸🇰"
            "sl" -> "🇸🇱"
            "sm" -> "🇸🇲"
            "sn" -> "🇸🇳"
            "so" -> "🇸🇴"
            "sr" -> "🇸🇷"
            "ss" -> "🇸🇸"
            "st" -> "🇸🇹"
            "sv" -> "🇸🇻"
            "sx" -> "🇸🇽"
            "sy" -> "🇸🇾"
            "sz" -> "🇸🇿"
            "tc" -> "🇹🇨"
            "td" -> "🇹🇩"
            "tf" -> "🇹🇫"
            "tg" -> "🇹🇬"
            "th" -> "🇹🇭"
            "tj" -> "🇹🇯"
            "tk" -> "🇹🇰"
            "tl" -> "🇹🇱"
            "tm" -> "🇹🇲"
            "tn" -> "🇹🇳"
            "to" -> "🇹🇴"
            "tr" -> "🇹🇷"
            "tt" -> "🇹🇹"
            "tv" -> "🇹🇻"
            "tw" -> "🇹🇼"
            "tz" -> "🇹🇿"
            "ua" -> "🇺🇦"
            "ug" -> "🇺🇬"
            "um" -> "🇺🇲"
            "us" -> "🇺🇸"
            "uy" -> "🇺🇾"
            "uz" -> "🇺🇿"
            "va" -> "🇻🇦"
            "vc" -> "🇻🇨"
            "ve" -> "🇻🇪"
            "vg" -> "🇻🇬"
            "vi" -> "🇻🇮"
            "vn" -> "🇻🇳"
            "vu" -> "🇻🇺"
            "wf" -> "🇼🇫"
            "ws" -> "🇼🇸"
            "xk" -> "🇽🇰"
            "ye" -> "🇾🇪"
            "yt" -> "🇾🇹"
            "za" -> "🇿🇦"
            "zm" -> "🇿🇲"
            "zw" -> "🇿🇼"
            else -> " "
        }
    }

    fun getLibraryMasterCountriesEnglish(): List<CountryModel>? {
        val countries: MutableList<CountryModel> = ArrayList<CountryModel>()
        countries.add(CountryModel("ad", "376", "Andorra", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ae", "971", "United Arab Emirates (UAE)", DEFAULT_FLAG_RES))
        countries.add(CountryModel("af", "93", "Afghanistan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ag", "1", "Antigua and Barbuda", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ai", "1", "Anguilla", DEFAULT_FLAG_RES))
        countries.add(CountryModel("al", "355", "Albania", DEFAULT_FLAG_RES))
        countries.add(CountryModel("am", "374", "Armenia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ao", "244", "Angola", DEFAULT_FLAG_RES))
        countries.add(CountryModel("aq", "672", "Antarctica", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ar", "54", "Argentina", DEFAULT_FLAG_RES))
        countries.add(CountryModel("as", "1", "American Samoa", DEFAULT_FLAG_RES))
        countries.add(CountryModel("at", "43", "Austria", DEFAULT_FLAG_RES))
        countries.add(CountryModel("au", "61", "Australia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("aw", "297", "Aruba", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ax", "358", "Åland Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("az", "994", "Azerbaijan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ba", "387", "Bosnia And Herzegovina", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bb", "1", "Barbados", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bd", "880", "Bangladesh", DEFAULT_FLAG_RES))
        countries.add(CountryModel("be", "32", "Belgium", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bf", "226", "Burkina Faso", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bg", "359", "Bulgaria", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bh", "973", "Bahrain", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bi", "257", "Burundi", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bj", "229", "Benin", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bl", "590", "Saint Barthélemy", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bm", "1", "Bermuda", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bn", "673", "Brunei Darussalam", DEFAULT_FLAG_RES))
        countries.add(
            CountryModel(
                "bo",
                "591",
                "Bolivia, Plurinational State Of",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(CountryModel("br", "55", "Brazil", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bs", "1", "Bahamas", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bt", "975", "Bhutan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bw", "267", "Botswana", DEFAULT_FLAG_RES))
        countries.add(CountryModel("by", "375", "Belarus", DEFAULT_FLAG_RES))
        countries.add(CountryModel("bz", "501", "Belize", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ca", "1", "Canada", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cc", "61", "Cocos (keeling) Islands", DEFAULT_FLAG_RES))
        countries.add(
            CountryModel(
                "cd",
                "243",
                "Congo, The Democratic Republic Of The",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(CountryModel("cf", "236", "Central African Republic", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cg", "242", "Congo", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ch", "41", "Switzerland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ci", "225", "Côte D'ivoire", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ck", "682", "Cook Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cl", "56", "Chile", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cm", "237", "Cameroon", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cn", "86", "China", DEFAULT_FLAG_RES))
        countries.add(CountryModel("co", "57", "Colombia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cr", "506", "Costa Rica", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cu", "53", "Cuba", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cv", "238", "Cape Verde", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cw", "599", "Curaçao", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cx", "61", "Christmas Island", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cy", "357", "Cyprus", DEFAULT_FLAG_RES))
        countries.add(CountryModel("cz", "420", "Czech Republic", DEFAULT_FLAG_RES))
        countries.add(CountryModel("de", "49", "Germany", DEFAULT_FLAG_RES))
        countries.add(CountryModel("dj", "253", "Djibouti", DEFAULT_FLAG_RES))
        countries.add(CountryModel("dk", "45", "Denmark", DEFAULT_FLAG_RES))
        countries.add(CountryModel("dm", "1", "Dominica", DEFAULT_FLAG_RES))
        countries.add(CountryModel("do", "1", "Dominican Republic", DEFAULT_FLAG_RES))
        countries.add(CountryModel("dz", "213", "Algeria", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ec", "593", "Ecuador", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ee", "372", "Estonia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("eg", "20", "Egypt", DEFAULT_FLAG_RES))
        countries.add(CountryModel("er", "291", "Eritrea", DEFAULT_FLAG_RES))
        countries.add(CountryModel("es", "34", "Spain", DEFAULT_FLAG_RES))
        countries.add(CountryModel("et", "251", "Ethiopia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("fi", "358", "Finland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("fj", "679", "Fiji", DEFAULT_FLAG_RES))
        countries.add(CountryModel("fk", "500", "Falkland Islands (malvinas)", DEFAULT_FLAG_RES))
        countries.add(
            CountryModel(
                "fm",
                "691",
                "Micronesia, Federated States Of",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(CountryModel("fo", "298", "Faroe Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("fr", "33", "France", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ga", "241", "Gabon", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gb", "44", "United Kingdom", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gd", "1", "Grenada", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ge", "995", "Georgia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gf", "594", "French Guyana", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gh", "233", "Ghana", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gi", "350", "Gibraltar", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gl", "299", "Greenland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gm", "220", "Gambia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gn", "224", "Guinea", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gp", "450", "Guadeloupe", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gq", "240", "Equatorial Guinea", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gr", "30", "Greece", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gt", "502", "Guatemala", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gu", "1", "Guam", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gw", "245", "Guinea-bissau", DEFAULT_FLAG_RES))
        countries.add(CountryModel("gy", "592", "Guyana", DEFAULT_FLAG_RES))
        countries.add(CountryModel("hk", "852", "Hong Kong", DEFAULT_FLAG_RES))
        countries.add(CountryModel("hn", "504", "Honduras", DEFAULT_FLAG_RES))
        countries.add(CountryModel("hr", "385", "Croatia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ht", "509", "Haiti", DEFAULT_FLAG_RES))
        countries.add(CountryModel("hu", "36", "Hungary", DEFAULT_FLAG_RES))
        countries.add(CountryModel("id", "62", "Indonesia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ie", "353", "Ireland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("il", "972", "Israel", DEFAULT_FLAG_RES))
        countries.add(CountryModel("im", "44", "Isle Of Man", DEFAULT_FLAG_RES))
        countries.add(CountryModel("is", "354", "Iceland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("in", "91", "India", DEFAULT_FLAG_RES))
        countries.add(CountryModel("io", "246", "British Indian Ocean Territory", DEFAULT_FLAG_RES))
        countries.add(CountryModel("iq", "964", "Iraq", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ir", "98", "Iran, Islamic Republic Of", DEFAULT_FLAG_RES))
        countries.add(CountryModel("it", "39", "Italy", DEFAULT_FLAG_RES))
        countries.add(CountryModel("je", "44", "Jersey ", DEFAULT_FLAG_RES))
        countries.add(CountryModel("jm", "1", "Jamaica", DEFAULT_FLAG_RES))
        countries.add(CountryModel("jo", "962", "Jordan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("jp", "81", "Japan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ke", "254", "Kenya", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kg", "996", "Kyrgyzstan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kh", "855", "Cambodia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ki", "686", "Kiribati", DEFAULT_FLAG_RES))
        countries.add(CountryModel("km", "269", "Comoros", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kn", "1", "Saint Kitts and Nevis", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kp", "850", "North Korea", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kr", "82", "South Korea", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kw", "965", "Kuwait", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ky", "1", "Cayman Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("kz", "7", "Kazakhstan", DEFAULT_FLAG_RES))
        countries.add(
            CountryModel(
                "la",
                "856",
                "Lao People's Democratic Republic",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(CountryModel("lb", "961", "Lebanon", DEFAULT_FLAG_RES))
        countries.add(CountryModel("lc", "1", "Saint Lucia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("li", "423", "Liechtenstein", DEFAULT_FLAG_RES))
        countries.add(CountryModel("lk", "94", "Sri Lanka", DEFAULT_FLAG_RES))
        countries.add(CountryModel("lr", "231", "Liberia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ls", "266", "Lesotho", DEFAULT_FLAG_RES))
        countries.add(CountryModel("lt", "370", "Lithuania", DEFAULT_FLAG_RES))
        countries.add(CountryModel("lu", "352", "Luxembourg", DEFAULT_FLAG_RES))
        countries.add(CountryModel("lv", "371", "Latvia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ly", "218", "Libya", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ma", "212", "Morocco", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mc", "377", "Monaco", DEFAULT_FLAG_RES))
        countries.add(CountryModel("md", "373", "Moldova, Republic Of", DEFAULT_FLAG_RES))
        countries.add(CountryModel("me", "382", "Montenegro", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mf", "590", "Saint Martin", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mg", "261", "Madagascar", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mh", "692", "Marshall Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mk", "389", "Macedonia (FYROM)", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ml", "223", "Mali", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mm", "95", "Myanmar", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mn", "976", "Mongolia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mo", "853", "Macau", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mp", "1", "Northern Mariana Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mq", "596", "Martinique", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mr", "222", "Mauritania", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ms", "1", "Montserrat", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mt", "356", "Malta", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mu", "230", "Mauritius", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mv", "960", "Maldives", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mw", "265", "Malawi", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mx", "52", "Mexico", DEFAULT_FLAG_RES))
        countries.add(CountryModel("my", "60", "Malaysia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("mz", "258", "Mozambique", DEFAULT_FLAG_RES))
        countries.add(CountryModel("na", "264", "Namibia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("nc", "687", "New Caledonia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ne", "227", "Niger", DEFAULT_FLAG_RES))
        countries.add(CountryModel("nf", "672", "Norfolk Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ng", "234", "Nigeria", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ni", "505", "Nicaragua", DEFAULT_FLAG_RES))
        countries.add(CountryModel("nl", "31", "Netherlands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("no", "47", "Norway", DEFAULT_FLAG_RES))
        countries.add(CountryModel("np", "977", "Nepal", DEFAULT_FLAG_RES))
        countries.add(CountryModel("nr", "674", "Nauru", DEFAULT_FLAG_RES))
        countries.add(CountryModel("nu", "683", "Niue", DEFAULT_FLAG_RES))
        countries.add(CountryModel("nz", "64", "New Zealand", DEFAULT_FLAG_RES))
        countries.add(CountryModel("om", "968", "Oman", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pa", "507", "Panama", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pe", "51", "Peru", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pf", "689", "French Polynesia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pg", "675", "Papua New Guinea", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ph", "63", "Philippines", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pk", "92", "Pakistan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pl", "48", "Poland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pm", "508", "Saint Pierre And Miquelon", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pn", "870", "Pitcairn Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pr", "1", "Puerto Rico", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ps", "970", "Palestine", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pt", "351", "Portugal", DEFAULT_FLAG_RES))
        countries.add(CountryModel("pw", "680", "Palau", DEFAULT_FLAG_RES))
        countries.add(CountryModel("py", "595", "Paraguay", DEFAULT_FLAG_RES))
        countries.add(CountryModel("qa", "974", "Qatar", DEFAULT_FLAG_RES))
        countries.add(CountryModel("re", "262", "Réunion", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ro", "40", "Romania", DEFAULT_FLAG_RES))
        countries.add(CountryModel("rs", "381", "Serbia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ru", "7", "Russian Federation", DEFAULT_FLAG_RES))
        countries.add(CountryModel("rw", "250", "Rwanda", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sa", "966", "Saudi Arabia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sb", "677", "Solomon Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sc", "248", "Seychelles", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sd", "249", "Sudan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("se", "46", "Sweden", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sg", "65", "Singapore", DEFAULT_FLAG_RES))
        countries.add(
            CountryModel(
                "sh",
                "290",
                "Saint Helena, Ascension And Tristan Da Cunha",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(CountryModel("si", "386", "Slovenia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sk", "421", "Slovakia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sl", "232", "Sierra Leone", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sm", "378", "San Marino", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sn", "221", "Senegal", DEFAULT_FLAG_RES))
        countries.add(CountryModel("so", "252", "Somalia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sr", "597", "Suriname", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ss", "211", "South Sudan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("st", "239", "Sao Tome And Principe", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sv", "503", "El Salvador", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sx", "1", "Sint Maarten", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sy", "963", "Syrian Arab Republic", DEFAULT_FLAG_RES))
        countries.add(CountryModel("sz", "268", "Swaziland", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tc", "1", "Turks and Caicos Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("td", "235", "Chad", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tg", "228", "Togo", DEFAULT_FLAG_RES))
        countries.add(CountryModel("th", "66", "Thailand", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tj", "992", "Tajikistan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tk", "690", "Tokelau", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tl", "670", "Timor-leste", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tm", "993", "Turkmenistan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tn", "216", "Tunisia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("to", "676", "Tonga", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tr", "90", "Turkey", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tt", "1", "Trinidad &amp; Tobago", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tv", "688", "Tuvalu", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tw", "886", "Taiwan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("tz", "255", "Tanzania, United Republic Of", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ua", "380", "Ukraine", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ug", "256", "Uganda", DEFAULT_FLAG_RES))
        countries.add(CountryModel("us", "1", "United States", DEFAULT_FLAG_RES))
        countries.add(CountryModel("uy", "598", "Uruguay", DEFAULT_FLAG_RES))
        countries.add(CountryModel("uz", "998", "Uzbekistan", DEFAULT_FLAG_RES))
        countries.add(CountryModel("va", "379", "Holy See (vatican City State)", DEFAULT_FLAG_RES))
        countries.add(
            CountryModel(
                "vc",
                "1",
                "Saint Vincent &amp; The Grenadines",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(
            CountryModel(
                "ve",
                "58",
                "Venezuela, Bolivarian Republic Of",
                DEFAULT_FLAG_RES
            )
        )
        countries.add(CountryModel("vg", "1", "British Virgin Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("vi", "1", "US Virgin Islands", DEFAULT_FLAG_RES))
        countries.add(CountryModel("vn", "84", "Vietnam", DEFAULT_FLAG_RES))
        countries.add(CountryModel("vu", "678", "Vanuatu", DEFAULT_FLAG_RES))
        countries.add(CountryModel("wf", "681", "Wallis And Futuna", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ws", "685", "Samoa", DEFAULT_FLAG_RES))
        countries.add(CountryModel("xk", "383", "Kosovo", DEFAULT_FLAG_RES))
        countries.add(CountryModel("ye", "967", "Yemen", DEFAULT_FLAG_RES))
        countries.add(CountryModel("yt", "262", "Mayotte", DEFAULT_FLAG_RES))
        countries.add(CountryModel("za", "27", "South Africa", DEFAULT_FLAG_RES))
        countries.add(CountryModel("zm", "260", "Zambia", DEFAULT_FLAG_RES))
        countries.add(CountryModel("zw", "263", "Zimbabwe", DEFAULT_FLAG_RES))
        return countries
    }
}