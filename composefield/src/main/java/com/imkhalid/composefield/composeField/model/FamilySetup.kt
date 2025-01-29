package com.imkhalid.composefield.composeField.model

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.model.DefaultValues

data class FamilyData(
    val isEditView: Boolean = false,
    val familySetup: FamilySetup,
    val snapshotStateList: SnapshotStateList<Map<String, String>>,
    var AddButton: (@Composable (onClick: () -> Unit) -> Unit) = {},
    var PopupButton: (@Composable BoxScope.(onClick: () -> Unit) -> Unit) = {},
)

data class FamilySetup(
    val hasSpouse: Boolean,
    val spouseMinDate: String,
    val spouseMaxDate: String,
    val hasChild: Boolean,
    val childMinDate: String,
    val childMaxDate: String,
    val hasParent: Boolean,
    val parentMinDate: String,
    val parentMaxDate: String,
    val minNoOfParent: Int,
    val minNoOfChild: Int,
    val minNoOfSpouse: Int,
    val maxNoOfParent: Int,
    val maxNoOfChild: Int,
    val maxNoOfSpouse: Int,
    val fields: List<FamilyField>
) {
    fun showAddButton(snapshotStateList: SnapshotStateList<Map<String, String>>): Boolean {
        val spouse = snapshotStateList.filter { x -> x.values.contains("spouse") }
        val child = snapshotStateList.filter { x -> x.values.contains("child") }
        val parent = snapshotStateList.filter { x -> x.values.contains("parent") }
        val validated =  (hasChild.not() || (hasChild && child.size <= maxNoOfChild)) &&
            (hasParent.not() || (hasParent && parent.size <= maxNoOfParent)) &&
            (hasSpouse.not() || (hasSpouse && spouse.size <= maxNoOfSpouse))

        val limitNotReach = ((hasChild.not() || (hasChild && child.size == maxNoOfChild)) &&
                (hasParent.not() || (hasParent && parent.size == maxNoOfParent)) &&
                (hasSpouse.not() || (hasSpouse && spouse.size == maxNoOfSpouse))).not()

        return validated && limitNotReach
    }

    fun getComposeSection(
        snapshotStateList: SnapshotStateList<Map<String, String>>
    ): List<ComposeSectionModule> {
        val spouse = snapshotStateList.filter { x -> x.values.contains("spouse") }
        val child = snapshotStateList.filter { x -> x.values.contains("child") }
        val parent = snapshotStateList.filter { x -> x.values.contains("parent") }
        return arrayListOf(
            ComposeSectionModule(
                name = "Family",
                sortNumber = 0,
                fields =
                    fields.map {
                        ComposeFieldModule(
                            name = it.familyDetailField,
                            label = it.familyDetailField.capitalize(),
                            type =
                                when (it.familyDetailField) {
                                    "gender",
                                    "relation" -> {
                                        ComposeFieldType.DROP_DOWN
                                    }
                                    "dob" -> {
                                        ComposeFieldType.DATE_PICKER
                                    }
                                    else -> {
                                        ComposeFieldType.TEXT_BOX
                                    }
                                },
                            defaultValues =
                                when (it.familyDetailField) {
                                    "gender" -> {
                                        arrayListOf(
                                            DefaultValues("male", "Male"),
                                            DefaultValues("female", "Female"),
                                        )
                                    }
                                    "relation" -> {
                                        arrayListOf<DefaultValues>().apply {
                                            if (hasChild && child.size < maxNoOfChild)
                                                add(DefaultValues("child", "Child"))
                                            if (hasSpouse && spouse.size < maxNoOfSpouse)
                                                add(DefaultValues("spouse", "Spouse"))
                                            if (hasParent && parent.size < maxNoOfParent)
                                                add(DefaultValues("parent", "Parent"))
                                        }
                                    }
                                    else -> {
                                        arrayListOf()
                                    }
                                },
                        )
                    }
            )
        )
    }

    @Composable
    fun getFields(
        list: SnapshotStateList<Map<String, String>>,
        familySetup: FamilySetup,
        data: Map<String, String>?,
    ): List<ComposeFieldStateHolder> {
        val spouse = list.filter { x -> x.values.contains("spouse") }
        val child = list.filter { x -> x.values.contains("child") }
        val parent = list.filter { x -> x.values.contains("parent") }
        val orderMap = HashMap<String,Int>()
            .apply {

                put("first_name",1)
                put("last_name",2)
                put("gender",3)
                put("relation",4)
                put("dob",5)
                put("id_no",6)
                put("cnic",7)
                put("email",8)
                put("mobile_no",9)
                put("phone_no",10)
            }
        return fields.map {
            rememberFieldState(
                fieldModule =
                    ComposeFieldModule(
                        name = it.familyDetailField,
                        label = it.familyDetailField.replace("_", " ").capitalize(),
                        type =
                            when (it.familyDetailField) {
                                "gender",
                                "relation" -> {
                                    if (data != null && it.familyDetailField == "relation") {
                                        ComposeFieldType.TEXT_BOX
                                    } else ComposeFieldType.DROP_DOWN
                                }
                                "dob" -> {
                                    ComposeFieldType.DATE_PICKER
                                }
                                else -> {
                                    ComposeFieldType.TEXT_BOX
                                }
                            },
                        sortNumber = orderMap.getOrDefault(it.familyDetailField,1),
                        keyboardType =
                            when (it.familyDetailField) {
                                "email" -> ComposeKeyboardTypeAdv.EMAIL
                                "mobile_no",
                                "phone_no" -> ComposeKeyboardTypeAdv.MOBILE_NO()
                                else -> ComposeKeyboardTypeAdv.TEXT
                            },
                        isEditable =
                            if (data != null && it.familyDetailField.equals("relation"))
                                ComposeFieldYesNo.NO
                            else ComposeFieldYesNo.YES,
                        value =
                            if (data != null) data.getOrDefault(it.familyDetailField, "") else "",
                        defaultValues =
                            when (it.familyDetailField) {
                                "gender" -> {
                                    arrayListOf(
                                        DefaultValues("male", "Male"),
                                        DefaultValues("female", "Female"),
                                    )
                                }
                                "relation" -> {
                                    arrayListOf<DefaultValues>().apply {
                                        if (hasChild && child.size < maxNoOfChild)
                                            add(DefaultValues("child", "Child"))
                                        if (hasSpouse && spouse.size < maxNoOfSpouse)
                                            add(DefaultValues("spouse", "Spouse"))
                                        if (hasParent && parent.size < maxNoOfParent)
                                            add(DefaultValues("parent", "Parent"))
                                    }
                                }
                                else -> {
                                    arrayListOf()
                                }
                            },
                        maxValue = if (data!=null && it.familyDetailField == "dob"){
                            if (data.get("relation").orEmpty().equals("spouse",true)){
                                familySetup.spouseMinDate
                            }else if (data.get("relation").orEmpty().equals("child",true)){
                                familySetup.childMinDate
                            }else if (data.get("relation").orEmpty().equals("parent",true)){
                                familySetup.parentMinDate
                            }else{
                                ""
                            }
                        }else "",
                        minValue = if (data!=null && it.familyDetailField == "dob"){
                            if (data.get("relation").orEmpty().equals("spouse",true)){
                                familySetup.spouseMaxDate
                            }else if (data.get("relation").orEmpty().equals("child",true)){
                                familySetup.childMaxDate
                            }else if (data.get("relation").orEmpty().equals("parent",true)){
                                familySetup.parentMaxDate
                            }else{
                                ""
                            }
                        }else ""
                    )
            )
        }.sortedBy { x->x.state.field.sortNumber }
    }
}

data class FamilyField(
    val familySetupId: String,
    val familyDetailField: String,
    val required: Boolean,
    val visible: Boolean
)
