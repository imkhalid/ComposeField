package com.imkhalid.composefield.composeField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.model.LoadingModel
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.model.DefaultValues


data class ComposeFieldState(
    val loader: LoadingModel = LoadingModel(),
    val field: ComposeFieldModule = ComposeFieldModule(),
    var text: String = "",
    var hasError: Boolean = false,
    var errorMessage: String = "",
) {
}


//
//@Composable
//fun rememberFieldState(
//    name:String,
//    label:String,
//    id: String= "",
//    type:ComposeFieldType = ComposeFieldType.TEXT_BOX,
//    keyboardType: ComposeKeyboardType = ComposeKeyboardType.TEXT,
//    value:String="",
//):ComposeFieldState{
//    return rememberSaveable(
//        inputs = arrayOf(
//            id,name, type, keyboardType, value, label
//        ),
//        saver = ComposeFieldState.Saver,
//    ){
//        ComposeFieldState(
//            field = ComposeFieldModule(
//                id,name, type, keyboardType, value, label
//            )
//        )
//    }
//}


@Composable
fun rememberFieldState(
    name: String,
    label: String,
    id: String = "",
    type: ComposeFieldType = ComposeFieldType.TEXT_BOX,
    keyboardType: ComposeKeyboardType = ComposeKeyboardType.TEXT,
    value: String = "",
    defaultValues:List<DefaultValues> = emptyList()
): ComposeFieldStateHolder {
    val initialField = ComposeFieldState(
        field = ComposeFieldModule(
            id = id,
            name = name,
            type = type,
            keyboardType = keyboardType,
            value = value,
            label = label,
            defaultValues = defaultValues
        )
    )
    return rememberSaveable(
        inputs = arrayOf(
            id, name, type, keyboardType, value, label,defaultValues
        ),
        saver = ComposeFieldStateHolder.Saver,
    ) {
        ComposeFieldStateHolder(initialField)
    }
}

class ComposeFieldStateHolder(initialField: ComposeFieldState) {
    var state by mutableStateOf(initialField)
        private set

    fun updateField(text: String) {
        state = state.copy(
            text = text
        )
    }
    fun updateValidation(pair: Pair<Boolean,String>){
        state = state.copy(
            hasError = pair.first.not(),
            errorMessage = pair.second
        )
    }

    fun updatedState(pair: Pair<Boolean, String>,text: String){
        updateField(text)
        updateValidation(pair)
    }

    companion object {
        internal val Saver: Saver<ComposeFieldStateHolder, Any> = listSaver(
            save = {
                listOf(
                    it.state.text,
                    it.state.field.id,
                    it.state.field.name,
                    it.state.field.type,
                    it.state.field.keyboardType,
                    it.state.field.value,
                    it.state.field.label,
                    it.state.field.defaultValues
                )
            },
            restore = {
                ComposeFieldStateHolder(
                    ComposeFieldState(
                        text = it[0] as String,
                        field = ComposeFieldModule(
                            it[1] as String,
                            it[2] as String,
                            it[3] as ComposeFieldType,
                            it[4] as ComposeKeyboardType,
                            it[5] as String,
                            it[6] as String,
                            defaultValues = it[7] as List<DefaultValues>
                        )

                    )
                )
            },
        )
    }
}