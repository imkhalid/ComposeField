package com.imkhalid.composefield.composeField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.fields.ComposeCheckBoxField
import com.imkhalid.composefield.composeField.fields.ComposeDatePickerField
import com.imkhalid.composefield.composeField.fields.ComposeDropDownField
import com.imkhalid.composefield.composeField.fields.ComposeMobileField
import com.imkhalid.composefield.composeField.fields.ComposeRadioGroupField
import com.imkhalid.composefield.composeField.fields.ComposeTimePickerField
import com.imkhalid.composefieldproject.composeField.fields.ComposeTextField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ComposeFieldBuilder {

    private lateinit var _fieldState: MutableStateFlow<ComposeFieldState>
    lateinit var fieldState: StateFlow<ComposeFieldState>
    var callback: ((isValidated: Boolean, fieldName: String) -> Unit)? = null

    fun setFieldModule(composeFieldModule: MutableStateFlow<ComposeFieldState>) = apply {
        _fieldState = composeFieldModule
        fieldState = _fieldState.asStateFlow()
    }

    fun setFocusCallback(focusCallback: ((isValidated: Boolean, fieldName: String) -> Unit)?) =
        apply {
            callback = focusCallback
        }

    @Composable
    fun build(modifier: Modifier = Modifier) {
        val state by fieldState.collectAsState()
        val field =
            when (state.field.type) {
                ComposeFieldType.TEXT_BOX,
                ComposeFieldType.TEXT_AREA -> {
                    if (state.field.keyboardType is ComposeKeyboardTypeAdv.MOBILE_NO) {
                        ComposeMobileField()
                    } else {
                        ComposeTextField().setFocusCallback(callback)
                    }
                }
                ComposeFieldType.DROP_DOWN -> ComposeDropDownField()
                ComposeFieldType.DATE_PICKER -> ComposeDatePickerField()
                ComposeFieldType.TIME_PICKER -> ComposeTimePickerField()
                ComposeFieldType.DATE_TIME_PICKER -> TODO()
                ComposeFieldType.SWITCH -> TODO()
                ComposeFieldType.CHECK_BOX -> ComposeCheckBoxField()
                ComposeFieldType.RADIO_BUTTON -> ComposeRadioGroupField()
            }
        field.Build(
            state = state,
            newValue = { error, newVal ->
                _fieldState.update {
                    it.copy(
                        text = newVal,
                        hasError = error.first.not(),
                        errorMessage = error.second
                    )
                }
            },
            modifier = modifier
        )
    }
}
