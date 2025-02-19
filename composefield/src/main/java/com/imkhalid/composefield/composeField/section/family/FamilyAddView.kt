package com.imkhalid.composefield.composeField.section.family

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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.util.getFieldByFieldName
import com.imkhalid.composefield.composeField.section.textColor
import com.imkhalid.composefield.composeField.section.titleColor
import com.imkhalid.composefield.composeField.responsiveHeight
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.responsiveWidth
import com.imkhalid.composefield.composeField.util.validate

internal fun LazyListScope.FamilyAddView(
    userCountry:String,
    modifier: Modifier,
    familyData: FamilyData
) {
    item {
        var shouldShowAddButton by remember {
            mutableStateOf(
                familyData.familySetup?.showAddButton(familyData.snapshotStateList) ?: true
            )
        }
        var showAddPopup by remember { mutableStateOf(false) }
        var editDataInd: Int? by remember { mutableStateOf(null) }
        Column(modifier = modifier.padding(responsiveSize(size = 20))) {
            Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = familyData.title,
                    fontSize = responsiveTextSize(size = 20).sp,
                    color = titleColor,
                    modifier = Modifier.weight(1f)
                )
                if (shouldShowAddButton) {
                    familyData.AddButton?.invoke { showAddPopup = true }
                }
            }
            Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))

            familyData.snapshotStateList.forEachIndexed { index, map ->
                val item = familyData.snapshotStateList[index]
                FamilyItem(
                    map = item,
                    onEdit = {
                        editDataInd = index
                        showAddPopup = true
                    },
                    onDelete = {
                        familyData.snapshotStateList.removeAt(index)
                        shouldShowAddButton =
                            familyData.familySetup.showAddButton(familyData.snapshotStateList)
                    }
                )
            }

            if (showAddPopup) {
                FamilyPopup(
                    userCountry=userCountry,
                    data = familyData.snapshotStateList.getOrNull(editDataInd ?: -1),
                    familyData = familyData,
                    onDismiss = {
                        editDataInd = null
                        showAddPopup = false
                    },
                    GradientButton = familyData.PopupButton,
                    onDone = { data ->
                        showAddPopup = false
                        if (editDataInd != null) {
                            editDataInd?.let {
                                try {
                                    familyData.snapshotStateList.removeAt(it)
                                } catch (_: IndexOutOfBoundsException) {
                                }
                                familyData.snapshotStateList.add(it, data)
                            }
                        } else {
                            familyData.snapshotStateList.add(data)
                        }
                        editDataInd = null
                        shouldShowAddButton =
                            familyData.familySetup?.showAddButton(familyData.snapshotStateList)
                                ?: true
                    }
                )
            }
        }
    }
}

@Composable
private fun FamilyItem(map: Map<String, String>, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier =
        Modifier
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
            text = map.getOrDefault("relation", "").capitalize(),
            color = textColor,
            fontSize = responsiveTextSize(size = 17).sp,
            modifier = Modifier
                .padding(start = responsiveWidth(size = 14))
                .weight(1f)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_edit_family),
            modifier =
            Modifier
                .size(responsiveSize(size = 35))
                .clickable { onEdit.invoke() }
                .padding(end = responsiveWidth(size = 8)),
            contentDescription = null
        )
        Image(
            painter = painterResource(id = R.drawable.ic_delete_family),
            modifier =
            Modifier
                .size(responsiveSize(size = 35))
                .clickable { onDelete.invoke() }
                .padding(end = responsiveWidth(size = 8)),
            contentDescription = null
        )
    }
}

@Composable
fun FamilyPopup(
    userCountry: String,
    modifier: Modifier = Modifier,
    familyData: FamilyData,
    GradientButton: @Composable() (BoxScope.(() -> Unit) -> Unit)? = null,
    data: Map<String, String>? = null,
    onDismiss: () -> Unit,
    onDone: (Map<String, String>) -> Unit,
) {

    var fields: List<ComposeFieldStateHolder> by remember { mutableStateOf(emptyList()) }
    if (fields.isEmpty()) {
        fields = familyData.familySetup.getFields(
            list = familyData.snapshotStateList,
            familyData.familySetup,
            data
        )
    }
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier =
            Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(responsiveSize(size = 15))
                )
                .padding(responsiveSize(size = 20))
        ) {
            Column {
                Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (data == null) "Add Family" else "Edit Family",
                        fontSize = responsiveTextSize(size = 20).sp,
                        color = titleColor,
                        modifier = Modifier.weight(1f)
                    )

                    Image(
                        painter =
                        painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                        contentDescription = null,
                        modifier = Modifier.clickable { onDismiss.invoke() }
                    )
                }
                Spacer(modifier = Modifier.height(responsiveHeight(size = 15)))
                fields.forEach {
                    com.imkhalid.composefieldproject.composeField.fields
                        .ComposeFieldBuilder()
                        .Build(modifier = Modifier, userCountry = userCountry, stateHolder = it) { pair, value ->
                            if (it.state.field.name == "relation") {
                                fields.getFieldByFieldName("dob")?.also {
                                    val minValue =
                                        when (value.lowercase()) {
                                            "child" -> familyData.familySetup.childMinDate
                                            "parent" -> familyData.familySetup.parentMinDate
                                            else -> familyData.familySetup.spouseMinDate
                                        }
                                    val maxValue =
                                        when (value.lowercase()) {
                                            "child" -> familyData.familySetup.childMaxDate
                                            "parent" -> familyData.familySetup.parentMaxDate
                                            else -> familyData.familySetup.spouseMaxDate
                                        }
                                    it.updateField("")
                                    it.updatedField(
                                        field =
                                        it.state.field.copy(
                                            minValue = maxValue,
                                            maxValue = minValue,
                                            /*helperText =
                                            if (
                                                minValue.isNotEmpty() &&
                                                maxValue.isNotEmpty()
                                            ) {
                                                "${it.state.field.label} should be between ${
                                                    changeDateFormat(
                                                        "yyyy-mm-dd",
                                                        "dd-MMM-yyyy",
                                                        maxValue
                                                    )
                                                } to ${
                                                    changeDateFormat(
                                                        "yyyy-mm-dd",
                                                        "dd-MMM-yyyy",
                                                        minValue
                                                    )
                                                }"
                                            } else if (minValue.isNotEmpty()) {
                                                "${it.state.field.label} can be maximum to ${
                                                    changeDateFormat(
                                                        "yyyy-mm-dd",
                                                        "dd-MMM-yyyy",
                                                        maxValue
                                                    )
                                                }"
                                            } else if (maxValue.isNotEmpty()) {
                                                "${it.state.field.label} can not be further than ${
                                                    changeDateFormat(
                                                        "yyyy-mm-dd",
                                                        "dd-MMM-yyyy",
                                                        minValue
                                                    )
                                                }"
                                            } else ""*/
                                        )
                                    )
                                }
                            }
                            it.updateField(value)
                        }
                }
                Spacer(modifier = Modifier.height(responsiveHeight(size = 65)))
            }
            GradientButton?.invoke(this) {
                if (fields.validate()) {
                    val map = HashMap<String, String>()
                    fields.forEach { map.put(it.state.field.name, it.state.text) }
                    onDone(map)
                }
            }
        }
    }
}
