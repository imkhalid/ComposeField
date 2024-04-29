package com.techInfo.composefieldproject.composeField

import com.techInfo.composefieldproject.model.LoadingModel


data class ComposeFieldState(
    val loader: LoadingModel = LoadingModel(),
    val field:ComposeFieldModule = ComposeFieldModule(),
    var text :String = "",
    var hasError:Boolean = false,
    var errorMessage:String = "",
)
