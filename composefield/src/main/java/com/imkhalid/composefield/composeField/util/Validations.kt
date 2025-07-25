package com.imkhalid.composefield.composeField.util

import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.PhoneNumberUtil
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.section.Sections
import com.imkhalid.composefield.model.DefaultValues
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar

/**isMatchingAny is Used to check Contains with Ignore case true,
 *  but it can take as much matching conditions*/
internal fun String.isMatchingAny(vararg with:String) =
    with.any { this.contains(it,true) }


internal fun updateDependantChildren(
    fields: HashMap<String, List<ComposeFieldStateHolder>>,
    stateHolder: ComposeFieldStateHolder,
    newValue:String
){
    if (
        stateHolder.state.field.type == ComposeFieldType.DROP_DOWN ||
        stateHolder.state.field.type == ComposeFieldType.RADIO_BUTTON ||
        stateHolder.state.field.type == ComposeFieldType.SWITCH
    ) {
        val currentChildName =
            stateHolder.state.field.defaultValues.find { x -> x.id == newValue }?.dependent_child_fields?.map {
                it.field_name
            } ?: emptyList()
        val allChild = stateHolder.state.field.defaultValues.flatMap {
            it.dependent_child_fields?.map { it.field_name } ?: emptyList()
        }
        allChild.forEach {
            getFieldByFieldName(it, fields)?.let {
                if (currentChildName.contains(it.state.field.name)) {
                    it.updatedField(
                        pair = true to "",
                        fieldModule = it.state.field.copy(
                            required = ComposeFieldYesNo.YES,
                            hidden = ComposeFieldYesNo.NO,
                            hideInitial = ComposeFieldYesNo.NO
                        )
                    )
                } else {
                    it.updatedField(
                        pair = true to "",
                        fieldModule = it.state.field.copy(
                            required = ComposeFieldYesNo.NO,
                            hidden = ComposeFieldYesNo.YES,
                            hideInitial = ComposeFieldYesNo.YES
                        )
                    )
                }
            }
        }
    } else if (
        stateHolder.state.field.defaultValues.size == 1 &&
        stateHolder.state.field.defaultValues.first().dependent_child_fields.isNullOrEmpty().not()
    ) {
        val defVal = stateHolder.state.field.defaultValues.first()
        val checkValue = if (stateHolder.state.field.type == ComposeFieldType.DATE_PICKER) {
            val year = (stateHolder.state.text.split("-").getOrNull(0) ?: "")
            stateHolder.state.text.getAgeFromDOBYear(year.toIntOrNull() ?: 0)
        } else {
            stateHolder.state.text
        }
        val isValueMatch = ExpressionEvaluator.evaluateCondition(defVal.text, checkValue)
        val currentChildName = defVal.dependent_child_fields?.map {
            it.field_name
        } ?: emptyList()
        currentChildName.forEach {
            getFieldByFieldName(it, fields)?.let {
                if (isValueMatch) {
                    it.updatedField(
                        it.state.field.copy(
                            required = ComposeFieldYesNo.YES,
                            hidden = ComposeFieldYesNo.NO,
                            hideInitial = ComposeFieldYesNo.NO
                        )
                    )

                } else {
                    it.updatedState(Pair(true, ""), "")
                    it.updatedField(
                        it.state.field.copy(
                            required = ComposeFieldYesNo.NO,
                            hidden = ComposeFieldYesNo.YES,
                            hideInitial = ComposeFieldYesNo.YES
                        )
                    )
                }
            }

        }

    }
}

internal fun updatedChildValues(
    childID: String,
    newValues: List<DefaultValues>,
    sectionState: List<ComposeFieldStateHolder>
) {
    val childs = childID.split(",")
    childs.forEach {
        sectionState
            .find { x -> x.state.field.id == it }
            ?.let { it.updatedFieldDefaultValues(newValues) }
    }
}

internal fun validatedSection(sectionState: List<ComposeFieldStateHolder>): Boolean {
    return sectionState.all { x ->
        x.state.field.required == ComposeFieldYesNo.YES &&
                x.state.text.trim().isNotEmpty() &&
                x.state.hasError.not()
    }
}


internal fun invalidFamily(familyData: FamilyData?): List<Int> {
    return familyData?.snapshotStateList?.mapIndexedNotNull { index, map ->
        if (map.getOrDefault("isValidated", "") != "1")
            index
        else null

    }.orEmpty()
}


fun getFieldByFieldName(
    name: String,
    state: HashMap<String, List<ComposeFieldStateHolder>>
): ComposeFieldStateHolder? {
    for (i in 0 until state.entries.size) {
        val foundState =
            state.entries.toList().get(i).value.find { x -> x.state.field.name == name }
        if (foundState != null) {
            return foundState
        }
    }
    return null
}

