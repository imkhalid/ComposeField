package com.imkhalid.composefieldproject.composeField.fields

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.fields.ComposeCheckBoxField
import com.imkhalid.composefield.composeField.fields.ComposeCurrencyField
import com.imkhalid.composefield.composeField.fields.ComposeDatePickerField
import com.imkhalid.composefield.composeField.fields.ComposeDropDownField
import com.imkhalid.composefield.composeField.fields.ComposeMobileField
import com.imkhalid.composefield.composeField.fields.ComposeRadioGroupField
import com.imkhalid.composefield.composeField.fields.ComposeSwitchField
import com.imkhalid.composefield.composeField.fields.ComposeTimePickerField
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.rememberFieldState

abstract class ComposeField {
    var currentUserCountryCode = ""
    var focusCallback: ((isValidated: Boolean, fieldName: String) -> Unit)? = null
    @OptIn(ExperimentalFoundationApi::class)
    lateinit var localRequester:BringIntoViewRequester

    @Composable
    fun TrailingIcon(
        field: ComposeFieldModule,
        passwordVisible: Boolean,
        onClick: (() -> Unit)? = null
    ) {
        val keyboardType = (field.type as ComposeFieldType.TextBox).keyboardType
        if (keyboardType is ComposeKeyboardType.PASSWORD) {
            Image(
                painter =
                    if (passwordVisible) painterResource(id = R.drawable.ic_open_password)
                    else painterResource(id = R.drawable.ic_close_password),
                contentDescription = "Toggle password visibility",
                modifier = Modifier.clickable { onClick?.invoke() }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PreBuild(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit
    ) {
        // ✅ Common logic that must run before any child implementation
        localRequester = remember { BringIntoViewRequester() }

        // Call the abstract method which will be implemented by child classes
        Build(modifier, state, newValue)
        LaunchedEffect(state.hasError) {
            if (state.hasError) {
                localRequester.bringIntoView()
            }
        }
    }

    @Composable
    abstract fun Build(
        modifier: Modifier,
        state: ComposeFieldState,
        newValue: (Pair<Boolean, String>, String) -> Unit,
    )
}

class ComposeFieldBuilder {
    @Composable
    fun Build(
        modifier: Modifier = Modifier,
        userCountry:String="2",
        stateHolder: ComposeFieldStateHolder = rememberFieldState(name = "", label = ""),
        focusCallback: ((isValidated: Boolean, fieldName: String) -> Unit)? = null,
        onValueChangeForChild: ((value: String) -> Unit)? = null,
        onValueChange: ((name: String, value: String) -> Unit)? = null
    ) {
        val state = stateHolder.state
        val field =
            when (state.field.type) {
                is ComposeFieldType.TextBox-> ComposeTextField().setFocusCallback(focusCallback)
                is ComposeFieldType.MobileNo -> ComposeMobileField()
                is ComposeFieldType.Currency-> ComposeCurrencyField()
                is ComposeFieldType.Dropdown -> ComposeDropDownField()
                is ComposeFieldType.DatePicker -> ComposeDatePickerField()
                is ComposeFieldType.TimePicker -> ComposeTimePickerField()
                is ComposeFieldType.DateTimePicker -> ComposeDatePickerField()
                is ComposeFieldType.Switch -> ComposeSwitchField()
                is ComposeFieldType.CheckBox -> ComposeCheckBoxField()
                is ComposeFieldType.RadioButton -> ComposeRadioGroupField()
            }

        field.currentUserCountryCode = userCountry
        if (state.field.hidden == ComposeFieldYesNo.NO)
            field.PreBuild(
                state = state,
                newValue = { error, newVal ->
                    updateFieldState(error, newVal, stateHolder, onValueChangeForChild)
                    onValueChange?.invoke(state.field.name, newVal)
                },
                modifier = modifier
            )
    }

    fun updateFieldState(
        error: Pair<Boolean, String>,
        text: String,
        stateHolder: ComposeFieldStateHolder,
        onValueChangeForChild: ((value: String) -> Unit)?
    ) {
        stateHolder.updatedState(error, text, onValueChangeForChild)
    }
}
