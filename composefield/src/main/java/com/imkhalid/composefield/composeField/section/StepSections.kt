package com.imkhalid.composefield.composeField.section

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.MyNavHost
import com.imkhalid.composefield.composeField.model.ChildValueModel
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.model.SectionState
import com.imkhalid.composefield.composeField.util.validatedSection

@Composable
internal fun Sections.StepsSections(
    nav: MyNavHost,
    modifier: Modifier,
    sectionNames: List<String>,
    sections: List<ComposeSectionModule>,
    sectionState: List<SectionState>,
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
                                                validatedSection(sectionState.find { x->x.name==subSec.name}?.fieldState)
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
                                        validatedSection(sectionState.find { x->x.name==it.name}?.fieldState)
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