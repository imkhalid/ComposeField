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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.R
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.MyNavHost
import com.imkhalid.composefield.composeField.TableColors
import com.imkhalid.composefield.composeField.TableConfig
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.navigateToNext
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.composeField.validate
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefield.theme.dashedBorder
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.imkhalid.composefield.composeField.responsiveHeight
import com.imkhalid.composefield.composeField.responsiveSize
import com.imkhalid.composefield.composeField.responsiveTextSize
import com.imkhalid.composefield.composeField.responsiveWidth
import com.imkhalid.composefield.composeField.util.invalidFamily
import com.imkhalid.composefield.composeField.util.updateDependantChildren
import com.imkhalid.composefield.composeField.util.updatedChildValues
import com.imkhalid.composefield.composeField.util.validatedSection
import kotlinx.coroutines.launch
import java.util.Calendar


open class Sections(
    internal val nav: NavHostController,
    private val sectionType: SectionType,
    private val stepSectionContentItem:
    @Composable() (LazyItemScope.(name: String, clickCallback: (sectionName: String) -> Unit) -> Unit)? =
        null,
    private val tabContentItem:
    @Composable() (LazyItemScope.(name: String, isSelected: Boolean, clickCallback: (() -> Unit)?) -> Unit)? =
        null,
    val userCountry:String="2"
) {
    val sectionState: HashMap<String, List<ComposeFieldStateHolder>> = LinkedHashMap()
    val tableData:
        HashMap<String, SnapshotStateList<HashMap<String, List<ComposeFieldStateHolder>>>> =
        HashMap()
    val sectionNames: ArrayList<String> = arrayListOf()


    /**
     * here we are expecting section that can have sub section and sub section will be show in
     * column we will receive single section and look for sub sections and draw them on a screen, a
     * section can have either sub sections or fields that is what we are assuming FamilyData->
     * Family Data only Support if Type is @{SectionType.SIMPLE_VERTICAL} Or @{SectionType.TAB} in
     * SectionType.TAB -> we add Family Detail as a section in Section Names for routing purpose
     */
    @Composable
    fun Build(
        modifier: Modifier = Modifier,
        sections: List<ComposeSectionModule>,
        familyData: FamilyData? = null,
        showTitle: Boolean = false,
        preState: HashMap<String, List<ComposeFieldStateHolder>>? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        onLastPageReach: ((Sections) -> Unit)? = null,
        errorDialog:
            (@Composable
            (onClick: (positive: Boolean) -> Unit, onDismiss: () -> Unit) -> Unit)? =
            null
    ) {
        if (sectionNames.isEmpty()) {
            sections.mapTo(sectionNames) { it.name }
            if (familyData != null && familyData.snapshotStateList.isNotEmpty()) {
                if (familyData.isEditView) {
                    sectionNames.add("Family Details")
                }
            }
            sections.forEach {sec->
                if (sec.isTable) {
                    tableData[sec.name] = SnapshotStateList()
                }
                // Removed else and ReGenerating states as it picks old
                // item state for table in case of TabView
                //in normal table it was working file
                if (sec.fields.isNotEmpty())
                    sectionState[sec.name] = sec.fields.map { field ->
                        val preFieldState =
                            preState?.getOrDefault(sec.name, emptyList())?.find { x ->
                                x.state.field.name == field.name
                            }
                        rememberFieldState(fieldModule = field, stateHolder = preFieldState)
                    }
                else
                    sectionState[sec.name] = sec.subSections.flatMap {
                            it.fields.map { field ->
                                val preFieldState =
                                    preState?.getOrDefault(sec.name, emptyList())?.find { x ->
                                        x.state.field.name == field.name
                                    }
                                rememberFieldState(fieldModule = field, stateHolder = preFieldState)
                            }
                        }

            }
        }
        val myNav = MyNavHost(nav,sectionNames,this,onLastPageReach)
        when (sectionType) {
            SectionType.Simple ->
                SimpleSections(
                    nav = myNav,
                    modifier = modifier,
                    sections = sections,
                    showTitle = showTitle,
                    valueChangeForChild = valueChangeForChild,
                    button = button,
                    onLastPageReach = onLastPageReach,
                    onValueChange = onValueChange
                )
            SectionType.SIMPLE_VERTICAL ->
                SimpleVertical(
                    nav = myNav,
                    modifier = modifier,
                    sections = sections,
                    showTitle = showTitle,
                    familyData = familyData,
                    valueChangeForChild = valueChangeForChild,
                    button = button,
                    onLastPageReach = onLastPageReach,
                    onValueChange = onValueChange
                )
            SectionType.Tab ->
                TabSections(
                    nav = myNav,
                    sections = sections,
                    familyData = familyData,
                    valueChangeForChild = valueChangeForChild,
                    button = button,
                    tableConfig = TableConfig(),
                    onLastPageReach = onLastPageReach
                )
            SectionType.Step ->
                StepsSections(
                    nav = myNav,
                    modifier = modifier,
                    sections = sections,
                    onValueChange = onValueChange,
                    valueChangeForChild = valueChangeForChild,
                    sectionNames = sectionNames,
                    sectionState = sectionState,
                    stepSectionContentItem = stepSectionContentItem,
                    errorDialog = errorDialog
                )
        }
    }

    @Composable
    fun TabBuild(
        modifier: Modifier=Modifier,
        sections: List<ComposeSectionModule>,
        familyData: FamilyData? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        button: @Composable (BoxScope.(onClick: () -> Unit) -> Unit)?,
        tableConfig: TableConfig = TableConfig(),
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
            sections.forEach {
                if (it.isTable) {
                    tableData[it.name] = SnapshotStateList()
                } else {
                    sectionState[it.name] =
                        it.fields.map { field ->
                            rememberFieldState(fieldModule = field, stateHolder = null)
                        }
                }
            }
        }
        TabSections(
            modifier=modifier,
            nav = MyNavHost(nav,sectionNames,this,onLastPageReach),
            sections = sections,
            familyData = familyData,
            onValueChange=onValueChange,
            valueChangeForChild = valueChangeForChild,
            button = button,
            tableConfig = tableConfig,
            onLastPageReach = onLastPageReach
        )
    }

    @Composable
    private fun SimpleSections(
        nav: MyNavHost,
        modifier: Modifier,
        sections: List<ComposeSectionModule>,
        showTitle: Boolean,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)?
    ) {
        Box(modifier = modifier) {
            NavHost(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = responsiveHeight(size = 60)),
                navController = nav.nav,
                startDestination = sections.firstOrNull()?.name ?: ""
            ) {
                sections.forEach { section ->
                    composable(section.name) {
                        LazyColumn {
                            if (section.subSections.isNotEmpty()) {
                                section.subSections.forEachIndexed { itemindex, item ->
                                    buildInnerSection(
                                        parentSectionName=section.name,
                                        section = item,
                                        showSectionName = showTitle,
                                        valueChangeForChild = valueChangeForChild,
                                        onValueChange = onValueChange
                                    )
                                }
                            } else {
                                buildInnerSection(
                                    section = section,
                                    showSectionName = showTitle,
                                    valueChangeForChild = valueChangeForChild,
                                    onValueChange = onValueChange
                                )
                            }
                        }
                    }
                }
            }
            button?.invoke(this) { navigateToNext(nav, onLastPageReach) }
        }
    }

    @Composable
    private fun SimpleVertical(
        nav: MyNavHost,
        modifier: Modifier,
        sections: List<ComposeSectionModule>,
        showTitle: Boolean,
        familyData: FamilyData? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)?
    ) {
        Box(modifier = modifier) {
            LazyColumn(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(bottom = responsiveHeight(size = 60)),
            ) {
                sections.forEach { section ->
                    if (section.subSections.isNotEmpty()) {
                        section.subSections.forEachIndexed { itemindex, item ->
                            buildInnerSection(
                                section = item,
                                showSectionName = showTitle,
                                valueChangeForChild = valueChangeForChild,
                                onValueChange = onValueChange
                            )
                        }
                    } else {
                        buildInnerSection(
                            section = section,
                            showSectionName = showTitle,
                            valueChangeForChild = valueChangeForChild,
                            onValueChange = onValueChange
                        )
                    }
                }
                if (familyData != null) {
                    FamilyForm(familyData = familyData,userCountry=userCountry)
                }
            }
            button?.invoke(this) { navigateToNext(nav, onLastPageReach) }


        }
    }

    internal @Composable fun Tabs(currentSection: String, clickCallback: (() -> Unit)?) {
        val listState = rememberLazyListState()
        // Remember a CoroutineScope to be able to launch
        val coroutineScope = rememberCoroutineScope()
        LazyRow(state = listState,
            modifier =
            Modifier
                .padding(bottom = responsiveHeight(size = 20))
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = responsiveSize(size = 17))
                )
        ) {
            items(sectionNames.size) {
                tabContentItem?.invoke(
                    this@items,
                    sectionNames[it],
                    sectionNames[it] == currentSection,
                    clickCallback
                )
            }
        }
        coroutineScope.launch {
            sectionNames.indexOf(currentSection).takeIf { x->x>=0 }?.let {
                listState.animateScrollToItem(it)
            }
        }
    }

    @Composable
    private fun Build(section: ComposeSectionModule) {

        LazyColumn {
            if (section.subSections.isNotEmpty()) {
                section.subSections.forEachIndexed { itemindex, item ->
                    this@LazyColumn.buildInnerSection(section = item)
                }
            } else {
                buildInnerSection(section = section)
            }
        }
    }
