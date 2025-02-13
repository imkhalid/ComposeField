package com.imkhalid.composefield.composeField

import androidx.navigation.NavHostController
import com.imkhalid.composefield.composeField.section.Sections

class  MyNavHost(val nav: NavHostController, val sections:List<String>, val section: Sections, val onLastPageReach: ((Sections) -> Unit)?) {

    fun next(){
        val currentSection = nav.currentDestination?.route.orEmpty()
        sections.indexOf(currentSection).takeIf { x->x!=-1 }?.let {
            val nextSection = if (sections.lastIndex>it)
                sections[it+1]
            else {
                onLastPageReach?.invoke(section)
                null
            }
            nextSection?.let {
                nav.navigate(it)
            }
        }
    }
}

internal fun navigateToNext(
    navController: MyNavHost,
    lastPage: ((Sections) -> Unit)? = null
) {
    navController.next()
}