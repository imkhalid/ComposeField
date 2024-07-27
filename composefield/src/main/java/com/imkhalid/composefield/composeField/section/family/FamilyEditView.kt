package com.imkhalid.composefield.composeField.section.family

import android.R
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.validate
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.ozonedDigital.jhk.ui.common.responsiveHeight
import com.ozonedDigital.jhk.ui.common.responsiveSize
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

internal fun LazyListScope.FamilyEditView(modifier: Modifier, familyData: FamilyData) {

    var expandedItem by mutableStateOf(-1)
    itemsIndexed(familyData.snapshotStateList) { index, item ->
        val context = LocalContext.current
        FamilyItem(
            familyData = familyData,
            index = index,
            expanded = expandedItem == index,
            onClick = {
                if (expandedItem == index) {
                    expandedItem = -1
                } else {
                    expandedItem = index
                }
            },
            onAddClick = { form ->
                if (form.validate()) {
                    val map = HashMap(item)
                    map["isValidated"] = "1"
                    form.forEach { map[it.state.field.name] = it.state.text }
                    familyData.snapshotStateList.apply {
                        removeAt(index)
                        add(index, map)
                    }
                    expandedItem = -1
                } else {
                    Toast.makeText(context, "Kindly Fill All Fields", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

private @Composable fun FamilyItem(
    modifier: Modifier = Modifier,
    familyData: FamilyData,
    index: Int,
    expanded: Boolean,
    onClick: () -> Unit,
    onAddClick: (List<ComposeFieldStateHolder>) -> Unit
) {
    val item = familyData.snapshotStateList.getOrNull(index) ?: emptyMap()
    val form = familyData.familySetup.getFields(list = SnapshotStateList(), data = item)
    Box(modifier = Modifier.padding(top = responsiveHeight(size = 5))) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier =
                    Modifier.border(
                            width = 1.dp,
                            shape = RoundedCornerShape(responsiveSize(size = 12)),
                            color = Color.Gray
                        )
                        .padding(top = responsiveHeight(size = 75))
                        .padding(horizontal = responsiveSize(size = 10))
                        .padding(bottom = responsiveHeight(size = 10))
            ) {
                form.forEach { field ->
                    ComposeFieldBuilder()
                        .Build(
                            stateHolder = field,
                        )
                }
                Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    familyData.AddButton.invoke { onAddClick.invoke(form) }
                }
            }
        }
        Row(
            modifier =
                Modifier.height(responsiveHeight(size = 60))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(responsiveSize(size = 12))
                    )
                    .clickable { onClick.invoke() }
                    .padding(responsiveSize(size = 10)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val title =
                item.getOrDefault("name", "").takeIf { x -> x.isNotEmpty() }
                    ?: item.getOrDefault("relation", "")
            Text(
                text = title,
                color = Color.Black,
                fontSize = responsiveTextSize(size = 17).sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter =
                    if (expanded) painterResource(id = R.drawable.arrow_up_float)
                    else painterResource(id = R.drawable.arrow_down_float),
                contentDescription = ""
            )
        }
    }
}
