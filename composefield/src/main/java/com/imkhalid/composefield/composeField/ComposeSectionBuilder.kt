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
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavigatorProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefieldproject.composeField.ComposeFieldModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ComposeSectionBuilder(preStateList:ArrayList<MutableStateFlow<ComposeFieldState>>?=null) {
    var list: ArrayList<MutableStateFlow<ComposeFieldState>> = preStateList?:arrayListOf()
    var isLocked:Boolean=false
    var sectionName:String = ""


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

    fun addSection(fields:List<ComposeFieldModule>) = apply {
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
    fun setCallback() = apply{

    }
    fun lock()=apply {
        isLocked=true
    }
    fun unLock() = apply {
        isLocked=false
    }

    @Composable
    fun build()=apply {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(list.size) {
                ComposeFieldBuilder()
                    .setFieldModule(list[it])
                    .setFocusCallback { b, s ->  }
                    .build()
            }
        }
    }
}


class ComposeSections(){
    val mSections:ArrayList<ComposeSectionBuilder> = arrayListOf()
    var isLocked:Boolean=false
    var currentSectionIndex=0
    lateinit var lastPage:()->Unit
    lateinit var navController: NavHostController
    lateinit var parentNavController: NavHostController

    fun setParentNav(navHostController: NavHostController) = apply {
        parentNavController = navHostController
    }

    fun setLasPageCallback(callback:()->Unit) = apply {
        lastPage = callback
    }

    fun lock()=apply {
        isLocked=true
    }
    fun unLock() = apply {
        isLocked=false
    }

    @Composable
    fun Build(sections: List<ComposeSectionModule>)= apply{
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
            NavHost(navController = navController, startDestination = mSections.firstOrNull()?.sectionName?:"") {
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
                if (currentSectionIndex!=0) {
                    --currentSectionIndex
                    navController.popBackStack()
                }else
                    parentNavController.popBackStack()
            }
        }
    }

    private fun navigateToNext() {
        if (currentSectionIndex<mSections.lastIndex){
            navController.navigate(mSections[++currentSectionIndex].sectionName)
        }else{
            lastPage.invoke()
        }
    }
}