package com.imkhalid.composefield.composeField.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.model.DefaultValues

data class FamilyData(
    val familySetup: FamilySetup,
    val snapshotStateList: SnapshotStateList<Map<String, String>>,
    val AddButton:@Composable (onClick:()->Unit)->Unit,
    val PupupButton: @Composable (onClick:()->Unit)->Unit,
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
        return (hasChild.not() || (hasChild && child.size <= maxNoOfChild)) &&
                (hasParent.not() || (hasParent && parent.size <= maxNoOfParent)) &&
                (hasSpouse.not() ||(hasSpouse && spouse.size <= maxNoOfSpouse))
    }


    fun getComposeSection(snapshotStateList: SnapshotStateList<Map<String, String>>): List<ComposeSectionModule> {
        val spouse = snapshotStateList.filter { x -> x.values.contains("spouse") }
        val child = snapshotStateList.filter { x -> x.values.contains("child") }
        val parent = snapshotStateList.filter { x -> x.values.contains("parent") }
        return arrayListOf(ComposeSectionModule(
            name = "Family",
            sortNumber = 0,
            fields = fields.map {
                ComposeFieldModule(
                    name = it.familyDetailField,
                    label = it.familyDetailField.capitalize(),
                    type = when (it.familyDetailField) {
                        "gender", "relation" -> {
                            ComposeFieldType.DROP_DOWN
                        }

                        "dob" -> {
                            ComposeFieldType.DATE_PICKER
                        }

                        else -> {
                            ComposeFieldType.TEXT_BOX
                        }
                    },
                    defaultValues = when (it.familyDetailField) {
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
}

data class FamilyField(
    val familySetupId: String,
    val familyDetailField: String,
    val required: Boolean,
    val visible: Boolean
)