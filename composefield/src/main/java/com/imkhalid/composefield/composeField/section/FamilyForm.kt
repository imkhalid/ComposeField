package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.section.family.FamilyAddView
import com.imkhalid.composefield.composeField.section.family.FamilyEditView

val titleColor = Color(0xFFBA0C2F)
val textColor = Color(0xFF5B6770)

@Preview(widthDp = 720)
fun LazyListScope.FamilyForm(modifier: Modifier = Modifier, familyData: FamilyData) {
    if (familyData.isEditView) {
        FamilyEditView(modifier, familyData)
    } else {
        FamilyAddView(modifier, familyData)
    }
}
