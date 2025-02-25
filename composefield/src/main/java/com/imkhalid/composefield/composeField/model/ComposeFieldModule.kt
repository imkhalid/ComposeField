package com.imkhalid.composefield.composeField.model

import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.util.isMatchingAny
import com.imkhalid.composefield.model.CustomFields
import com.imkhalid.composefield.model.DefaultValues

/** This Class is Used for parsing Server Field to Compose Field */
data class ComposeFieldModule(
    val id: String = "",
    val name: String = "",
    val type: ComposeFieldType = ComposeFieldType.TextBox(keyboardType = ComposeKeyboardType.TEXT),
    val value: String = "",
    val label: String = "",
    val hint: String = "",
    val required: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val description: String = "",
    var defaultValues: List<DefaultValues> = listOf(),
    val childID: String = "-1",
    val minValue: String? = "",
    val maxValue: String? = "",
    val sortNumber: Int = 0,
    val sectionSortNumber: Int = 0,
    val isEditable: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val useIDValue: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val hideInitial: ComposeFieldYesNo = ComposeFieldYesNo.NO,
    val autoFocusable: ComposeFieldYesNo = ComposeFieldYesNo.YES,
    val pattern: String = "",
    val patternMessage: String = "",
    val hidden: ComposeFieldYesNo =
        if (hideInitial == ComposeFieldYesNo.YES) ComposeFieldYesNo.YES else ComposeFieldYesNo.NO,
    val helperText: String = "",
    var visualTransformation:String = "",
    val parent_field_value_id:String?=null,
) {

    fun parseCustomField(
        customField: CustomFields,
        sortNumber: Int = 0,
    ): ComposeFieldModule {
        val selected_value = customField.selectedValue?.takeIf { x->x.isNotEmpty() }?:customField.selected_value.orEmpty()
        val isEmailMobile =
            (customField.field_name == "email" || customField.field_name == "mobile_no")
        val showIcon =
            customField.label.contains("date", true) ||
                customField.field_name.contains("cnic", true)
        return ComposeFieldModule(
            id = customField.id.toString(),
            parent_field_value_id=customField.parent_field_value_id,
            name = customField.field_name,
            childID = customField.child_id.toString(),
            type = customField.type.fieldType(customField.inputType,customField.field_name, customField.field_hint),
            value = getInitialValue(customField, selected_value),
            label = customField.label,
            hint = customField.field_hint ?: "",
            description = customField.description ?: "",
            defaultValues = customField.default_values,
            required = customField.required.toString().CHOICE(),
            minValue = getMinValue(customField, customField.type.fieldType(customField.inputType,customField.field_name,customField.field_hint)),
            maxValue = getMaxValue(customField, customField.type.fieldType(customField.inputType,customField.field_name,customField.field_hint)),
            sortNumber = customField.field_sort_number,
            sectionSortNumber = sortNumber,
            hidden = getHiddenValue(
                customField.visible,
                customField.parent_field_value_id,
                customField.selected_value
            ),
            hideInitial = getHiddenValue(
                customField.visible,
                customField.parent_field_value_id,
                customField.selected_value
            ),
            pattern = customField.regex.orEmpty(),
            helperText = getHelperText(customField.field_hint,customField.field_name),
            patternMessage = if (customField.regex.isNullOrEmpty().not()) customField.field_hint.orEmpty() else ""
        )
    }

    private fun getHelperText(hint: String?,name:String):String{
        return if (hint.isNullOrEmpty().not())
            hint?:""
        else if(name.contains("dob",true)|| name.contains("date_of_birth",true)){
            "DOB should be same as National ID Card"
        }else
            ""
    }

    private fun getHiddenValue(field: Int,parentFieldValueId:String?,selectedValue: String?): ComposeFieldYesNo {
        //parent id null or empty means it should not hide,
        //checking selected value if its prefilled than it should not hide
        // field == 1 means it should not hide.
        return if ((parentFieldValueId.isNullOrEmpty().not() && selectedValue.isNullOrEmpty()) || field==0)
            ComposeFieldYesNo.YES
        else
            ComposeFieldYesNo.NO
    }

    private fun getInitialValue(customField: CustomFields, selectedValue: String): String {
        return if (selectedValue.isNotEmpty()) selectedValue
        else if (customField.type.fieldType(customField.inputType,customField.field_name,customField.field_hint) == ComposeFieldType.Switch) {
            val falseValue =
                customField.default_values
                    .find { x ->
                        x.text.contains("no", true) ||
                            x.text.contains("false", true) ||
                            x.text.contains("female", true)
                    }
                    ?.id ?: ""
            falseValue
        } else {
            selectedValue
        }
    }

    fun getMinValue(customField: CustomFields, type: ComposeFieldType): String {
        return when (type) {
            is ComposeFieldType.TextBox -> customField.min_rule
            is ComposeFieldType.DatePicker,
            is ComposeFieldType.TimePicker,
            is ComposeFieldType.DateTimePicker -> customField.min_date
            is ComposeFieldType.Dropdown,
            is ComposeFieldType.Switch,
            is ComposeFieldType.CheckBox,
            is ComposeFieldType.RadioButton,
            is ComposeFieldType.Currency,
            is ComposeFieldType.MobileNo -> ""
        }
    }

    fun getMaxValue(customField: CustomFields, type: ComposeFieldType): String {
        return when (type) {
            is ComposeFieldType.TextBox -> customField.max_rule
            is ComposeFieldType.DatePicker,
            is ComposeFieldType.TimePicker,
            is ComposeFieldType.DateTimePicker -> customField.max_date
            is ComposeFieldType.Dropdown,
            is ComposeFieldType.Switch,
            is ComposeFieldType.CheckBox,
            is ComposeFieldType.RadioButton,
            is ComposeFieldType.Currency,
            is ComposeFieldType.MobileNo -> ""
        }
    }

    fun getTextFromValue(value: String): String {
        return when (this.type) {
            is ComposeFieldType.TextBox,
            is ComposeFieldType.DatePicker,
            ComposeFieldType.TimePicker,
            ComposeFieldType.Currency,
            is ComposeFieldType.MobileNo,
            ComposeFieldType.DateTimePicker -> {
                value
            }
            ComposeFieldType.Switch,
            ComposeFieldType.Dropdown,
            ComposeFieldType.RadioButton -> {
                this.defaultValues.find { x -> x.id == value }?.text ?: ""
            }
            ComposeFieldType.CheckBox -> {
                val finalStr = buildString {
                    value.split("::").forEach { newVal ->
                        if (newVal.isNotEmpty()) {
                            val text =
                                this@ComposeFieldModule.defaultValues
                                    .find { x -> x.id == newVal }
                                    ?.text ?: ""
                            append(text + ", ")
                        }
                    }
                }
                finalStr
            }
        }
    }
}

