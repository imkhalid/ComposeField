package com.imkhalid.composefield.model

data class LoadingModel(
    var shouldCallApi:Boolean=true,
    var isLoading:Boolean=false,
    var noItem:Boolean=false,
    var error:Boolean=false,
    var errors:Any = Any()
)
