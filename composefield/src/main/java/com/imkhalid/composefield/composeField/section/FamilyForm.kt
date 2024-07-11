package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fields.changeDateFormat
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.section.family.FamilyAddView
import com.imkhalid.composefield.composeField.section.family.FamilyEditView
import com.imkhalid.composefield.composeField.validate
import com.ozonedDigital.jhk.ui.common.responsiveHeight
import com.ozonedDigital.jhk.ui.common.responsiveSize
import com.ozonedDigital.jhk.ui.common.responsiveTextSize
import com.ozonedDigital.jhk.ui.common.responsiveWidth

val titleColor = Color(0xFFBA0C2F)
val textColor = Color(0xFF5B6770)

@Preview(widthDp = 720)
fun LazyListScope.FamilyForm(
    modifier: Modifier = Modifier,
    familyData: FamilyData
) {
    if (familyData.isEditView) {
        FamilyEditView(modifier, familyData)
    } else {
        FamilyAddView(modifier, familyData)
    }
}