package com.imkhalid.composefield.composeField.model

import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.model.section
import kotlinx.coroutines.flow.MutableStateFlow


data class ComposeSectionModule(
    var sortNumber: Int = 0,
    var fields:List<ComposeFieldModule> = emptyList(),
    var sectionStates:ArrayList<MutableStateFlow<ComposeFieldState>> = arrayListOf(),
    var name:String ="",
    var isDone:Boolean=false,
    var subSections:List<ComposeSectionModule> = emptyList(),
    var isTable:Boolean=false,
    var min:Int = 0,
    var max:Int = 0
){
    fun parseSectionToComposeSec(entry:Map.Entry<String, section>) =apply{
        name = entry.key
        entry.value.let {
            sortNumber = it.sorting_number
            fields = it.custom_fields.map {
                ComposeFieldModule().parseCustomField(it)
            }
        }
    }
}