/**
 * parentSectionName is Used to send main section name so it can retrieve state fields*/
    internal fun LazyListScope.buildInnerSection(
        modifier: Modifier = Modifier,
        section: ComposeSectionModule,
        showButton: Boolean = false,
        showSectionName: Boolean = false,
        clickCallback: (() -> Unit)? = null,
        parentSectionName:String?=null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null
    ) {
        item {
            if (showSectionName)
                Text(
                    text = section.name,
                    color = ComposeFieldTheme.focusedLabelColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = responsiveTextSize(size = 15).sp,
                    modifier = Modifier.padding(5.dp)
                )
            section.fields.forEach { field ->
                val sectionsStateList = this@Sections.sectionState[parentSectionName?:section.name]?: emptyList()
                val selectedState =sectionsStateList.find { x ->
                    x.state.field.name == field.name
                }


                val state =
                    sectionState[parentSectionName?:section.name]?.find { x -> x.state.field.name == field.name }
                        ?: rememberFieldState(fieldModule = field, stateHolder = selectedState)

                ComposeFieldBuilder()
                    .Build(
                        userCountry = userCountry,
                        modifier = modifier,
                        stateHolder = state,
                        onValueChange = {name,value->
                            updateDependantChildren(sectionsStateList,state,value)
                            onValueChange?.invoke(name,value)
                        }
                        ,
                        onValueChangeForChild = {
                            valueChangeForChild?.invoke(
                                ChildValueModel(
                                    state.state.field,
                                    it,
                                    childValues = {
                                        updatedChildValues(
                                            state.state.field.childID,
                                            it,
                                            sectionState[section.name] ?: emptyList()
                                        )
                                    }
                                )
                            )
                        }
                    )
            }
            if (showButton)
                Button(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    onClick = { clickCallback?.invoke() }
                ) {
                    Text(text = "Continue")
                }
        }
    }

    @Composable
    fun StepsSections(
        nav: MyNavHost,
        modifier: Modifier,
        sectionNames: List<String>,
        sections: List<ComposeSectionModule>,
        sectionState: HashMap<String, List<ComposeFieldStateHolder>>,
        stepSectionContentItem:
            (@Composable
            LazyItemScope.(name: String, clickCallback: (sectionName: String) -> Unit) -> Unit)?,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        errorDialog:
            (@Composable
            (onClick: (positive: Boolean) -> Unit, onDismiss: () -> Unit) -> Unit)? =
            null
    ) {

        val callback = { str: String -> nav.nav.navigate("CurrentSection/$str") }
        var showDialog by remember { mutableStateOf(false) }

        NavHost(navController = nav.nav, startDestination = "SectionNames") {
            composable("SectionNames") {
                LazyColumn {
                    items(sectionNames.size) {
                        stepSectionContentItem?.invoke(this, sectionNames[it], callback)
                    }
                }
            }
            composable("CurrentSection/{data}") {
                LazyColumn {
                    sections
                        .find { x -> x.name == it.arguments?.getString("data") }
                        ?.let {
                            if (it.subSections.isNotEmpty()) {
                                it.subSections.forEach { subSec ->
                                    buildInnerSection(
                                        modifier = modifier,
                                        section = subSec,
                                        showButton = false,
                                        showSectionName = true,
                                        onValueChange = onValueChange,
                                        valueChangeForChild = valueChangeForChild,
                                    )
                                }
                                item {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(0.7f),
                                        onClick = {
                                            val isValidated =
                                                it.subSections.map { subSec ->
                                                    validatedSection(
                                                        sectionState[subSec.name] ?: emptyList()
                                                    )
                                                }

                                            if (isValidated.all { x -> x }) nav.nav.popBackStack()
                                            else showDialog = true
                                        }
                                    ) {
                                        Text(text = "Continue")
                                    }
                                }
                            } else {
                                buildInnerSection(
                                    modifier = modifier,
                                    section = it,
                                    showButton = true,
                                    onValueChange = onValueChange,
                                    valueChangeForChild = valueChangeForChild,
                                    clickCallback = {
                                        val isValidated =
                                            validatedSection(sectionState[it.name] ?: emptyList())
                                        if (isValidated) nav.nav.popBackStack() else showDialog = true
                                    }
                                )
                            }
                        }
                }
                if (showDialog)
                    errorDialog?.invoke(
                        { showDialog = false },
                        { showDialog = false }
                    )
            }
        }
    }
}

