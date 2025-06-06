package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.TableColors
import com.imkhalid.composefield.composeField.TableConfig
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.model.TaggedMap
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefield.composeField.responsiveHeight
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.responsiveWidth
import com.imkhalid.composefield.theme.dashedBorder

class TableSection(
    nav: NavHostController,
    sectionType: SectionType,
    var min: Int = 0,
    var max: Int = 1,
    userCountry:String="2"
) : Sections(nav, sectionType,userCountry=userCountry) {

    @Composable
    fun TableBuild(
        modifier: Modifier = Modifier,
        sections: List<ComposeSectionModule>,
        tableConfig: TableConfig = TableConfig(),
        tableName: String,
        description: String,
        tableDataList: SnapshotStateList<TaggedMap> =
            SnapshotStateList(),
        preState: HashMap<String, List<ComposeFieldStateHolder>>? = null,
        onItemAdded: (HashMap<String, List<ComposeFieldStateHolder>>) -> Unit,
        onDeleteItem: (index: Int) -> Unit,
        onItemEdited: (HashMap<String, List<ComposeFieldStateHolder>>, index: Int) -> Unit,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        SingleItemHeader:
        @Composable
            (
            onEditClick: () -> Unit,
            onDeleteClick: () -> Unit,
            onExpandClick: () -> Unit,
            textTitle: String
        ) -> Unit,
        errorDialog:
        (@Composable
            (onClick: (positive: Boolean) -> Unit, onDismiss: () -> Unit) -> Unit)? =
            null
    ) {
        if (sectionNames.isEmpty()) {
            sections.mapTo(sectionNames) { it.name }
            sections.forEach {
                sectionState[it.name] =
                    it.fields.map { field ->
                        val preFieldState =
                            preState?.getOrDefault(it.name, emptyList())?.find { x ->
                                x.state.field.name == field.name
                            }
                        rememberFieldState(fieldModule = field, stateHolder = preFieldState)
                    }
            }
        }
        var showDialog by remember { mutableStateOf(false) }

        var editItem by remember { mutableStateOf(-1) }
        var expandedItem by remember { mutableStateOf(-1) }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(responsiveSize(size = 20)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tableConfig.showTitle) {
                Text(
                    text = tableName,
                    fontSize = responsiveTextSize(size = 16).sp,
                    color = tableConfig.tableColors.sectionTitleColor
                )
            }
            if (tableConfig.showLimit) {
                Text(
                    text =
                        if ((min == max && min > 0)||(min == 0 && max > 0)) "Maximum $max ${tableConfig.validationName}  can be Added"
                        else "You can add between  $min and $max ${tableConfig.validationName}.",
                    fontSize = responsiveTextSize(size = 12).sp,
                    color = tableConfig.tableColors.sectionLimitColor
                )
            }

            if(tableConfig.showDescription){
                Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))
                Text(
                    text = description,
                    fontSize = responsiveTextSize(size = 13).sp,
                    color = tableConfig.tableColors.sectionLimitColor,
                    minLines = 2
                )
            }
            if (tableDataList.size < max) {
                tableConfig.tableAddButton?.invoke(this) { showDialog = true }
            }

            Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(responsiveHeight(10))
            ) {
                items(tableDataList.size) {
                    if (tableDataList[it].isDeleted.not()) {
                        var itemName = tableName.replace("_", " ")
                        val item =
                            LinkedHashMap<String, Pair<String, String>>().apply {
                                tableDataList[it].data.forEach { x ->
                                    x.value.sortedBy { y -> y.state.field.sortNumber }
                                        .forEach { y ->
                                            if (y.state.field.isDisplay) {
                                                itemName += " (${y.state.field.getTextFromValue(y.state.text)})"

                                            }
                                            put(
                                                y.state.field.name,
                                                Pair(
                                                    y.state.field.label,
                                                    y.state.field.getTextFromValue(y.state.text)
                                                )
                                            )
                                        }
                                }
                            }
                        TableItem(
                            tableConfig.tableColors,
                            itemName,
                            it,
                            item,
                            SingleItemHeader = {
                                SingleItemHeader.invoke(
                                    /*onEditClick = */{ editItem = it },
                                    /*onDeleteClick = */{ onDeleteItem.invoke(it) },
                                    /*onExpandClick = */{
                                        if (expandedItem == it) {
                                            expandedItem = -1
                                        } else expandedItem = it
                                    },
                                    /*textTitle = */"${it.plus(1)}. ${itemName.replace("_", " ")}"
                                )
                            },
                            expandedItem
                        )
                    }
                }
            }
        }
        if (showDialog || editItem != -1) {
            TableItemDialog(
                name = tableName,
                preState = tableDataList.getOrNull(editItem)?.data,
                sections = sections,
                valueChangeForChild = valueChangeForChild,
                onValueChange = tableConfig.onChangeValue,
                DoneButton = tableConfig.tablePopupButton,
                onDismiss = {
                    showDialog = false
                    editItem = -1
                },
                onDone = {
                    if (editItem != -1) {
                        onItemEdited(it, editItem)
                        editItem = -1
                    } else {
                        onItemAdded(it)
                    }
                    showDialog = false
                }
            )
        }
    }

    private @Composable fun TableItem(
        tableColors: TableColors,
        tableName: String,
        index: Int,
        item: Map<String, Pair<String, String>>,
        SingleItemHeader: @Composable () -> Unit,
        expandedItem: Int
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (expandedItem == index) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                Color.Gray,
                                shape = RoundedCornerShape(responsiveSize(size = 12))
                            )
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(responsiveSize(size = 12))
                            )
                            .padding(top = responsiveHeight(size = 60))
                            .padding(responsiveSize(size = 15))
                ) {
                    item.forEach {
                        DetailItem(
                            modifier = Modifier.fillMaxWidth(),
                            label = it.value.first,
                            value = it.value.second,
                            labelColor = tableColors.itemsLabelColor,
                            valueColor = tableColors.itemsValueColor,
                        )
                    }
                }
            }
            if (SingleItemHeader == null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                color = ComposeFieldTheme.focusedBorderColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(responsiveSize(size = 12))
                            )
                            .padding(responsiveSize(size = 15)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index.plus(1)}. $tableName",
                        color = ComposeFieldTheme.focusedBorderColor,
                        fontSize = responsiveTextSize(size = 15).sp
                    )

                    Row {
                        Image(imageVector = Icons.Default.Edit, contentDescription = "")
                        Spacer(modifier = Modifier.width(responsiveWidth(size = 10)))
                        Image(imageVector = Icons.Default.Delete, contentDescription = "")
                        Spacer(modifier = Modifier.width(responsiveWidth(size = 10)))
                        Image(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = ""
                        )
                    }
                }
            } else {
                SingleItemHeader.invoke()
            }
        }
    }

    @Composable
    fun DetailItem(modifier: Modifier = Modifier, label: String, value: String,labelColor:Color,valueColor:Color) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = label,
                color = labelColor,
                fontSize = responsiveTextSize(size = 15).sp
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = responsiveTextSize(size = 15).sp
            )
        }
    }

    @Composable
    fun TableItemDialog(
        name: String,
        modifier: Modifier = Modifier,
        preState: HashMap<String, List<ComposeFieldStateHolder>>?,
        onValueChange: ((name: String, newValue: String, states: List<ComposeFieldStateHolder>, sectionName: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        DoneButton: (@Composable BoxScope.(onClick: () -> Unit,data:HashMap<String, List<ComposeFieldStateHolder>>) -> Unit)?,
        sections: List<ComposeSectionModule>,
        onDismiss: () -> Unit,
        onDone: (HashMap<String, List<ComposeFieldStateHolder>>) -> Unit,
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Box(modifier=Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(responsiveSize(12))
                        )
                        .align(Alignment.Center)
                        .padding(responsiveSize(size = 10)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    sectionNames.clear()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(responsiveSize(size = 20))
                    ) {
                        Text(
                            text = name,
                            fontSize = responsiveTextSize(size = 18).sp,
                            fontWeight = FontWeight.Medium,
                            color = ComposeFieldTheme.focusedLabelColor,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Image(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .clickable { onDismiss.invoke() }
                        )
                    }
                    Build(
                        modifier = modifier,
                        sections = sections,
                        preState = preState,
                        onValueChange = onValueChange,
                        valueChangeForChild = valueChangeForChild,
                        button = {sectionName,callback->
                            DoneButton?.invoke(
                                this,
                                { onDone.invoke(sectionState) },
                                sectionState
                            )
                        }
                    )
                }
            }
        }
    }
}



