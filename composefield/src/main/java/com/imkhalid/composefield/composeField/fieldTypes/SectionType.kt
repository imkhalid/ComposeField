package com.imkhalid.composefield.composeField.fieldTypes

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import com.imkhalid.composefield.composeField.TableConfig

enum class SectionTypeDep {
    Simple,
    SIMPLE_VERTICAL,
    Tab,
    Step;

    fun getSectionType(): SectionType {
        return when (this) {
            Simple -> SectionType.SIMPLE()
            SIMPLE_VERTICAL -> SectionType.SIMPLE(false)
            Tab -> SectionType.TAB(TableConfig())
            Step -> SectionType.STEP()
        }
    }
}

sealed class SectionType {
    data class SIMPLE(
        val horizontalSection: Boolean = true
    ) : SectionType()


    data class TAB(
        val tableConfig: TableConfig,
        val tabContentItem: @Composable (LazyItemScope.(name: String, isSelected: Boolean, clickCallback: (() -> Unit)?) -> Unit)? =
            null,
        val sectionValidated:(()->Unit)?=null,
    ) : SectionType()

    data class STEP(
        val stepSectionContentItem: @Composable (
        LazyItemScope.(name: String, clickCallback: (sectionName: String) -> Unit) -> Unit
        )? = null,
        val errorDialog: (@Composable
            (onClick: (positive: Boolean) -> Unit, onDismiss: () -> Unit) -> Unit)? = null
    ) : SectionType()
}
