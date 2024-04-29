package com.imkhalid.composefield.composeField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.fields.ComposeCheckBoxField
import com.imkhalid.composefield.composeField.fields.ComposeDatePickerField
import com.imkhalid.composefield.composeField.fields.ComposeMobileField
import com.imkhalid.composefield.composeField.fields.ComposeRadioGroupField
import com.imkhalid.composefield.composeField.fields.ComposeSwitchField
import com.imkhalid.composefieldproject.composeField.fields.ComposeTextField
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ComposeFieldBuilder {

    private lateinit var _fieldState : MutableStateFlow<ComposeFieldState>
    lateinit var fieldState : StateFlow<ComposeFieldState>
    var callback:((isValidated:Boolean,fieldName:String)->Unit)?=null

    fun setFieldModule(composeFieldModule: MutableStateFlow<ComposeFieldState>) = apply {
        _fieldState= composeFieldModule
        fieldState = _fieldState.asStateFlow()
    }

    fun setFocusCallback(focusCallback:((isValidated:Boolean,fieldName:String)->Unit)?) = apply {
        callback = focusCallback
    }


    @Composable
    fun build(){
        val state by fieldState.collectAsState()
        when(state.field.type){
            ComposeFieldType.TEXT_BOX ,
            ComposeFieldType.TEXT_AREA -> {
                if (state.field.keyboardType== ComposeKeyboardType.MOBILE_NO){
                    ComposeMobileField()
                        .Build(state,newValue ={error,newVal->
                            _fieldState.update {
                                it.copy(
                                    text = newVal,
                                    hasError = error.first.not(),
                                    errorMessage = error.second
                                )
                            }
                        })
                }else {
                    ComposeTextField()
                        .setFocusCallback(callback)
                        .Build(state, newValue = { error, newVal ->
                            _fieldState.update {
                                it.copy(
                                    text = newVal,
                                    hasError = error.first.not(),
                                    errorMessage = error.second
                                )
                            }
                        })
                }
            }
            ComposeFieldType.DROP_DOWN -> ComposeSwitchField()
                .Build(state,newValue ={error,newVal->
                    _fieldState.update {
                        it.copy(
                            text = newVal,
                            hasError = error.first.not(),
                            errorMessage = error.second
                        )
                    }
                })
            ComposeFieldType.DATE_PICKER -> ComposeDatePickerField()
                .Build(state,newValue ={error,newVal->
                    _fieldState.update {
                        it.copy(
                            text = newVal,
                            hasError = error.first.not(),
                            errorMessage = error.second
                        )
                    }
                })
            ComposeFieldType.DATE_TIME_PICKER -> TODO()
            ComposeFieldType.SWITCH -> TODO()
            ComposeFieldType.CHECK_BOX -> ComposeCheckBoxField()
                .Build(state,newValue ={error,newVal->
                    _fieldState.update {
                        it.copy(
                            text = newVal,
                            hasError = error.first.not(),
                            errorMessage = error.second
                        )
                    }
                })
            ComposeFieldType.RADIO_BUTTON -> ComposeRadioGroupField()
                .Build(state,newValue ={error,newVal->
                    _fieldState.update {
                        it.copy(
                            text = newVal,
                            hasError = error.first.not(),
                            errorMessage = error.second
                        )
                    }
                })
        }
    }


}