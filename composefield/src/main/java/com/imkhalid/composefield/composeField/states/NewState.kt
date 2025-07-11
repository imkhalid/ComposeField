package com.imkhalid.composefield.composeField.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import com.imkhalid.composefield.composeField.ComposeFieldState
import com.imkhalid.composefield.composeField.ComposeFieldStateHolder
import com.imkhalid.composefield.composeField.fieldTypes.ComposeFieldType
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardTypeAdv
import com.imkhalid.composefield.composeField.model.ComposeFieldModule
import com.imkhalid.composefield.model.DefaultValues

/**
 * 🚀 **rememberFieldState (Updated Version)**
 *
 * This version replaces the old `rememberFieldState` function and now uses `ComposeKeyboardTypeAdv`.
 *
 * ## 🌟 **Usage Example**
 *
 * - **Basic Text Field:**
 * ```kotlin
 * val state = rememberFieldState(name = "username", keyboardType = ComposeKeyboardTypeAdv.TEXT)
 * ```
 *
 * - **Mobile Number Field with Country Code:**
 * ```kotlin
 * val state = rememberFieldState(
 *     name = "phone",
 *     keyboardType = ComposeKeyboardTypeAdv.MOBILE_NO(isSingle = true, countryCode = "+1")
 * )
 * ```
 *
 * ## 🛠 **Key Improvements**
 * ✅ `keyboardType` now uses `ComposeKeyboardTypeAdv`, supporting additional properties.
 * ✅ `MOBILE_NO` now supports `isSingle` and `countryCode`.
 * ✅ More flexibility for different keyboard input types.
 */
@Composable
fun rememberFieldState(
    name: String,
    label: String,
    id: String = "",
    type: ComposeFieldType = ComposeFieldType.TEXT_BOX,
    keyboardType: ComposeKeyboardTypeAdv = ComposeKeyboardTypeAdv.TEXT(),
    value: String = "",
    defaultValues: List<DefaultValues> = emptyList()
): ComposeFieldStateHolder {
    val initialField =
        ComposeFieldState(
            text = value,
            field =
            ComposeFieldModule(
                id = id,
                name = name,
                type = type,
                keyboardType = keyboardType,
                value = value,
                label = label,
                defaultValues = defaultValues
            )
        )
    return rememberSaveable(
        inputs = arrayOf(id, name, type, keyboardType, value, label, defaultValues),
        saver = ComposeFieldStateHolder.Saver,
    ) {
        ComposeFieldStateHolder(initialField)
    }
}