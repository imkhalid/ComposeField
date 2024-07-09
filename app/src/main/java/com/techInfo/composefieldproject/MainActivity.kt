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
                    Box(contentAlignment = Alignment.Center) {
                        NavHost(navController = navController, startDestination = "Step1") {
                            composable("Step1") {
                                Greeting(navController, viewModel)
                            }
                            composable("Step2") {

                            }
                        }
                        Button(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .width(150.dp)
                                .height(50.dp), onClick = {
                                if (navController.currentDestination?.route == "Step2") {
                                    printCurrentData(viewModel)
                                } else {
                                    navController.navigate("Step2")
                                }
                            }) {
                            Text(text = "Next")
                        }
                    }
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
            navHostController,
            rememberNavController(),
            sectionType = SectionType.SIMPLE_VERTICAL
        )
            .Build(
                sections = state.section?.data?.risk_sections?.map {
                    ComposeSectionModule().parseSectionToComposeSec(it)
                } ?: emptyList(),
                familyData = FamilyData(
                    familySetup = FamilySetup(
                        hasSpouse = true,
                        spouseMinDate = "1995-01-01",
                        spouseMaxDate = "2001-01-01",
                        hasChild = true,
                        childMinDate = "2001-01-02",
                        childMaxDate = "2024-01-03",
                        hasParent = true,
                        parentMinDate = "",
                        parentMaxDate = "",
                        minNoOfParent = 0,
                        minNoOfChild = 0,
                        minNoOfSpouse = 0,
                        maxNoOfParent = 1,
                        maxNoOfChild = 1,
                        maxNoOfSpouse = 1,
                        fields = arrayListOf(
                            FamilyField(
                                familySetupId = "1",
                                familyDetailField = "relation",
                                required = true,
                                visible = true
                            ),
                            FamilyField(
                                familySetupId = "1",
                                familyDetailField = "gender",
                                required = true,
                                visible = true
                            ),
                            FamilyField(
                                familySetupId = "1",
                                familyDetailField = "dob",
                                required = true,
                                visible = true
                            ),
                        )
                    ),
                    AddButton = {
                        Button(onClick = { it.invoke() }) {
                            Text(text = "Add Button")
                        }
                    },
                    PopupButton = {
                        Button(modifier = Modifier.align(Alignment.BottomCenter),
                            onClick = { it.invoke() }) {
                            Text(text = "Done")
                        }
                    },
                    snapshotStateList = SnapshotStateList()

                )
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