package com.imkhalid.composefield.composeField.fieldTypes
@Deprecated("""
        ComposeKeyboardType is deprecated and replaced with ComposeKeyboardTypeAdv.
        
        ❌ Old Usage:
            val type = ComposeKeyboardType.MOBILE_NO
        
        ✅ New Usage:
            val type = ComposeKeyboardTypeAdv.MOBILE_NO(isSingle = true, countryCode = "+1")
        
        Key Differences:
        - Enum replaced with a sealed class to allow additional properties.
        - MOBILE_NO now supports isSingle and countryCode properties.
        
        Please migrate to ComposeKeyboardTypeAdv.
    """, ReplaceWith("ComposeKeyboardTypeAdv",))
enum class ComposeKeyboardType {
    CNIC,
    ID_NO,
    MOBILE_NO,
    EMAIL,
    TEXT,
    NUMBER,
    CURRENCY,
    PASSWORD,
    SENSITIVE,
    NONE;

    fun getAdvanceKeyboardType():ComposeKeyboardTypeAdv{

        return when(this){
            CNIC -> ComposeKeyboardTypeAdv.CNIC
            ID_NO -> ComposeKeyboardTypeAdv.ID_NO
            MOBILE_NO -> ComposeKeyboardTypeAdv.MOBILE_NO()
            EMAIL -> ComposeKeyboardTypeAdv.EMAIL
            TEXT -> ComposeKeyboardTypeAdv.TEXT()
            NUMBER -> ComposeKeyboardTypeAdv.NUMBER
            CURRENCY -> ComposeKeyboardTypeAdv.CURRENCY
            PASSWORD -> ComposeKeyboardTypeAdv.PASSWORD
            SENSITIVE -> ComposeKeyboardTypeAdv.SENSITIVE
            NONE -> ComposeKeyboardTypeAdv.NONE
        }
    }
}

