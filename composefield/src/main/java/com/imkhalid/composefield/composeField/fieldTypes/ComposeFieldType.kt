package com.imkhalid.composefield.composeField.fieldTypes

import android.os.Parcel
import android.os.Parcelable
import com.imkhalid.composefield.composeField.fieldTypes.ComposeKeyboardType.TEXT


enum class ComposeFieldYesNo(val value: Boolean) {
    YES(true),
    NO(false)
}

sealed class ComposeFieldType:Parcelable{
    data class TextBox(
        val keyboardType: ComposeKeyboardType = TEXT,
    ) : ComposeFieldType()
    data class MobileNo(
        val isSelectionDisabled: Boolean = false,
        val countryCode: String = ""
    ): ComposeFieldType()
    data object Dropdown : ComposeFieldType()
    data class DatePicker(
        val showEndClear: Boolean = false,
        val ageCalculation: Boolean = false,
        val helperText: String = ""
    ): ComposeFieldType()
    data object TimePicker : ComposeFieldType()
    data object DateTimePicker : ComposeFieldType()
    data object Switch : ComposeFieldType()
    data object CheckBox : ComposeFieldType()
    data object RadioButton : ComposeFieldType()
    data object Currency : ComposeFieldType()


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this::class.java.simpleName) // Store class name

        when (this) {
            is TextBox -> {
                keyboardType.writeToParcel(parcel, flags)
            }
            is MobileNo->{
                parcel.writeByte(if (isSelectionDisabled)1 else 0)
                parcel.writeString(countryCode)
            }
            is DatePicker -> {
                parcel.writeByte(if (showEndClear) 1 else 0)
                parcel.writeByte(if (ageCalculation) 1 else 0)
                parcel.writeString(helperText)
            }
            else -> {} // No extra data needed for object subclasses
        }
    }

    companion object {
        fun fromParcel(parcel: Parcel): ComposeFieldType {
            return when (parcel.readString()) {

                MobileNo::class.java.simpleName -> MobileNo(
                    parcel.readByte() != 0.toByte(),
                    parcel.readString()?:"",
                )
                DatePicker::class.java.simpleName -> DatePicker(
                    parcel.readByte() != 0.toByte(),
                    parcel.readByte() != 0.toByte(),
                    parcel.readString() ?: ""
                )
                TimePicker::class.java.simpleName->TimePicker
                DateTimePicker::class.java.simpleName->DateTimePicker
                Dropdown::class.java.simpleName->Dropdown
                RadioButton::class.java.simpleName->RadioButton
                Switch::class.java.simpleName->Switch
                Currency::class.java.simpleName->Currency
                CheckBox::class.java.simpleName->CheckBox
                else->TextBox(
                    keyboardType = ComposeKeyboardType.CREATOR.createFromParcel(parcel)
                )
            }
        }

        @JvmField
        val CREATOR: Parcelable.Creator<ComposeFieldType> = object : Parcelable.Creator<ComposeFieldType> {
            override fun createFromParcel(parcel: Parcel): ComposeFieldType = fromParcel(parcel)
            override fun newArray(size: Int): Array<ComposeFieldType?> = arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int = 0
}
