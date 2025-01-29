package com.imkhalid.composefield.composeField.fieldTypes

/**
 * 🚀 **ComposeKeyboardTypeAdv** (Replaces ComposeKeyboardType)
 *
 * This sealed class replaces the old enum and allows flexibility by adding parameters to MOBILE_NO.
 *
 * ## 🌟 **Usage Example**
 *
 * - **CNIC & ID_NO** (No parameters needed)
 * ```kotlin
 * val type: ComposeKeyboardTypeAdv = ComposeKeyboardTypeAdv.CNIC
 * ```
 *
 * - **MOBILE_NO** (New properties available)
 * ```kotlin
 * val mobileType = ComposeKeyboardTypeAdv.MOBILE_NO(isSingle = true, countryCode = "+92")
 * ```
 *
 * ## 🛠 **Key Improvements**
 * ✅ **Extensibility** - Allows adding properties like `isSingle` and `countryCode`.
 * ✅ **Type Safety** - Prevents incorrect usage with unnecessary parameters.
 * ✅ **More Descriptive** - Provides meaningful structure compared to enums.
 */
sealed class ComposeKeyboardTypeAdv {
    object CNIC: ComposeKeyboardTypeAdv()
    object ID_NO: ComposeKeyboardTypeAdv()
    data class MOBILE_NO(val isSingle:Boolean=false,val countryCode:String=""):
        ComposeKeyboardTypeAdv()
    object EMAIL: ComposeKeyboardTypeAdv()
    object TEXT: ComposeKeyboardTypeAdv()
    object NUMBER: ComposeKeyboardTypeAdv()
    object CURRENCY: ComposeKeyboardTypeAdv()
    object PASSWORD: ComposeKeyboardTypeAdv()
    object SENSITIVE: ComposeKeyboardTypeAdv()
    object NONE: ComposeKeyboardTypeAdv()
}