package com.techInfo.composefieldproject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
import com.imkhalid.composefield.composeField.model.FamilyData
import com.imkhalid.composefield.composeField.model.FamilyField
import com.imkhalid.composefield.composeField.model.FamilySetup
import com.imkhalid.composefield.composeField.section.Sections
import com.imkhalid.composefieldproject.ui.MainViewModel
import com.imkhalid.composefieldproject.ui.theme.ComposeFieldProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeFieldProjectTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(
    navHostController: NavHostController,
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.sectionState.collectAsState()
    if (state.loadingModel.shouldCallApi)
        viewModel.getSections()


    if (state.loadingModel.isLoading)
        Box {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onBackground
            )
        }

    if (state.loadingModel.isLoading.not() && state.section != null)
//        viewModel.sections
//            .setParentNav(navHostController)
//            .setLasPageCallback { }
//            .Build(sections = emptyList(),
//
//            )
        Sections(
            rememberNavController(),
            sectionType = SectionType.Tab
        )
            .TabBuild(
                sections = ArrayList(state.section?.data?.risk_sections?.map {
                    ComposeSectionModule(isTable = true, max = 5).parseSectionToComposeSec(it)
                } ?: emptyList()).apply {
                    add(
                        this.elementAt(0).copy(
                            isTable = false,
                            name = "Khalid" + "2",
                            sortNumber = 5,
                        )
                    )
                },
                button = {
                    Button(onClick = { it.invoke() }) {
                        Text(text = "Add")
                    }
                },
                tableAddButton = null,
                tablePopupButton = {
                    Button(onClick = { it.invoke() }) {
                        Text(text = "+ Add")
                    }
                }
            )
}


fun printCurrentData(viewModel: MainViewModel) {
    val pair = viewModel.sections.mSections.forEach {
        it.list.map {
            it.value.field.name to it.value.text
        }
    }

    Log.d("REsult Data", (pair).toString())
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeFieldProjectTheme {
    }
}