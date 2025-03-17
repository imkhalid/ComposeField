package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.MyNavHost
import com.imkhalid.composefield.composeField.TableConfig
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.model.TaggedMap
import com.imkhalid.composefield.composeField.navigateToNext
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.composeField.responsiveHeight
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.util.invalidFamily
import com.imkhalid.composefield.composeField.util.validate
import kotlinx.coroutines.launch

@Composable
fun Sections.TabBuild(
    nav: MyNavHost,
    modifier: Modifier = Modifier,
    sections: List<ComposeSectionModule>,
    familyData: FamilyData? = null,
    onValueChange: ((name: String, newValue: String) -> Unit)? = null,
    button: @Composable (BoxScope.(currentSectionName:String,onClick: () -> Unit) -> Unit)?,
    tableConfig: TableConfig,
    valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
    onLastPageReach: ((Sections) -> Unit)? = null,
) {
    if (sectionNames.isEmpty()) {
        sections.mapTo(sectionNames) { it.name }
        if (familyData != null && familyData.snapshotStateList.isNotEmpty()) {
            if (familyData.isEditView) {
                sectionNames.add("Family Details")
            }
        }
        sections.forEach {sc->
            if (sc.isTable) {
                tableData[sc.name] = SnapshotStateList<TaggedMap>().apply {
                    sc.prefilledTableData?.onEach {singleRow->
                        var id = ""
                        val list =singleRow.keys.mapNotNull {key->
                            val value = singleRow.getOrDefault(key,"")
                            if (value.isNotEmpty()){
                                if (key =="_id"|| key=="id") {
                                    id=value
                                    null
                                }else {
                                    sc.fields.find { x -> x.name == key }?.let { fiel ->
                                        rememberFieldState(
                                            fieldModule = fiel.copy(
                                                value = value,
                                                hideInitial = ComposeFieldYesNo.NO,
                                                hidden = ComposeFieldYesNo.NO
                                            ),
                                            stateHolder = null
                                        )
                                    }

                                }
                            }else null
                        }
                        this.add(TaggedMap(data = hashMapOf(sc.name to list), tag = id))
                    }
                }
            } else {
                sectionState[sc.name] =
                    sc.fields.map { field ->
                        rememberFieldState(fieldModule = field, stateHolder = null)
                    }
            }
        }
    }
    TabSections(
        modifier = modifier,
        nav = nav,
        sections = sections,
        familyData = familyData,
        onValueChange = onValueChange,
        valueChangeForChild = valueChangeForChild,
        button = button,
        tableConfig = tableConfig,
        onLastPageReach = onLastPageReach
    )
}