fun getFieldByFieldNameStartsWith(
    name: String,
    state: HashMap<String, List<ComposeFieldStateHolder>>
): ComposeFieldStateHolder? {
    for (i in 0 until state.entries.size) {
        val foundState =
            state.entries.toList()[i].value.find { x -> x.state.field.name.startsWith(name) }
        if (foundState != null) {
            return foundState
        }
    }
    return null
}


fun Sections.getCurrentSection(): List<ComposeFieldStateHolder>? {
    return sectionState[nav.currentDestination?.route.orEmpty()]
}

fun Sections.getCurrentSectionName(): String = nav.currentDestination?.route.orEmpty()

fun List<ComposeFieldStateHolder>.getFieldByFieldName(name: String): ComposeFieldStateHolder? {
    for (i in 0 until this.size) {
        if (this[i].state.field.name == name) return this[i]
    }
    return null
}


fun String.getAgeFromDOBYear(year: Int): String {
    var age = ""
    try {
        this.takeIf { it.isNotEmpty() }?.let {
            val today = Calendar.getInstance()
            age = "${today.get(Calendar.YEAR) - year}"
        }
    } catch (e: Exception) {
        age = ""
    }
    return age
}


fun ArrayList<MutableStateFlow<ComposeFieldState>>.validateSection(): Boolean {
    return this.all { x ->
        val state = x.value
        (state.field.required == ComposeFieldYesNo.YES &&
                (state.text.trim().isNotEmpty() && state.hasError.not())) ||
                (state.field.required == ComposeFieldYesNo.NO && state.hasError.not())
    }
}

fun java.util.HashMap<String, List<ComposeFieldStateHolder>>.validate(showError: Boolean = false): Boolean {
    val res = this.all { x ->
        x.value.all {
            it.state.field.required == ComposeFieldYesNo.YES &&
                    (it.state.text.trim().isNotEmpty() && it.state.hasError.not()) ||
                    (it.state.field.required == ComposeFieldYesNo.NO && it.state.hasError.not())
        }
    }
    if (res.not() && showError) {
        this.flatMap {
            it.value
        }.firstOrNull {
            it.state.field.required == ComposeFieldYesNo.YES &&
                    (it.state.text.trim().isEmpty() || it.state.hasError)
        }?.let { err ->
            val message =
                err.state.errorMessage.ifEmpty {
                    "Required Field"
                }
            err.updateValidation(Pair(false, message))
        }
    }
    return res
}

fun List<ComposeFieldStateHolder>.validate(showError: Boolean = false): Boolean {
    val res =
        this.all {
            it.state.field.required == ComposeFieldYesNo.YES &&
                    (it.state.text.trim().isNotEmpty() && it.state.hasError.not()) ||
                    (it.state.field.required == ComposeFieldYesNo.NO && it.state.hasError.not())
        }
    if (res.not() && showError) {
        this.firstOrNull {
            it.state.field.required == ComposeFieldYesNo.YES &&
                    (it.state.text.trim().isEmpty() || it.state.hasError)
        }
            ?.let { err ->
                val message =
                    err.state.errorMessage.ifEmpty {
                        "Required Field"
                    }
                err.updateValidation(Pair(false, message))
            }
    }

    return res
}

fun getFieldByFieldId(
    id: String,
    state: HashMap<String, List<ComposeFieldStateHolder>>
): ComposeFieldStateHolder? {
    for (i in 0 until state.entries.size) {
        val foundState =
            state.entries.toList().get(i).value.find { x -> x.state.field.id == id }
        if (foundState != null) {
            return foundState
        }
    }
    return null
}

fun List<ComposeFieldStateHolder>.getFieldByFieldId(id: String): ComposeFieldStateHolder? {
    for (i in 0 until this.size) {
        if (this[i].state.field.id == id) return this[i]
    }
    return null
}

fun String.getPhoneNumber(): PhoneNumberUtil.PhoneNumber? {
    return try {
        val phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance()
        val number = phoneUtil.parse(
            this,
            null
        )
        var  emoji = ""
        PhoneNumberUtil.getLibraryMasterCountriesEnglish()?.find { x->x.code==phoneUtil.getRegionCodeForNumber(number) }?.let {
            emoji = it.emoji
            
        }
        PhoneNumberUtil.PhoneNumber(
            "+${number.countryCode}",
            replace("+${number.countryCode}", "",),
            emoji
        )
    } catch (e: Exception) {
        null
    }
}