fun String?.CHOICE(): ComposeFieldYesNo {
    return if (this==null ||
        this.equals("0", true) ||
        this.equals("false", true) ||
        this.equals("no", true) ||
        this.isEmpty())
        ComposeFieldYesNo.NO
    else ComposeFieldYesNo.YES
}

fun String.fieldType(inputType:String,fieldName:String,fieldHint:String?): ComposeFieldType {
    return when (this.lowercase()) {
        "textbox" -> {
            if (isCurrencyField(fieldName)){
                ComposeFieldType.Currency
            }else {
                ComposeFieldType.TextBox(
                    keyboardType = inputType.lowercase().keyboardType()
                )
            }
        }
        "textarea" -> ComposeFieldType.TextBox(
            keyboardType = ComposeKeyboardType.TEXTAREA
        )
        "dropdown" -> ComposeFieldType.Dropdown
        "date" -> ComposeFieldType.DatePicker(
            ageCalculation = shouldCalculateAge(fieldName),
            helperText = getHelperText(fieldHint,fieldName)
        )
        "date_time" -> ComposeFieldType.DateTimePicker
        "radiobutton" -> ComposeFieldType.RadioButton
        "switch" -> ComposeFieldType.Switch
        "time" -> ComposeFieldType.TimePicker
        else -> ComposeFieldType.CheckBox
    }
}

fun isCurrencyField(fieldName:String):Boolean{
    return fieldName.isMatchingAny("sum_assured,amount,premium")
}

fun shouldCalculateAge(fieldName: String):Boolean{
    val isDob = fieldName.isMatchingAny("dob","date_of_birth")
    return isDob
}

fun getHelperText(hint: String?,name:String):String{
    return hint?:if (shouldCalculateAge(name))"DOB should be same as National ID Card." else ""
}

fun String.keyboardType(): ComposeKeyboardType {
    return when (this.lowercase()) {
        "text" -> ComposeKeyboardType.TEXT
        "cnic" -> ComposeKeyboardType.CNIC
        "email" -> ComposeKeyboardType.EMAIL
        "number" -> ComposeKeyboardType.NUMBER
        else -> ComposeKeyboardType.NONE
    }
}

data class ChildValueModel(
    val fieldModule: ComposeFieldModule,
    val value: String,
    val childValues: (List<DefaultValues>) -> Unit
)