@Composable
fun TableItemHeader(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExpandClick: () -> Unit,
    textTitle: String,
    tableColors: TableColors = TableColors(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(responsiveHeight(size = 60))
                .dashedBorder(
                    1.dp,
                    tableColors.headerBorderColor,
                    RoundedCornerShape(responsiveSize(size = 12)),
                    5.dp,
                    5.dp
                )
                .background(
                    color = tableColors.headerBackgroundColor,
                    shape = RoundedCornerShape(responsiveSize(size = 12))
                )
                .clickable { onExpandClick.invoke() }
                .padding(responsiveSize(size = 15)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = textTitle,
            color = tableColors.headerTextColor,
            fontSize = responsiveTextSize(size = 15).sp
        )

        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_edit_table),
                contentDescription = "",
                modifier = Modifier.clickable { onEditClick.invoke() }
            )
            Spacer(modifier = Modifier.width(responsiveWidth(size = 10)))
            Image(
                painter = painterResource(id = R.drawable.ic_delete_table),
                contentDescription = "",
                modifier = Modifier.clickable { onDeleteClick.invoke() }
            )
            Spacer(modifier = Modifier.width(responsiveWidth(size = 10)))
            Image(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "",
                colorFilter = ColorFilter.tint(ComposeFieldTheme.focusedLabelColor),
            )
        }
    }
}
