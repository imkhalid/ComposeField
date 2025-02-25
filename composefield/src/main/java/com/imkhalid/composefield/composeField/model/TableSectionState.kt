package com.imkhalid.composefield.composeField.model

import androidx.compose.runtime.snapshots.SnapshotStateList

data class TableSectionState(
    val name:String,
    val items: SnapshotStateList<SectionState>
)