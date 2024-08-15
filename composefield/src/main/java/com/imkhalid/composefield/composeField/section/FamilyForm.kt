package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.model.FamilySetup
import com.imkhalid.composefield.composeField.section.family.FamilyAddView
import com.imkhalid.composefield.composeField.section.family.FamilyEditView

val titleColor = Color(0xFFBA0C2F)
val textColor = Color(0xFF5B6770)

@Preview(widthDp = 720)
fun LazyListScope.FamilyForm(
    modifier: Modifier = Modifier,
    familyData: FamilyData,
    errorItem:MutableState<Int> = mutableStateOf(-1),
    userCountry:String
) {
    if (familyData.isEditView) {
        FamilyEditView(modifier = modifier, familyData = familyData, errorItem = errorItem,userCountry=userCountry)
    } else {
        FamilyAddView(modifier=modifier, familyData = familyData,userCountry=userCountry)
    }
}

fun SnapshotStateList<Map<String,String>>.validateFamily(familySetup: FamilySetup): String {
    val spouseSize = this.filter { x -> x.values.contains("spouse") }.size
    val childSize = this.filter { x -> x.values.contains("child") }.size
    val parentSize = this.filter { x -> x.values.contains("parent") }.size
    val spouseMin = familySetup.minNoOfSpouse
    val spouseMax = familySetup.maxNoOfSpouse
    val childMin = familySetup.minNoOfChild
    val childMax = familySetup.maxNoOfChild
    val parentMin = familySetup.minNoOfParent
    val parentMax = familySetup.maxNoOfParent

    val validate = (
            spouseSize in spouseMin..spouseMax &&
                    childSize in childMin..childMax &&
                    parentSize in parentMin..parentMax
            )
    return if (validate)
        ""
    else {
        val message = if (spouseSize <spouseMin || spouseSize>spouseMax)
            "Spouse must be Between $spouseMin to $spouseMax"
        else if (childSize <childMin || childSize>childMax)
            "Child(s) must be Between $childMin to $childMax"
        else
            "Parent(s) must be Between $parentMin to $parentMax"

        message
    }
}
