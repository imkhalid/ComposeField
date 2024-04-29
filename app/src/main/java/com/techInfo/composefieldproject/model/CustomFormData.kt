package com.techInfo.composefieldproject.model

data class CustomFormData(
    val status: String? = null,
    val message: String? = null,
    val code:Int = 0,
    val data: FormData? = null,
)

data class FormData(
    val risk_sections : Map<String,section>
)

data class section(
    val category_id: Int,
    val section_id: Int,
    val sorting_number: Int,
    var custom_fields: List<CustomFields>,
    val section_name: String = "",
)