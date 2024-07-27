package com.imkhalid.composefield.composeField.fieldTypes

enum class ComposeFieldType {
    TEXT_BOX,
    TEXT_AREA,
    DROP_DOWN,
    DATE_PICKER,
    TIME_PICKER,
    DATE_TIME_PICKER,
    SWITCH,
    CHECK_BOX,
    RADIO_BUTTON,
}

enum class ComposeFieldYesNo(val value: Boolean) {
    YES(true),
    NO(false)
}
