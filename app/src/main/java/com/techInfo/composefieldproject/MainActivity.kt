package com.techInfo.composefieldproject

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.imkhalid.composefield.composeField.TableConfig
import com.imkhalid.composefield.composeField.fieldTypes.SectionType
import com.imkhalid.composefield.composeField.model.ComposeSectionModule
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

        Sections(
            rememberNavController(),
            sectionType = SectionType.TAB(tableConfig = TableConfig(
                tableAddButton = null,
                tablePopupButton = {it,data->
                    Button(onClick = { it.invoke() }) {
                        Text(text = "+ Add")
                    }
                }
            ),)
        )
            .Build(
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
            )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeFieldProjectTheme {
    }
}