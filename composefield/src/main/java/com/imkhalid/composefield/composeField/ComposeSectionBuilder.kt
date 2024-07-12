package com.imkhalid.composefield.composeField

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldYesNo
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.HashMap

@Deprecated(
    message = "This class is Deprecated, Use Sections Class Instead"
)
class ComposeSectionBuilder(preStateList: ArrayList<MutableStateFlow<ComposeFieldState>>? = null) {
    var list: ArrayList<MutableStateFlow<ComposeFieldState>> = preStateList ?: arrayListOf()
    var isLocked: Boolean = false
    var sectionName: String = ""


    fun addField(field: ComposeFieldModule) = apply {
        if (isLocked.not()) {
            val _fieldState: MutableStateFlow<ComposeFieldState> = MutableStateFlow(
                ComposeFieldState()
            ).apply {
                update {
                    it.copy(
                        field = field,
                        text = field.value
                    )
                }
            }
            list.add(_fieldState)
        }
    }

    fun addSection(fields: List<ComposeFieldModule>) = apply {
        if (isLocked.not()) {
            fields.mapTo(list) { fieldMo ->
                val _fieldState: MutableStateFlow<ComposeFieldState> = MutableStateFlow(
                    ComposeFieldState()
                ).apply {
                    update {
                        it.copy(
                            field = fieldMo,
                            text = fieldMo.value
                        )
                    }
                }
                _fieldState
            }
            lock()
        }
    }

    fun setCallback() = apply {

    }

    fun lock() = apply {
        isLocked = true
    }

    fun unLock() = apply {
        isLocked = false
    }

    @Composable
    fun build(modifier: Modifier = Modifier) = apply {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(list.size) {
                ComposeFieldBuilder()
                    .setFieldModule(list[it])
                    .setFocusCallback { b, s -> }
                    .build(modifier = modifier)
            }
        }
    }
}


@Deprecated(
    message = "This class is Deprecated, Use Sections Class Instead"
)
class ComposeSections() {
    val mSections: ArrayList<ComposeSectionBuilder> = arrayListOf()
    var isLocked: Boolean = false
    var currentSectionIndex = 0
    lateinit var lastPage: () -> Unit
    lateinit var navController: NavHostController
    lateinit var parentNavController: NavHostController

    fun setParentNav(navHostController: NavHostController) = apply {
        parentNavController = navHostController
    }

    fun setLasPageCallback(callback: () -> Unit) = apply {
        lastPage = callback
    }

    fun lock() = apply {
        isLocked = true
    }

    fun unLock() = apply {
        isLocked = false
    }

    @Composable
    fun Build(sections: List<ComposeSectionModule>): ComposeSections {
        if (isLocked.not()) {
            navController = rememberNavController()
            sections.forEach {
                val composeSectionBuilder = ComposeSectionBuilder().apply {
                    sectionName = it.name
                    addSection(it.fields)
                }
                mSections.add(composeSectionBuilder)
            }
            lock()
        }
        Column {
            NavHost(
                navController = navController,
                startDestination = mSections.firstOrNull()?.sectionName ?: ""
            ) {
                mSections.forEachIndexed { index, composeSectionModule ->
                    composable(composeSectionModule.sectionName) {
                        composeSectionModule.build()
                    }
                }
            }
            Button(onClick = {
                navigateToNext()
            }) {
                Text(text = "Next Section")
            }

            BackHandler {
                if (currentSectionIndex != 0) {
                    --currentSectionIndex
                    navController.popBackStack()
                } else
                    parentNavController.popBackStack()
            }
        }
        return this
    }

    private fun navigateToNext() {
        if (currentSectionIndex < mSections.lastIndex) {
            navController.navigate(mSections[++currentSectionIndex].sectionName)
        } else {
            lastPage.invoke()
        }
    }
}

fun ArrayList<MutableStateFlow<ComposeFieldState>>.validateSection(): Boolean {
    return this.all { x ->
        val state = x.value
        (
                state.field.required == ComposeFieldYesNo.YES &&
                        (state.text.isNotEmpty() &&
                                state.hasError.not())
                ) ||
                (
                        state.field.required == ComposeFieldYesNo.NO &&
                                state.hasError.not())
    }
}

fun HashMap<String, List<ComposeFieldStateHolder>>.validate(): Boolean {
    return this.all { x ->
        x.value.all {
            it.state.field.required == ComposeFieldYesNo.YES && (it.state.text.isNotEmpty() &&
                    it.state.hasError.not()) ||
                    (it.state.field.required == ComposeFieldYesNo.NO &&
                            it.state.hasError.not())
        }
    }
}


fun List<ComposeFieldStateHolder>.validate(showError: Boolean = false): Boolean {
    val res = this.all {
        it.state.field.required == ComposeFieldYesNo.YES && (it.state.text.isNotEmpty() &&
                it.state.hasError.not()) ||
                (it.state.field.required == ComposeFieldYesNo.NO &&
                        it.state.hasError.not())
    }
    if (res.not() && showError) {
        this.first {
            it.state.field.required == ComposeFieldYesNo.YES && (it.state.text.isNotEmpty() &&
                    it.state.hasError.not()) ||
                    (it.state.field.required == ComposeFieldYesNo.NO &&
                            it.state.hasError.not())
        }.let { err ->
            val message = if (err.state.errorMessage.isEmpty()) {
                "Required"
            } else err.state.errorMessage
            err.updateValidation(Pair(false, message))
        }
    }

    return res
}

