package com.imkhalid.composefield.composeField.section.family

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.imkhalid.composefield.composeField.responsiveHeight
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.util.validate

internal fun LazyListScope.FamilyEditView(
    userCountry:String,
    modifier: Modifier,
    familyData: FamilyData,
    errorItem:MutableState<Int>) {

    var expandedItem by mutableStateOf(-1)
    itemsIndexed(familyData.snapshotStateList) { index, item ->
        val context = LocalContext.current
        FamilyItem(
            userCountry=userCountry,
            familyData = familyData,
            index = index,
            expanded = expandedItem == index,
            errorItem = errorItem.value==index,
            onClick = {
                if (expandedItem == index) {
                    expandedItem = -1
                } else {
                    expandedItem = index
                }
                if (errorItem.value == index) {
                    errorItem.value=-1
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
    userCountry: String,
    modifier: Modifier = Modifier,
    familyData: FamilyData,
    index: Int,
    expanded: Boolean,
    errorItem: Boolean,
    onClick: () -> Unit,
    onAddClick: (List<ComposeFieldStateHolder>) -> Unit
) {
    val item = familyData.snapshotStateList.getOrNull(index) ?: emptyMap()
    val form = familyData.familySetup.getFields(list = SnapshotStateList(),familyData.familySetup, data = item)
    Box(modifier = Modifier.padding(top = responsiveHeight(size = 5))) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier =
                Modifier
                    .border(
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
                            userCountry = userCountry
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
            Modifier
                .height(responsiveHeight(size = 60))
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
                text = title.capitalize(),
                color = Color.Black,
                fontSize = responsiveTextSize(size = 17).sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            if (errorItem){
                Image(
                    painter = painterResource(id = com.imkhalid.composefield.R.drawable.ic_error),
                    contentDescription = "",
                    modifier= Modifier
                        .padding(horizontal = responsiveSize(size = 10))
                        .size(responsiveSize(size = 15))
                )
            }
            Image(
                painter =
                    if (expanded) painterResource(id = com.imkhalid.composefield.R.drawable.ic_up_arrow)
                    else painterResource(id = com.imkhalid.composefield.R.drawable.ic_down_arrow),
                contentDescription = ""
            )
        }
    }
}
