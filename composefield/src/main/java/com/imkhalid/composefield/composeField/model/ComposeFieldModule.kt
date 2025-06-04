package com.imkhalid.composefield.composeField.model

import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.util.isMatchingAny
import com.imkhalid.composefield.model.CustomFields
import com.imkhalid.composefield.model.DefaultValues

/** This Class is Used for parsing Server Field to Compose Field */
data class ComposeFieldModule(
    val id: String = "",
    val name: String = "",
    val type: ComposeFieldType = ComposeFieldType.TEXT_BOX,
    val keyboardType: ComposeKeyboardTypeAdv = ComposeKeyboardTypeAdv.TEXT,
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
    val parent_field_value_id: String? = null,
    val isCurrencyField: Boolean = false,
    val isDisplay : Boolean = false,
) {

    fun parseCustomField(
        customField: CustomFields,
        sortNumber: Int = 0,
    ): ComposeFieldModule {
        val selected_value = customField.selectedValue?.takeIf { x -> x.isNotEmpty() }
            ?: customField.selected_value.orEmpty()
        val isEmailMobile =
            (customField.field_name == "email" || customField.field_name == "mobile_no")
        val showIcon =
            customField.label.contains("date", true) ||
                    customField.field_name.contains("cnic", true)
        return ComposeFieldModule(
            id = customField.id.toString(),
            parent_field_value_id = customField.parent_field_value_id,
            name = customField.field_name,
            childID = customField.child_id.toString(),
            type = customField.type.fieldType(),
            keyboardType = customField.inputType.keyboardType(
                customField.type.fieldType(),
                customField.field_name,
                customField.field_hint
            ),
            isEditable = (customField.is_readonly == 1).not().toString().CHOICE(),
            value = getInitialValue(customField, selected_value),
            label = customField.label,
            hint = customField.field_hint ?: "",
            description = customField.description ?: "",
            defaultValues = customField.default_values,
            required = customField.required.toString().CHOICE(),
            minValue = getMinValue(customField, customField.type.fieldType()),
            maxValue = getMaxValue(customField, customField.type.fieldType()),
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
            helperText = getHelperText(customField.field_hint, customField.field_name),
            patternMessage = if (customField.regex.isNullOrEmpty()
                    .not()
            ) customField.field_hint.orEmpty() else "",
            isDisplay = customField.display == 1
        )
    }

    private fun getHelperText(hint: String?, name: String): String {
        return if (hint.orEmpty().isNotEmpty())
            hint.orEmpty()
        else if (name.contains("dob", true) || name.contains("date_of_birth", true)) {
            "DOB should be same as National ID Card"
        } else
            ""
    }

    private fun getHiddenValue(
        field: Int,
        parentFieldValueId: String?,
        selectedValue: String?
    ): ComposeFieldYesNo {
        //parent id null or empty means it should not hide,
        //checking selected value if its prefilled than it should not hide
        // field == 1 means it should not hide.
        return if ((parentFieldValueId.isNullOrEmpty()
                .not() && selectedValue.isNullOrEmpty()) || field == 0
        )
            ComposeFieldYesNo.YES
        else
            ComposeFieldYesNo.NO
    }

    private fun getInitialValue(customField: CustomFields, selectedValue: String): String {
        return if (selectedValue.isNotEmpty()) selectedValue
        else if (customField.type.fieldType() == ComposeFieldType.SWITCH) {
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
            ComposeFieldType.TEXT_BOX,
            ComposeFieldType.TEXT_AREA -> customField.min_rule
            ComposeFieldType.DATE_PICKER,
            ComposeFieldType.TIME_PICKER,
            ComposeFieldType.DATE_TIME_PICKER -> customField.min_date
            ComposeFieldType.DROP_DOWN,
            ComposeFieldType.SWITCH,
            ComposeFieldType.CHECK_BOX,
            ComposeFieldType.RADIO_BUTTON -> ""
        }
    }

    fun getMaxValue(customField: CustomFields, type: ComposeFieldType): String {
        return when (type) {
            ComposeFieldType.TEXT_BOX -> customField.max_rule
            ComposeFieldType.TEXT_AREA -> customField.max_rule
            ComposeFieldType.DATE_PICKER,
            ComposeFieldType.TIME_PICKER,
            ComposeFieldType.DATE_TIME_PICKER -> customField.max_date
            ComposeFieldType.DROP_DOWN,
            ComposeFieldType.SWITCH,
            ComposeFieldType.CHECK_BOX,
            ComposeFieldType.RADIO_BUTTON -> ""
        }
    }

    fun getTextFromValue(value: String): String {
        return when (this.type) {
            ComposeFieldType.TEXT_BOX,
            ComposeFieldType.TEXT_AREA,
            ComposeFieldType.DATE_PICKER,
            ComposeFieldType.TIME_PICKER,
            ComposeFieldType.DATE_TIME_PICKER -> {
                value
            }
            ComposeFieldType.SWITCH,
            ComposeFieldType.DROP_DOWN,
            ComposeFieldType.RADIO_BUTTON -> {
                this.defaultValues.find { x -> x.id == value }?.text ?: ""
            }
            ComposeFieldType.CHECK_BOX -> {
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
    return if (this == null ||
        this.equals("0", true) ||
        this.equals("false", true) ||
        this.equals("no", true) ||
        this.isEmpty()
    )
        ComposeFieldYesNo.NO
    else ComposeFieldYesNo.YES
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

fun String.keyboardType(
    type: ComposeFieldType,
    fieldName: String,
    hint: String?
): ComposeKeyboardTypeAdv {
    return when (this.lowercase()) {
        "text" -> {
            if (type == ComposeFieldType.DATE_PICKER) {
                val isDob = fieldName.isMatchingAny("dob", "date_of_birth")
                ComposeKeyboardTypeAdv.DATE(
                    ageCalculation = isDob,
                    helperText = hint
                        ?: if (isDob) "DOB should be same as National ID Card." else ""
                )
            } else
                ComposeKeyboardTypeAdv.TEXT
        }
        "cnic" -> ComposeKeyboardTypeAdv.CNIC
        "email" -> ComposeKeyboardTypeAdv.EMAIL
        "mobile",
        "mobile_number" -> ComposeKeyboardTypeAdv.MOBILE_NO()
        "number" -> ComposeKeyboardTypeAdv.NUMBER
        else -> ComposeKeyboardTypeAdv.NONE
    }
}

data class ChildValueModel(
    val fieldModule: ComposeFieldModule,
    val value: String,
    val childValues: (List<DefaultValues>) -> Unit
)