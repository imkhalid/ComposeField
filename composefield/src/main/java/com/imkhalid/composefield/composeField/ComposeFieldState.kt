package com.imkhalid.composefield.composeField

import com.imkhalid.composefield.model.LoadingModel
import com.imkhalid.composefieldproject.composeField.ComposeFieldModule


data class ComposeFieldState(
    val loader: LoadingModel = LoadingModel(),
    val field: ComposeFieldModule = ComposeFieldModule(),
    var text :String = "",
    var hasError:Boolean = false,
    var errorMessage:String = "",
)
