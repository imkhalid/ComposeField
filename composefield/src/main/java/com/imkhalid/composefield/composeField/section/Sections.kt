package com.imkhalid.composefield.composeField.section

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
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
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.rememberFieldState
import com.imkhalid.composefieldproject.composeField.fields.ComposeFieldBuilder
import com.ozonedDigital.jhk.ui.common.responsiveTextSize

class Sections(
    val parentNav: NavHostController,
    val sectionType: SectionType,
    val stepSectionContentItem: (@Composable LazyItemScope.(name: String, clickCallback: (sectionName: String) -> Unit) -> Unit)? = null
) {
    val sectionState: HashMap<String, List<ComposeFieldStateHolder>> = HashMap()
    val sectionNames: ArrayList<String> = arrayListOf()
    var currentSectionIndex = 0

    /** here we are expecting section that can have sub section and sub section will be show in column
    we will receive single section and look for sub sections and draw them on a screen,
    a section can have either sub sections or fields that is what we are assuming*/
    @Composable
    fun Build(
        modifier: Modifier=Modifier,
        sections: List<ComposeSectionModule>,
        errorDialog: (@Composable (
            onClick:(positive:Boolean)->Unit,
            onDismiss:()->Unit
        ) -> Unit)?=null
    ) {
        var showDialog by remember {
            mutableStateOf(false)
        }
        sections.mapTo(sectionNames) {
            it.name
        }
        when (sectionType) {
            SectionType.Simple -> SimpleSections(sections = sections)
            SectionType.Step -> StepsSections(modifier=modifier,sections = sections){
                showDialog=true
            }
        }

        if (showDialog)
            errorDialog?.invoke(onClick={
                          showDialog=false
            },onDismiss={
                showDialog=false
            })


    }

    @Composable
    private fun SimpleSections(sections: List<ComposeSectionModule>) {
        val navController = rememberNavController()
        Column {
            NavHost(
                navController = navController,
                startDestination = sections.firstOrNull()?.name ?: ""
            ) {
                sections.forEach { section ->
                    composable(section.name) {
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
                }
            }
            Button(onClick = {
                navigateToNext(navController)
            }) {
                Text(text = "Next Section")
            }

            BackHandler {
                if (currentSectionIndex != 0) {
                    --currentSectionIndex
                    navController.popBackStack()
                } else {
                    parentNav.popBackStack()
                }

            }
        }
    }

    @Composable
    fun StepsSections(modifier: Modifier,sections: List<ComposeSectionModule>,erroCallback:()->Unit) {
        val nav = rememberNavController()
        val callback = { str: String ->
            nav.navigate("CurrentSection/$str")
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
                            modifier=modifier,
                            section = it,
                            stateList = sectionState[it.name] ?: emptyList(),
                            showButton = true
                        ){
                            val isValidated = validatedSection(sectionState[it.name]?: emptyList())
                            if (isValidated)
                                nav.popBackStack()
                            else
                                erroCallback.invoke()

                        }
                    }
                }
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
        clickCallback: (() -> Unit)? = null
    ): List<ComposeFieldStateHolder> {
        val sectionState: ArrayList<ComposeFieldStateHolder> = arrayListOf()
        LazyColumn {
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
                        )
                    sectionState.add(state)

                }

            }
            if (showButton)
                item {
                    Button(
                        modifier=Modifier
                            .fillMaxWidth(0.7f),
                        onClick = {clickCallback?.invoke()}) {
                        Text(text = "Continue")
                    }
                }
        }
        return sectionState
    }

    private fun navigateToNext(navController: NavHostController) {
        if (currentSectionIndex < sectionNames.lastIndex) {
            navController.navigate(sectionNames[++currentSectionIndex])
        } else {
//            lastPage.invoke()
        }
    }

    private fun validatedSection(sectionState:List<ComposeFieldStateHolder>):Boolean{
        return sectionState.all { x->
            x.state.field.required==ComposeFieldYesNo.YES &&
                    x.state.text.isNotEmpty() &&
                    x.state.hasError.not()
        }
    }
}