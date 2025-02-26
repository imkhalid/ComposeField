package com.imkhalid.composefield.composeField.model

import com.imkhalid.composefield.composeField.ComposeFieldStateHolder

data class TaggedMap(
    val data:HashMap<String,List<ComposeFieldStateHolder>>,
    val tag:String="",
    val isDeleted:Boolean=false,
)