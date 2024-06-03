package com.imkhalid.composefield.composeField.section

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefield.model.DefaultValues
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.ozonedDigital.jhk.ui.common.responsiveHeight
import com.ozonedDigital.jhk.ui.common.responsiveSize
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

class Sections(
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
        button: (@Composable ColumnScope.(onClick: () -> Unit) -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        onLastPageReach: ((Sections) -> Unit)? = null,
        errorDialog: (@Composable (
            onClick: (positive: Boolean) -> Unit,
            onDismiss: () -> Unit
        ) -> Unit)? = null
    ) {
        if (sectionNames.isEmpty())
            sections.mapTo(sectionNames) {
                it.name
            }
        when (sectionType) {
            SectionType.Simple -> SimpleSections(
                nav = nav,
                sections = sections,
                valueChangeForChild = valueChangeForChild,
                button = button,
                onLastPageReach = onLastPageReach
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
                valueChangeForChild = valueChangeForChild,
                errorDialog = errorDialog
            )
        }

    }

    @Composable
    private fun SimpleSections(
        nav: NavHostController,
        sections: List<ComposeSectionModule>,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable ColumnScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null
    ) {
        Column {
            NavHost(
                navController = nav,
                startDestination = sections.firstOrNull()?.name ?: ""
            ) {
                sections.forEach { section ->
                    composable(section.name) {
                        if (section.subSections.isNotEmpty()) {
                            Column {
                                section.subSections.forEachIndexed { itemindex, item ->
                                    sectionState[item.name] = buildInnerSection(
                                        section = item,
                                        valueChangeForChild = valueChangeForChild
                                    )
                                }
                            }
                        } else {
                            sectionState[section.name] = buildInnerSection(
                                section = section,
                                valueChangeForChild = valueChangeForChild
                            )
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
    private fun TabSections(
        nav: NavHostController,
        sections: List<ComposeSectionModule>,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
        button: (@Composable ColumnScope.(onClick: () -> Unit) -> Unit)?,
        onLastPageReach: ((Sections) -> Unit)? = null
    ) {
        var currentSection by remember {
            mutableStateOf(sections[currentSectionIndex].name ?: "")
        }
        Column {
            Tabs(currentSection) {

            }
            NavHost(
                navController = nav,
                startDestination = sections.firstOrNull()?.name ?: ""
            ) {
                sections.forEach { section ->
                    composable(section.name) {
                        if (section.subSections.isNotEmpty()) {
                            Column {
                                section.subSections.forEachIndexed { itemindex, item ->
                                    sectionState[item.name] = buildInnerSection(
                                        section = item,
                                        valueChangeForChild = valueChangeForChild
                                    )
                                }
                            }
                        } else {
                            sectionState[section.name] = buildInnerSection(
                                section = section,
                                valueChangeForChild = valueChangeForChild
                            )
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
    fun StepsSections(
        nav: NavHostController,
        modifier: Modifier,
        sections: List<ComposeSectionModule>,
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
                Column {
                    sections.find { x -> x.name == it.arguments?.getString("data") }?.let {
                        sectionState[it.name] = buildInnerSection(
                            modifier = modifier,
                            section = it,
                            stateList = sectionState[it.name] ?: emptyList(),
                            showButton = true,
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

    @Composable
    private fun Build(section: ComposeSectionModule) {

        if (section.subSections.isNotEmpty()) {
            Column {
                section.subSections.forEachIndexed { itemindex, item ->
                    sectionState[item.name] = buildInnerSection(section = item)
                }
            }
        } else {
            sectionState[section.name] = buildInnerSection(section = section)
        }
    }

    @Composable
    private fun buildInnerSection(
        modifier: Modifier = Modifier,
        section: ComposeSectionModule,
        stateList: List<ComposeFieldStateHolder> = emptyList(),
        showButton: Boolean = false,
        showSectionName: Boolean = false,
        clickCallback: (() -> Unit)? = null,
        valueChangeForChild: ((childValueMode: ChildValueModel) -> Unit)? = null,
    ): List<ComposeFieldStateHolder> {

        val sectionState: ArrayList<ComposeFieldStateHolder> = arrayListOf()
        LazyColumn {
            if (showSectionName)
                item {
                    Text(
                        text = section.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontSize = responsiveTextSize(size = 15).sp,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            section.fields.forEach { field ->
                item {
                    val selectedState = stateList.find { x -> x.state.field.name == field.name }
                    val state = rememberFieldState(
                        fieldModule = field,
                        stateHolder = selectedState
                    )
                    ComposeFieldBuilder()
                        .Build(
                            modifier = modifier,
                            stateHolder = state,
                            onValueChangeForChild = {
                                valueChangeForChild?.invoke(
                                    ChildValueModel(
                                        state.state.field,
                                        it,
                                        childValues = {
                                            updatedChildValues(
                                                state.state.field.childID,
                                                it,
                                                sectionState
                                            )
                                        }
                                    )
                                )
                            }
                        )
                    sectionState.add(state)

                }

            }
            if (showButton)
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        onClick = { clickCallback?.invoke() }) {
                        Text(text = "Continue")
                    }
                }
        }
        return sectionState
    }

    private fun updatedChildValues(
        childID: String,
        newValues: List<DefaultValues>,
        sectionState: java.util.ArrayList<ComposeFieldStateHolder>
    ) {
        sectionState.find { x -> x.state.field.id == childID }?.let {
            it.updatedFieldDefaultValues(newValues)
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


    private fun validatedSection(sectionState: List<ComposeFieldStateHolder>): Boolean {
        return sectionState.all { x ->
            x.state.field.required == ComposeFieldYesNo.YES &&
                    x.state.text.isNotEmpty() &&
                    x.state.hasError.not()
        }
    }
}