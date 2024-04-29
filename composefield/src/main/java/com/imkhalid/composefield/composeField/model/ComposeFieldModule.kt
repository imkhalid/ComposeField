package com.techInfo.composefieldproject.composeField

import com.techInfo.composefieldproject.composeField.fieldTypes.ComposeFieldType
import com.techInfo.composefieldproject.composeField.fieldTypes.ComposeFieldYesNo
import com.techInfo.composefieldproject.composeField.fieldTypes.ComposeKeyboardType
import com.techInfo.composefieldproject.model.CustomFields
import com.techInfo.composefieldproject.model.DefaultValues

/**
 * This Class is Used for parsing Server Field to Compose Field
 * */

data class ComposeFieldModule(
    val id: String = "",
    val name: String = "",
    val type: ComposeFieldType = ComposeFieldType.TEXT_BOX,
    val keyboardType: ComposeKeyboardType = ComposeKeyboardType.TEXT,
    val value: String = "",
    val label: String = "",
    val hint: String = "",
    val required: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val description: String = "",
    val defaultValues: List<DefaultValues> = listOf(),
    val childID: String = "0",
    val minValue: String? = "",
    val maxValue: String? = "",
    val sortNumber: Int = 0,
    val sectionSortNumber: Int = 0,
    val isEditable: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val display: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val useIDValue: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val hideInitial: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val autoFocusable: ComposeFieldYesNo = ComposeFieldYesNo.YES,
) {

    fun parseCustomField(
        customField: CustomFields,
        sortNumber: Int = 0,
    ): ComposeFieldModule {
        val selected_value = customField.selectedValue
        val isEmailMobile =
            (customField.field_name == "email" || customField.field_name == "mobile_no")
        val showIcon = customField.label.contains("date", true) || customField.field_name.contains(
            "cnic",
            true
        )
        return ComposeFieldModule(
            id = customField.id.toString(),
            name = customField.field_name,
            childID = customField.child_id.toString(),
            type = customField.type.fieldType(),
            keyboardType = customField.inputType.keyboardType(),
            value = getInitialValue(customField,selected_value),
            label = customField.label,
            hint = customField.field_hint ?: "",
            description = customField.description ?: "",
            defaultValues = customField.default_values,
            required = customField.required.toString().CHOICE(),
            minValue = getMinValue(customField,customField.type.fieldType()),
            maxValue = getMaxValue(customField,customField.type.fieldType()),
            sortNumber = customField.field_sort_number,
            sectionSortNumber = sortNumber,
            display = customField.visible?.toString()?.CHOICE()?:ComposeFieldYesNo.YES,
            hideInitial = customField.visible.toString()?.CHOICE()?:ComposeFieldYesNo.YES
        )
    }

    private fun getInitialValue(customField: CustomFields, selectedValue: String): String {
        return if (selectedValue.isNotEmpty())
            selectedValue
        else if (customField.type.fieldType()==ComposeFieldType.DROP_DOWN){
            val falseValue = customField.default_values.find { x->x.text.contains("no",true) ||
                    x.text.contains("false",true) ||
                    x.text.contains("female",true)
            }?.id?:""
            falseValue
        }else{
            selectedValue
        }
    }

    fun getMinValue(customField: CustomFields,type: ComposeFieldType):String{
        return when(type){
            ComposeFieldType.TEXT_BOX ,
            ComposeFieldType.TEXT_AREA -> customField.min_rule
            ComposeFieldType.DATE_PICKER ,
            ComposeFieldType.DATE_TIME_PICKER -> customField.min_date
            ComposeFieldType.DROP_DOWN ,
            ComposeFieldType.SWITCH,
            ComposeFieldType.CHECK_BOX,
            ComposeFieldType.RADIO_BUTTON -> ""
        }
    }
    fun getMaxValue(customField: CustomFields,type: ComposeFieldType):String{
        return when(type){
            ComposeFieldType.TEXT_BOX -> customField.max_rule
            ComposeFieldType.TEXT_AREA -> customField.max_rule
            ComposeFieldType.DATE_PICKER -> customField.max_date
            ComposeFieldType.DATE_TIME_PICKER -> customField.max_date
            ComposeFieldType.DROP_DOWN ,
            ComposeFieldType.SWITCH,
            ComposeFieldType.CHECK_BOX,
            ComposeFieldType.RADIO_BUTTON -> ""
        }

    }
}

fun String.CHOICE(): ComposeFieldYesNo {
    return if (this.equals("0", true))
        ComposeFieldYesNo.NO
    else
        ComposeFieldYesNo.YES
}

fun String.fieldType(): ComposeFieldType {
    return when (this.lowercase()) {
        "textbox" -> ComposeFieldType.TEXT_BOX
        "textarea" -> ComposeFieldType.TEXT_AREA
        "dropdown" -> ComposeFieldType.DROP_DOWN
        "date" -> ComposeFieldType.DATE_PICKER
        "date_time" -> ComposeFieldType.DATE_TIME_PICKER
        "radiobutton" -> ComposeFieldType.RADIO_BUTTON
        "switch" -> ComposeFieldType.SWITCH
        else -> ComposeFieldType.CHECK_BOX
    }
}

fun String.keyboardType(): ComposeKeyboardType {
    return when (this.lowercase()) {
        "text" -> ComposeKeyboardType.TEXT
        "cnic" -> ComposeKeyboardType.CNIC
        "email" -> ComposeKeyboardType.EMAIL
        "mobile" ,
        "mobile_number" -> ComposeKeyboardType.MOBILE_NO
        "number" -> ComposeKeyboardType.NUMBER
        else -> ComposeKeyboardType.NONE
    }
}