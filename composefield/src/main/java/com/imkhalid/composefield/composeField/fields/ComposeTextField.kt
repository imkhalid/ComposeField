package com.imkhalid.composefieldproject.composeField.fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.FieldMaskTransformation
import com.imkhalid.composefield.composeField.Patterns
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import io.michaelrocks.libphonenumber.android.CountryCodeToRegionCodeMap
import java.util.regex.Pattern

class ComposeTextField : ComposeField() {


    fun setFocusCallback(callback:((isValidated:Boolean,fieldName:String)->Unit)?)= apply{
        focusCallback =callback
    }


    @Composable
    fun Build(state: ComposeFieldState, newValue: (Pair<Boolean,String>, String) -> Unit) {
        val mask = getFieldMask(state.field)
        Column {
            OutlinedTextField(
                value = state.text,
                enabled = state.field.isEditable.value,
                onValueChange = {curVal->
                    if (mask!=Patterns.NONE && mask.value.isNotEmpty()){
                        if (curVal.length<=mask.length){
                            builtinValidations(curVal,state){validated,newVal->
                                newValue.invoke(validated,newVal)
                            }
                        }
                    }else{
                        builtinValidations(curVal,state){validated,newVal->
                            newValue.invoke(validated,newVal)
                        }
                    }

                },
                prefix = {
                    if (state.field.keyboardType== ComposeKeyboardType.MOBILE_NO)
                        Text(text = "+1", modifier = Modifier.clickable {

                        })
                    else null
                },
                keyboardOptions =getKeyboardOptions(state.field),
                isError = state.hasError,
                label = { Text(state.field.label) },
                minLines = getMinLine(state.field.type),
                maxLines = getMaxLine(state.field.type),
                visualTransformation = if (mask!= Patterns.MOBILE && mask!=Patterns.NONE &&mask.value.isNotEmpty())
                    FieldMaskTransformation(mask.value)
                else
                VisualTransformation.None
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
    }

    private fun openCountryPicker(any: Any) {
        CountryCodeToRegionCodeMap.getCountryCodeToRegionCodeMap()
    }

    private fun getMinLine(type: ComposeFieldType): Int {
        return when(type){
            ComposeFieldType.TEXT_BOX -> 1
            ComposeFieldType.TEXT_AREA -> 3
            else -> 1
        }
    }

    private fun getMaxLine(type: ComposeFieldType): Int {
        return when(type){
            ComposeFieldType.TEXT_BOX -> 1
            ComposeFieldType.TEXT_AREA -> 3
            else -> 1
        }
    }

    private fun getKeyboardOptions(fieldState: ComposeFieldModule):KeyboardOptions{
        val type = when(fieldState.keyboardType){
            ComposeKeyboardType.CNIC ,
            ComposeKeyboardType.MOBILE_NO,
            ComposeKeyboardType.NUMBER -> KeyboardType.Number
            ComposeKeyboardType.EMAIL -> KeyboardType.Email
            ComposeKeyboardType.TEXT ,
            ComposeKeyboardType.NONE -> KeyboardType.Text
        }
        return KeyboardOptions(
            keyboardType = type,
            autoCorrect = false,
            imeAction = ImeAction.Next
        )
    }

    private fun builtinValidations(curVal:String,state:ComposeFieldState,newValue: (Pair<Boolean,String>,String) -> Unit){
        /*we will be using curVal for getValueWithMask and on final callback-> newValue
        * operations will be performed on value collected from getValueWithMask method*/

        var bool = true
        var message = ""
        var valueToBeUsed =getValueWithMask(curVal,state.field)
        when(state.field.keyboardType){
            ComposeKeyboardType.CNIC-> {
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
            ComposeKeyboardType.EMAIL->{
                val pattern = Patterns.EMAIL.pattern.toList()
                if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                    bool = false
                    message = "Please enter valid Email Address"
                }
            }
            ComposeKeyboardType.TEXT->{
                if (state.field.name.contains("email",true)) {
                    val pattern = Patterns.EMAIL.pattern.toList()
                    if (!Pattern.matches(pattern.first(), valueToBeUsed)) {
                        bool = false
                        message = "Please enter valid Email Address"
                    }
                }
            }
            else->{

            }
        }
        newValue.invoke(Pair(bool , message),curVal)
    }

    private fun getFieldMask(module:ComposeFieldModule):Patterns{
        val keyboardType = module.keyboardType

        return when(keyboardType){
            ComposeKeyboardType.CNIC -> Patterns.CNIC
            ComposeKeyboardType.MOBILE_NO ->
                Patterns.MOBILE.apply {
                    value = ""
                    pattern = arrayOf("")
                }
            ComposeKeyboardType.EMAIL->Patterns.EMAIL
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
            ComposeKeyboardType.TEXT ,
            ComposeKeyboardType.NONE -> Patterns.NONE
        }
    }

    private fun getValueWithMask(currValue:String,field:ComposeFieldModule):String{
        val mask = when(field.keyboardType){
            ComposeKeyboardType.CNIC -> Patterns.CNIC.value
            ComposeKeyboardType.MOBILE_NO -> Patterns.MOBILE.value
            ComposeKeyboardType.EMAIL -> Patterns.EMAIL.value
            ComposeKeyboardType.TEXT,
            ComposeKeyboardType.NUMBER,
            ComposeKeyboardType.NONE -> Patterns.NONE.value
        }

        val transforation = FieldMaskTransformation(mask)
        return if (Patterns.NONE.value==mask){
            currValue
        }else
            transforation.applyMaskAndGetResult(currValue)
    }


}