@Composable
private fun Sections.TabSections(
    modifier: Modifier = Modifier,
    nav: MyNavHost,
    sections: List<ComposeSectionModule>,
    familyData: FamilyData?,
    onValueChange: ((name: String, newValue: String) -> Unit)? = null,
    valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
    button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)?,
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
            Tabs(currentSection) {}

            NavHost(navController = nav.nav, startDestination = sections.firstOrNull()?.name ?: "") {
                sections.forEach { section ->
                    composable(section.name) {
                        currentSection = section.name
                        if (section.isTable) {

                            TableSection(
                                    nav = rememberNavController(),
                                    sectionType = SectionType.SIMPLE_VERTICAL,
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
                                    onItemAdded = {
                                        tableData
                                            .getOrDefault(section.name, SnapshotStateList())
                                            .apply { add(it) }
                                    },
                                    onDeleteItem = {
                                        tableData
                                            .getOrDefault(section.name, SnapshotStateList())
                                            .apply { removeAt(it) }
                                    },
                                    onItemEdited = {
                                        hashMap: HashMap<String, List<ComposeFieldStateHolder>>,
                                        i: Int ->
                                        tableData
                                            .getOrDefault(section.name, SnapshotStateList())
                                            .apply {
                                                removeAt(i)
                                                add(i, hashMap)
                                            }
                                    },
                                    valueChangeForChild = valueChangeForChild,
                                    onValueChange = onValueChange,
                                    SingleItemHeader = {
                                        onEditClick,
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
                        LazyColumn { FamilyForm(userCountry = userCountry, familyData = familyData) }
                    }
            }
        }
        button?.invoke(this) {
            val currentSectionOf = sections.find { x->x.name==currentSection }
            if (currentSection=="Family Details" && invalidFamily(familyData).isNotEmpty()){
                invalidFamily(familyData).firstOrNull()?.let {
                    familyExpandItem.value=it
                }
            } else if (currentSectionOf?.isTable == true) {

                val dataList = tableData.getOrDefault(currentSection, SnapshotStateList())
                if (
                    (dataList.size) >= currentSectionOf.min && dataList.size <= currentSectionOf.max
                ) {
                    navigateToNext(nav, onLastPageReach)
                }
            } else {
                if (
                    sectionState
                        .getOrDefault(currentSection, emptyList())
                        .validate(showError = true)
                ) {
                    navigateToNext(nav, onLastPageReach)
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