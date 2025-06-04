package com.imkhalid.composefield.composeField.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.model.TaggedMap
import com.imkhalid.composefield.composeField.section.Sections
import com.imkhalid.composefield.model.FormDataModel
import com.imkhalid.composefield.model.SectionDataModel
import com.imkhalid.composefield.model.TableDataModel

private fun HashMap<String, SnapshotStateList<TaggedMap>>.convertTableData(): List<TableDataModel> {
    val mainList = arrayListOf<TableDataModel>()
    forEach { (sectionName, value) ->
        val data = value.filter {
            it.isDeleted.not()
        }.map {
            buildMap {
                it.data.forEach {
                    it.value.forEach {
                        put(
                            it.state.field.name,
                            it.state.text
                        )
                    }
                }
                if (it.tag.isNotEmpty()) {
                    put("id", it.tag)
                }
            }
        }


        mainList.add(
            TableDataModel(
                sectionName.lowercase(),
                data
            )
        )
    }
    return mainList

}

private fun HashMap<String, List<ComposeFieldStateHolder>>.convertSectionData(): List<SectionDataModel> {
    val mainList = arrayListOf<SectionDataModel>()
    forEach { (sectionName, value) ->
        val data = buildMap {
            value.forEach {
                put(
                    it.state.field.name,
                    it.state.text
                )
            }
        }

        mainList.add(
            SectionDataModel(
                sectionName.lowercase(),
                data
            )
        )
    }
    return mainList

}

private fun HashMap<String, SnapshotStateList<TaggedMap>>.getDeletedIds(): List<String> {
    val list = arrayListOf<String>()
    forEach {
        it.value.forEach {
            if (it.isDeleted) {
                list.add(it.tag)
            }
        }
    }
    return list
}

fun Sections.getFormData(): FormDataModel {
    return FormDataModel(
        tableDataModels = tableData.convertTableData(),
        sectionDataModels = sectionState.convertSectionData(),
        deleteIds = tableData.getDeletedIds()
    )
}