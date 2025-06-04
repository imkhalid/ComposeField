package com.imkhalid.composefield.model

data class FormDataModel(
    val tableDataModels: List<TableDataModel>,
    val sectionDataModels: List<SectionDataModel>,
    val deleteIds: List<String>
)