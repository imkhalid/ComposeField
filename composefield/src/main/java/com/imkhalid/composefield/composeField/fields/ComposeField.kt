package com.techInfo.composefieldproject.composeField.fields

open class ComposeField {
    var focusCallback:((isValidated:Boolean,fieldName:String) -> Unit)? = null

}