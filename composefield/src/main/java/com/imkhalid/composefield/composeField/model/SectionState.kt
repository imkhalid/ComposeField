package com.imkhalid.composefield.composeField.model

import com.imkhalid.composefield.composeField.ComposeFieldStateHolder

data class SectionState(
    val name:String="",
    val fieldState:List<ComposeFieldStateHolder> = emptyList()
)