@Composable
private fun Sections.TabSections(
    modifier: Modifier = Modifier,
    nav: MyNavHost,
    sections: List<ComposeSectionModule>,
    familyData: FamilyData?,
    onValueChange: ((name: String, newValue: String) -> Unit)? = null,
    valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
    button: (@Composable BoxScope.(currentSectionName:String,onClick: () -> Unit) -> Unit)?,
    tableConfig: TableConfig = TableConfig(),
    onLastPageReach: ((Sections) -> Unit)? = null
) {
    var currentSection by remember { mutableStateOf("") }
    var familyExpandItem = remember {
        mutableIntStateOf(-1)
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = responsiveHeight(size = 60)),
        ) {
            Tabs(sectionNames, currentSection, sectionType) {}

            NavHost(
                navController = nav.nav,
                startDestination = sections.firstOrNull()?.name ?: ""
            ) {
                sections.forEach { section ->
                    composable(section.name) {
                        currentSection = section.name
                        if (section.isTable) {

                            TableSection(
                                nav = rememberNavController(),
                                sectionType = SectionType.SIMPLE(false),
                                min = section.min,
                                max = section.max
                            )
                                .TableBuild(
                                    modifier = Modifier,
                                    sections = listOf(section),
                                    tableConfig = tableConfig,
                                    tableName = section.name.replace("_", " "),
                                    description = "",
                                    tableDataList =
                                    tableData.getOrDefault(section.name, SnapshotStateList()),
                                    preState = null,
                                    onItemAdded = { data ->
                                        //Here First Filter only fields that are not hidden so it does not show empty values
                                        val map = data.mapValues {
                                            it.value.filter { x -> x.state.field.hidden == ComposeFieldYesNo.NO }
                                        }
                                        tableData
                                            .getOrDefault(section.name, SnapshotStateList())
                                            .apply { add(TaggedMap(HashMap(map))) }
                                    },
                                    onDeleteItem = {ind->
                                        tableData
                                            .getOrDefault(section.name, SnapshotStateList())
                                            .apply {
                                                val data = getOrNull(ind)
                                                data?.let {d->
                                                    removeAt(ind)
                                                    add(ind,d.copy(isDeleted = true))
                                                }
                                            }
                                    },
                                    onItemEdited = { hashMap: Map<String, List<ComposeFieldStateHolder>>,
                                                     i: Int ->
                                        tableData
                                            .getOrDefault(section.name, SnapshotStateList())
                                            .apply {
                                                val id = get(i).tag
                                                removeAt(i)
                                                add(i, TaggedMap(tag = id, data = java.util.HashMap(hashMap)))
                                            }
                                    },
                                    valueChangeForChild = valueChangeForChild,
                                    onValueChange = onValueChange,
                                    SingleItemHeader = { onEditClick,
                                                         onDeleteClick,
                                                         onExpandClick,
                                                         textTitle ->
                                        TableItemHeader(
                                            onEditClick = onEditClick,
                                            onDeleteClick = onDeleteClick,
                                            onExpandClick = onExpandClick,
                                            textTitle = textTitle,
                                            tableColors = tableConfig.tableColors,
                                        )
                                    },
                                )
                        } else {
                            LazyColumn {
                                if (section.subSections.isNotEmpty()) {
                                    section.subSections.forEachIndexed { itemindex, item ->
                                        this.buildInnerSection(
                                            section = item,
                                            onValueChange = onValueChange,
                                            valueChangeForChild = valueChangeForChild
                                        )
                                    }
                                } else {
                                    buildInnerSection(
                                        section = section,
                                        onValueChange = onValueChange,
                                        valueChangeForChild = valueChangeForChild
                                    )
                                }
                            }
                        }
                    }
                }
                if (familyData != null && familyData.snapshotStateList.isNotEmpty())
                    composable("Family Details") {
                        currentSection = "Family Details"
                        LazyColumn {
                            FamilyForm(
                                userCountry = userCountry,
                                familyData = familyData
                            )
                        }
                    }
            }
        }
        button?.invoke(this,currentSection) {
            val currentSectionOf = sections.find { x -> x.name == currentSection }
            if (currentSection == "Family Details" && invalidFamily(familyData).isNotEmpty()) {
                invalidFamily(familyData).firstOrNull()?.let {
                    familyExpandItem.value = it
                }
            } else if (currentSectionOf?.isTable == true) {

                val dataList = tableData.getOrDefault(currentSection, SnapshotStateList()).filter { x->x.isDeleted.not() }
                if (
                    (dataList.size) >= currentSectionOf.min && dataList.size <= currentSectionOf.max
                ) {
                    if(sectionType is SectionType.TAB ){
                        sectionType.sectionValidated?.invoke()
                    }
                    navigateToNext(nav, onLastPageReach)
                }
            } else {
                if (
                    sectionState
                        .getOrDefault(currentSection, emptyList())
                        .validate(showError = true)
                ) {
                    if(sectionType is SectionType.TAB ){
                        sectionType.sectionValidated?.invoke()
                    }
                    navigateToNext(nav, onLastPageReach)
                }
            }
        }

    }
}


@Composable
private fun Tabs(
    sectionNames: List<String>,
    currentSection: String,
    sectionType: SectionType,
    clickCallback: (() -> Unit)?
) {
    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()
    LazyRow(
        state = listState,
        modifier =
        Modifier
            .padding(bottom = responsiveHeight(size = 20))
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = responsiveSize(size = 17))
            )
    ) {
        items(sectionNames.size) {
            if (sectionType is SectionType.TAB) {
                sectionType.tabContentItem?.invoke(
                    this@items,
                    sectionNames[it],
                    sectionNames[it] == currentSection,
                    clickCallback
                )
            }
        }
    }
    coroutineScope.launch {
        sectionNames.indexOf(currentSection).takeIf { x -> x >= 0 }?.let {
            listState.animateScrollToItem(it)
        }
    }
}