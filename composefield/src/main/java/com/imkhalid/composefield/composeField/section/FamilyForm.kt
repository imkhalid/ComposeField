package com.imkhalid.composefield.composeField.section

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.model.FamilySetup
import com.imkhalid.composefield.composeField.validate
import com.ozonedDigital.jhk.ui.common.responsiveHeight
import com.ozonedDigital.jhk.ui.common.responsiveSize
import com.ozonedDigital.jhk.ui.common.responsiveTextSize
import com.ozonedDigital.jhk.ui.common.responsiveWidth

val titleColor = Color(0xFFBA0C2F)
val textColor = Color(0xFF5B6770)

@Preview(widthDp = 720)
@Composable
fun LazyListScope.FamilyForm(
    parentNav:NavHostController,
    modifier: Modifier = Modifier,
    familyData: FamilyData
) {
    var shouldShowAddButton by remember {
        mutableStateOf(familyData.familySetup?.showAddButton(familyData.snapshotStateList) ?: true)
    }
    var showAddPopup by remember {
        mutableStateOf(false)
    }

    item{
        Column(
            modifier = modifier
                .padding(responsiveSize(size = 20))
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Family's Detail",
                    fontSize = responsiveTextSize(size = 20).sp,
                    color = titleColor,
                    modifier = Modifier.weight(1f)
                )
                if (shouldShowAddButton) {
                    familyData.AddButton?.invoke{
                        showAddPopup = true
                    }
                }
            }
            Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))

            familyData.snapshotStateList.forEachIndexed { index, map ->
                val item = familyData.snapshotStateList[index]
                FamilyItem(
                    map = item,
                    onEdit = {

                    },
                    onDelete = {
                        familyData.snapshotStateList.removeAt(index)
                        shouldShowAddButton = familyData.familySetup.showAddButton(familyData.snapshotStateList)
                    }
                )
            }

            if (showAddPopup) {
                FamilyPopup(
                    parentNav=parentNav,
                    section = familyData.familySetup?.getComposeSection(familyData.snapshotStateList),
                    onDismiss = { showAddPopup=false },
                    GradientButton = familyData.PopupButton,
                    onDone = {
                        showAddPopup=false
                        familyData.snapshotStateList.add(it)
                        shouldShowAddButton = familyData.familySetup?.showAddButton(familyData.snapshotStateList)?:true
                    }
                )
            }
            BackHandler {
                if (showAddPopup)
                    showAddPopup=false
                else
                    parentNav.popBackStack()
            }
        }
    }
}

@Composable
private fun FamilyItem(map: Map<String, String>, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = responsiveHeight(size = 8))
            .background(
                shape = RoundedCornerShape(responsiveSize(size = 15)),
                color = Color.White
            )
            .padding(
                vertical = responsiveSize(size = 15),
                horizontal = responsiveSize(size = 21)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_family_icon),
            modifier = Modifier.size(responsiveSize(size = 40)),
            contentDescription = null
        )
        Text(
            text = map.getOrDefault("relation","").capitalize(),
            color = textColor,
            fontSize = responsiveTextSize(size = 17).sp,
            modifier = Modifier
                .padding(start = responsiveWidth(size = 14))
                .weight(1f)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_edit_family),
            modifier = Modifier
                .size(responsiveSize(size = 35))
                .clickable { onEdit.invoke() }
                .padding(end = responsiveWidth(size = 8)),
            contentDescription = null
        )
        Image(
            painter = painterResource(id = R.drawable.ic_delete_family),
            modifier = Modifier
                .size(responsiveSize(size = 35))
                .clickable { onDelete.invoke() }
                .padding(end = responsiveWidth(size = 8)),
            contentDescription = null
        )

    }
}

@Composable
fun FamilyPopup(
    parentNav: NavHostController,
    modifier: Modifier = Modifier,
    GradientButton:(@Composable (()->Unit)->Unit)?=null,
    section: List<ComposeSectionModule>?,
    onDismiss: () -> Unit,
    onDone: (Map<String, String>) -> Unit,
) {
    var sectionB: Sections? by remember {
        mutableStateOf(null)
    }
    section?.let {
        Dialog(onDismissRequest = onDismiss) {
            Column {
                if (sectionB == null)
                    sectionB = Sections(
                        parentNav = parentNav,
                        nav = rememberNavController(),
                        sectionType = SectionType.Simple,
                    )
                sectionB?.Build(
                    sections = section,
                    button = {
                        GradientButton?.invoke {
                            if (sectionB?.sectionState?.validate() == true) {
                                val map = HashMap<String, String>()
                                sectionB?.sectionState?.forEach { s, composeFieldStateHolders ->
                                    composeFieldStateHolders?.forEach {
                                        map.put(it.state.field.name, it.state.text)
                                    }
                                }
                                onDone(map)
                            }
                        }

                    }
                )
            }
        }
    } ?: run { onDismiss.invoke() }
    BackHandler {
        onDismiss.invoke()
    }

}