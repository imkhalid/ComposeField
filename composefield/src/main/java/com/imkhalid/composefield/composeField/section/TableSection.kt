package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.ozonedDigital.jhk.ui.common.responsiveHeight
import com.ozonedDigital.jhk.ui.common.responsiveSize
import com.ozonedDigital.jhk.ui.common.responsiveTextSize
import com.ozonedDigital.jhk.ui.common.responsiveWidth

class TableSection(
    parentNav: NavHostController,
    nav: NavHostController,
    sectionType: SectionType,
    var min:Int=0,
    var max:Int = 1,
) : Sections(parentNav, nav, sectionType) {

    @Composable
    fun TableBuild(
        modifier: Modifier = Modifier,
        sections: List<ComposeSectionModule>,
        tableName: String,
        description: String,
        tableDataList: SnapshotStateList<HashMap<String, List<ComposeFieldStateHolder>>> = SnapshotStateList(),
        preState: HashMap<String, List<ComposeFieldStateHolder>>? = null,
        onItemAdded: (HashMap<String, List<ComposeFieldStateHolder>>) -> Unit,
        onDeleteItem: (index: Int) -> Unit,
        onItemEdited: (HashMap<String, List<ComposeFieldStateHolder>>,index:Int) -> Unit,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        AddButton: (@Composable ColumnScope.(onClick: () -> Unit) -> Unit)? = null,
        DoneButton: (@Composable ColumnScope.(onClick: () -> Unit) -> Unit)? = null,
        SingleItemHeader: @Composable (onEditClick: () -> Unit, onDeleteClick: () -> Unit, onExpandClick: () -> Unit, textTitle: String) -> Unit,
        errorDialog: (@Composable (
            onClick: (positive: Boolean) -> Unit,
            onDismiss: () -> Unit
        ) -> Unit)? = null
    ) {
        if (sectionNames.isEmpty()) {
            sections.mapTo(sectionNames) {
                it.name
            }
            sections.forEach {
                sectionState[it.name] = it.fields.map { field ->
                    val preFieldState = preState?.getOrDefault(it.name, emptyList())
                        ?.find { x -> x.state.field.name == field.name }
                    rememberFieldState(
                        fieldModule = field,
                        stateHolder = preFieldState
                    )
                }
            }
        }
        var showDialog by remember {
            mutableStateOf(false)
        }

        var editItem by remember {
            mutableStateOf(-1)
        }
        var expandedItem by remember {
            mutableStateOf(-1)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(responsiveSize(size = 20))
        ) {
            Text(
                text = tableName,
                fontSize = responsiveTextSize(size = 16).sp
            )
            Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))
            Text(
                text = description,
                fontSize = responsiveTextSize(size = 13).sp,
                color = ComposeFieldTheme.textColor,
                minLines = 2
            )
            if (tableDataList.size<max) {
                AddButton?.invoke(this) {
                    showDialog = true
                }
            }

            Spacer(modifier = Modifier.height(responsiveHeight(size = 10)))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(tableDataList.size) {
                    val item = HashMap<String, Pair<String, String>>().apply {
                        tableDataList[it].forEach { x ->
                            x.value.forEach { y ->
                                put(y.state.field.name, Pair(y.state.field.label, y.state.text))
                            }
                        }
                    }
                    TableItem(
                        tableName,
                        it,
                        item,
                        SingleItemHeader = {
                            SingleItemHeader.invoke(
                                onEditClick = {
                                    editItem = it
                                }, onDeleteClick = {
                                    onDeleteItem.invoke(it)
                                }, onExpandClick = {
                                    expandedItem = it
                                },
                                textTitle = "${it.plus(1)}. $tableName"
                            )
                        },
                        expandedItem
                    )
                }
            }
        }
        if (showDialog || editItem != -1) {
            TableItemDialog(
                preState = tableDataList.getOrNull(editItem),
                sections = sections,
                valueChangeForChild = valueChangeForChild,
                onValueChange = onValueChange,
                DoneButton = DoneButton ?: AddButton,
                onDismiss = { showDialog = false },
                onDone = {
                    if (editItem != -1) {
                        onItemEdited(it,editItem)
                        editItem=-1
                    } else {
                        onItemAdded(it)
                    }
                    showDialog = false
                })
        }
    }

    private @Composable
    fun TableItem(
        tableName: String,
        index: Int,
        item: Map<String, Pair<String, String>>,
        SingleItemHeader: @Composable () -> Unit,
        expandedItem: Int
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = responsiveHeight(size = 10))
        ) {
            if (expandedItem == index) {
                Column(
                    modifier = Modifier
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
                        .padding(top = responsiveHeight(size = 35))
                        .padding(responsiveSize(size = 15))
                ) {
                    item.forEach {
                        DetailItem(
                            modifier = Modifier.fillMaxWidth(),
                            label = it.value.first,
                            value = it.value.second
                        )
                    }
                }
            }
            if (SingleItemHeader == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = ComposeFieldTheme.focusedBorderColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(
                                responsiveSize(size = 12)
                            )
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
    fun DetailItem(modifier: Modifier = Modifier, label: String, value: String) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = label,
                color = ComposeFieldTheme.textColor.copy(alpha = 0.7f),
                fontSize = responsiveTextSize(size = 15).sp
            )
            Text(
                text = value,
                color = ComposeFieldTheme.textColor,
                fontSize = responsiveTextSize(size = 15).sp
            )
        }
    }

    @Composable
    fun TableItemDialog(
        modifier: Modifier = Modifier,
        preState: HashMap<String, List<ComposeFieldStateHolder>>?,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        DoneButton: (@Composable ColumnScope.(onClick: () -> Unit) -> Unit)?,
        sections: List<ComposeSectionModule>,
        onDismiss: () -> Unit,
        onDone: (HashMap<String, List<ComposeFieldStateHolder>>) -> Unit,
    ) {
        Dialog(onDismissRequest = onDismiss) {
            Column(
                Modifier
                    .background(color = Color.White, shape = RoundedCornerShape(12))
                    .padding(responsiveSize(size = 10)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                sectionNames.clear()
                Build(
                    modifier = modifier,
                    sections = sections,
                    preState = preState,
                    onValueChange = onValueChange,
                    valueChangeForChild = valueChangeForChild
                )
                DoneButton?.invoke(this) {
                    onDone.invoke(
                        sectionState
                    )
                }
            }
        }
    }
}