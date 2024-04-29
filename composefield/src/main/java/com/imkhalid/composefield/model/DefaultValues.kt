package com.imkhalid.composefield.model


data class DefaultValues(
    val id: String,
    val text: String,
    val code: String? = null,
    val form_fields_id: String? = null,
    var isChecked: Boolean = false
)