package com.imkhalid.composefield.composeField.section

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.model.DefaultValues
import com.imkhalid.composefield.theme.ComposeFieldTheme
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.ozonedDigital.jhk.ui.common.responsiveHeight
import com.ozonedDigital.jhk.ui.common.responsiveSize
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

open class Sections(
    private val parentNav: NavHostController,
    private val nav: NavHostController,
    private val sectionType: SectionType,
    private val stepSectionContentItem: (@Composable LazyItemScope.(name: String, clickCallback: (sectionName: String) -> Unit) -> Unit)? = null,
    private val tabContentItem: (@Composable LazyItemScope.(name: String, isSelected: Boolean, clickCallback: (() -> Unit)?) -> Unit)? = null,

    ) {
    val sectionState: HashMap<String, List<ComposeFieldStateHolder>> = HashMap()
    val sectionNames: ArrayList<String> = arrayListOf()
    var currentSectionIndex = 0

    /** here we are expecting section that can have sub section and sub section will be show in column
    we will receive single section and look for sub sections and draw them on a screen,
    a section can have either sub sections or fields that is what we are assuming*/
    @Composable
    fun Build(
        modifier: Modifier = Modifier,
        sections: List<ComposeSectionModule>,
        showTitle: Boolean = false,
        preState: HashMap<String, List<ComposeFieldStateHolder>>? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        onLastPageReach: ((Sections) -> Unit)? = null,
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
        when (sectionType) {
            SectionType.Simple -> SimpleSections(
                nav = nav,
                sections = sections,
                showTitle = showTitle,
                valueChangeForChild = valueChangeForChild,
                button = button,
                onLastPageReach = onLastPageReach,
                onValueChange = onValueChange
            )

            SectionType.SIMPLE_VERTICAL -> SimpleVertical(
                nav = nav,
                sections = sections,
                showTitle = showTitle,
                valueChangeForChild = valueChangeForChild,
                button = button,
                onLastPageReach = onLastPageReach,
                onValueChange = onValueChange
            )

            SectionType.Tab -> TabSections(
                nav = nav,
                sections = sections,
                valueChangeForChild = valueChangeForChild,
                button = button,
                onLastPageReach = onLastPageReach
            )

            SectionType.Step -> StepsSections(
                nav = nav,
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
    private fun SimpleSections(
        nav: NavHostController,
        sections: List<ComposeSectionModule>,
        showTitle: Boolean,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)?
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = responsiveHeight(size = 60)),
                navController = nav,
                startDestination = sections.firstOrNull()?.name ?: ""
            ) {
                sections.forEach { section ->
                    composable(section.name) {
                        LazyColumn {
                            if (section.subSections.isNotEmpty()) {
                                section.subSections.forEachIndexed { itemindex, item ->
                                    buildInnerSection(
                                        section = item,
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
            button?.invoke(this) {
                navigateToNext(nav, onLastPageReach)
            }

            BackHandler {
                if (currentSectionIndex != 0) {
                    --currentSectionIndex
                    nav.popBackStack()
                } else {
                    parentNav.popBackStack()
                }

            }
        }
    }

    @Composable
    private fun SimpleVertical(
        nav: NavHostController,
        sections: List<ComposeSectionModule>,
        showTitle: Boolean,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)?
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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

            }
            button?.invoke(this) {
                navigateToNext(nav, onLastPageReach)
            }

            BackHandler {
                if (currentSectionIndex != 0) {
                    --currentSectionIndex
                    nav.popBackStack()
                } else {
                    parentNav.popBackStack()
                }

            }
        }
    }

    @Composable
    private fun TabSections(
        nav: NavHostController,
        sections: List<ComposeSectionModule>,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable BoxScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null
    ) {
        var currentSection by remember {
            mutableStateOf(sections[currentSectionIndex].name ?: "")
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = responsiveHeight(size = 60)),
            ) {
                Tabs(currentSection) {

                }
                NavHost(
                    navController = nav,
                    startDestination = sections.firstOrNull()?.name ?: ""
                ) {
                    sections.forEach { section ->
                        composable(section.name) {
                            LazyColumn {
                                if (section.subSections.isNotEmpty()) {
                                    section.subSections.forEachIndexed { itemindex, item ->
                                        this.buildInnerSection(
                                            section = item,
                                            valueChangeForChild = valueChangeForChild
                                        )
                                    }
                                } else {
                                    buildInnerSection(
                                        section = section,
                                        valueChangeForChild = valueChangeForChild
                                    )
                                }
                            }
                        }
                    }
                }
            }
            button?.invoke(this) {
                navigateToNext(nav, onLastPageReach)
                currentSection = sectionNames[currentSectionIndex]
            }

            BackHandler {
                if (currentSectionIndex != 0) {
                    --currentSectionIndex
                    nav.popBackStack()
                    currentSection = sectionNames[currentSectionIndex]
                } else {
                    parentNav.popBackStack()
                }

            }
        }
    }

    private @Composable
    fun Tabs(currentSection: String, clickCallback: (() -> Unit)?) {
        LazyRow(
            modifier = Modifier
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

    private fun navigateToNext(
        navController: NavHostController,
        lastPage: ((Sections) -> Unit)? = null
    ) {
        if (currentSectionIndex < sectionNames.lastIndex) {
            navController.navigate(sectionNames[++currentSectionIndex])
        } else {
            lastPage?.invoke(this)
        }
    }

    private fun LazyListScope.buildInnerSection(
        modifier: Modifier = Modifier,
        section: ComposeSectionModule,
        showButton: Boolean = false,
        showSectionName: Boolean = false,
        clickCallback: (() -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null
    ) {
        item {
            if (showSectionName)
                Text(
                    text = section.name,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = responsiveTextSize(size = 15).sp,
                    modifier = Modifier.padding(5.dp)
                )
            section.fields.forEach { field ->
                val selectedState =
                    this@Sections.sectionState[section.name]?.find { x -> x.state.field.name == field.name }

//                val state = rememberFieldState(
//                    fieldModule = field,
//                    stateHolder = selectedState
//                )
                val state = sectionState[section.name]
                    ?.find { x -> x.state.field.name == field.name }
                    ?: rememberFieldState(
                        fieldModule = field,
                        stateHolder = selectedState
                    )

                ComposeFieldBuilder()
                    .Build(
                        modifier = modifier,
                        stateHolder = state,
                        onValueChange = onValueChange,
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
                    modifier = Modifier
                        .fillMaxWidth(0.7f),
                    onClick = { clickCallback?.invoke() }) {
                    Text(text = "Continue")
                }
        }
    }

    internal fun updatedChildValues(
        childID: String,
        newValues: List<DefaultValues>,
        sectionState: List<ComposeFieldStateHolder>
    ) {
        val childs = childID.split(",")
        childs.forEach {
            sectionState.find { x -> x.state.field.id == it }?.let {
                it.updatedFieldDefaultValues(newValues)
            }
        }
    }

    internal fun validatedSection(sectionState: List<ComposeFieldStateHolder>): Boolean {
        return sectionState.all { x ->
            x.state.field.required == ComposeFieldYesNo.YES &&
                    x.state.text.isNotEmpty() &&
                    x.state.hasError.not()
        }
    }


    @Composable
    fun StepsSections(
        nav: NavHostController,
        modifier: Modifier,
        sectionNames: List<String>,
        sections: List<ComposeSectionModule>,
        sectionState: HashMap<String, List<ComposeFieldStateHolder>>,
        stepSectionContentItem: (@Composable LazyItemScope.(name: String, clickCallback: (sectionName: String) -> Unit) -> Unit)?,
        onValueChange: ((name: String, newValue: String) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        errorDialog: (@Composable (
            onClick: (positive: Boolean) -> Unit,
            onDismiss: () -> Unit
        ) -> Unit)? = null
    ) {

        val callback = { str: String ->
            nav.navigate("CurrentSection/$str")
        }
        var showDialog by remember {
            mutableStateOf(false)
        }

        NavHost(navController = nav, startDestination = "SectionNames") {
            composable("SectionNames") {
                LazyColumn {
                    items(sectionNames.size) {
                        stepSectionContentItem?.invoke(
                            this,
                            sectionNames[it],
                            callback
                        )
                    }
                }
            }
            composable("CurrentSection/{data}") {
                LazyColumn {
                    sections.find { x -> x.name == it.arguments?.getString("data") }?.let {
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
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f),
                                    onClick = {
                                        val isValidated =
                                            it.subSections.map { subSec ->
                                                validatedSection(
                                                    sectionState[subSec.name] ?: emptyList()
                                                )
                                            }

                                        if (isValidated.all { x -> x })
                                            nav.popBackStack()
                                        else
                                            showDialog = true
                                    }) {
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
                                    if (isValidated)
                                        nav.popBackStack()
                                    else
                                        showDialog = true
                                }
                            )
                        }
                    }
                }
                if (showDialog)
                    errorDialog?.invoke(
                        onClick = {
                            showDialog = false
                        },
                        onDismiss = {
                            showDialog = false
                        }
                    )
            }
        }

    }

}

fun getFieldByFieldName(
    name: String,
    state: HashMap<String, List<ComposeFieldStateHolder>>
): ComposeFieldStateHolder? {
    for (i in 0 until state.entries.size) {
        val foundState =
            state.entries.toList().get(i).value.find { x -> x.state.field.name == name }
        if (foundState != null) {
            return foundState
        }
    }
    return null
}


