package com.imkhalid.composefield.composeField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.model.DefaultValues
import com.imkhalid.composefield.model.LoadingModel

data class ComposeFieldState(
    val loader: LoadingModel = LoadingModel(),
    val field: ComposeFieldModule = ComposeFieldModule(),
    var text: String = "",
    var hasError: Boolean = false,
    var errorMessage: String = "",
) {}


@Composable
fun rememberFieldState(
    name: String,
    label: String,
    id: String = "",
    type: ComposeFieldType = ComposeFieldType.TextBox(keyboardType = ComposeKeyboardType.TEXT),
    keyboardType: ComposeKeyboardType = ComposeKeyboardType.TEXT,
    value: String = "",
    defaultValues: List<DefaultValues> = emptyList()
): ComposeFieldStateHolder {
    val initialField =
        ComposeFieldState(
            text = value,
            field =
            ComposeFieldModule(
                id = id,
                name = name,
                type = type,
                value = value,
                label = label,
                defaultValues = defaultValues
            )
        )
    return rememberSaveable(
        inputs = arrayOf(id, name, type, keyboardType, value, label, defaultValues),
        saver = ComposeFieldStateHolder.Saver,
    ) {
        ComposeFieldStateHolder(initialField)
    }
}

@Composable
fun rememberFieldState(
    fieldModule: ComposeFieldModule,
    stateHolder: ComposeFieldStateHolder? = null
): ComposeFieldStateHolder {
    val initialField =
        ComposeFieldState(
            field = stateHolder?.state?.field ?: fieldModule,
            text =
                stateHolder?.state?.text ?: fieldModule.value.takeIf { x -> x.isNotEmpty() } ?: ""
        )
    val value = stateHolder?.state?.text ?: fieldModule.value
    return rememberSaveable(
        inputs =
            arrayOf(
                fieldModule.id,
                fieldModule.name,
                fieldModule.type,
                value,
                fieldModule.label,
                fieldModule.hint,
                fieldModule.required,
                fieldModule.description,
                fieldModule.defaultValues,
                fieldModule.childID,
                fieldModule.minValue,
                fieldModule.maxValue,
                fieldModule.sortNumber,
                fieldModule.sectionSortNumber,
                stateHolder?.state?.field?.isEditable ?: fieldModule.isEditable,
                fieldModule.useIDValue,
                fieldModule.hideInitial,
                fieldModule.autoFocusable,
                fieldModule.pattern,
                fieldModule.patternMessage,
                stateHolder?.state?.field?.hidden ?: fieldModule.hidden,
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
        state = state.copy(text = text)
    }

    fun updatedField(field: ComposeFieldModule) {
        state = state.copy(field = field)
    }

    fun updatedFieldDefaultValues(list: List<DefaultValues>) {
        state = state.copy(field = state.field.copy(defaultValues = list))
    }

    fun updateValidation(pair: Pair<Boolean, String>) {
        state = state.copy(hasError = pair.first.not(), errorMessage = pair.second)
    }

    fun updatedState(pair: Pair<Boolean, String>, text: String) {
        updateField(text)
        updateValidation(pair)
    }

    fun updatedState(
        pair: Pair<Boolean, String>,
        text: String,
        onValueChangeForChild: ((value: String) -> Unit)?
    ) {
        updateField(text)
        updateValidation(pair)
        if (state.field.childID != "-1") {
            onValueChangeForChild?.invoke(text)
        }
    }

    companion object {
        internal val Saver: Saver<ComposeFieldStateHolder, Any> =
            listSaver(
                save = {
                    listOf(
                        it.state.text,
                        it.state.field.id,
                        it.state.field.name,
                        it.state.field.type,
                        it.state.field.value,
                        it.state.field.label,
                        it.state.field.defaultValues,
                        it.state.field.hint,
                        it.state.field.required,
                        it.state.field.description,
                        it.state.field.childID,
                        it.state.field.minValue,
                        it.state.field.maxValue,
                        it.state.field.sortNumber,
                        it.state.field.sectionSortNumber,
                        it.state.field.isEditable,
                        it.state.field.useIDValue,
                        it.state.field.hideInitial,
                        it.state.field.autoFocusable,
                        it.state.field.pattern,
                        it.state.field.patternMessage,
                        it.state.field.hidden,
                    )
                },
                restore = {
                    ComposeFieldStateHolder(
                        ComposeFieldState(
                            text = it[0] as String,
                            field =
                                ComposeFieldModule(
                                    it[1] as String,
                                    it[2] as String,
                                    it[3] as ComposeFieldType,
                                    it[5] as String,
                                    it[6] as String,
                                    defaultValues = it[7] as List<DefaultValues>,
                                    hint = it[8] as String,
                                    required = it[9] as ComposeFieldYesNo,
                                    description = it[10] as String,
                                    childID = it[11] as String,
                                    minValue = it[12] as? String,
                                    maxValue = it[13] as? String,
                                    sortNumber = it[14] as Int,
                                    sectionSortNumber = it[15] as Int,
                                    isEditable = it[16] as ComposeFieldYesNo,
                                    useIDValue = it[17] as ComposeFieldYesNo,
                                    hideInitial = it[18] as ComposeFieldYesNo,
                                    autoFocusable = it[19] as ComposeFieldYesNo,
                                    pattern = it[20] as String,
                                    patternMessage = it[21] as String,
                                    hidden = it[22] as ComposeFieldYesNo
                                )
                        )
                    )
                },
            )
    }
}
