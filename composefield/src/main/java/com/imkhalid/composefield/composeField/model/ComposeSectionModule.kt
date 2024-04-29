package com.imkhalid.composefield.composeField.model

import com.imkhalid.composefield.model.section


data class ComposeSectionModule(
    var sortNumber: Int = 0,
    var fields:List<ComposeFieldModule> = emptyList(),
    var name:String ="",
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