package com.imkhalid.composefield.composeField.fieldTypes

import android.os.Parcel
import android.os.Parcelable

sealed class ComposeKeyboardType : Parcelable {

    // Object-Based Subclasses (Stateless)
    data object CNIC : ComposeKeyboardType()
    data object TEXTAREA : ComposeKeyboardType()
    data object ID_NO : ComposeKeyboardType()
    data object EMAIL : ComposeKeyboardType()
    data object TEXT : ComposeKeyboardType()
    data object NUMBER : ComposeKeyboardType()
    data object PASSWORD : ComposeKeyboardType()
    data object SENSITIVE : ComposeKeyboardType()
    data object NONE : ComposeKeyboardType()


    data class DATE(val showEndClear: Boolean = false, val ageCalculation: Boolean = false, val helperText: String = "") :
        ComposeKeyboardType()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this::class.java.simpleName) // Store class name

        when (this) {
            is DATE -> {
                parcel.writeByte(if (showEndClear) 1 else 0)
                parcel.writeByte(if (ageCalculation) 1 else 0)
                parcel.writeString(helperText)
            }
            else -> {} // No extra data needed for object subclasses
        }
    }

    companion object {
        fun fromParcel(parcel: Parcel): ComposeKeyboardType {
            return when (parcel.readString()) {
                CNIC::class.java.simpleName -> CNIC
                ID_NO::class.java.simpleName -> ID_NO
                EMAIL::class.java.simpleName -> EMAIL
                TEXT::class.java.simpleName -> TEXT
                NUMBER::class.java.simpleName -> NUMBER
                PASSWORD::class.java.simpleName -> PASSWORD
                SENSITIVE::class.java.simpleName -> SENSITIVE
                NONE::class.java.simpleName -> NONE
                DATE::class.java.simpleName -> DATE(
                    parcel.readByte() != 0.toByte(),
                    parcel.readByte() != 0.toByte(),
                    parcel.readString() ?: ""
                )
                else -> NONE // Default fallback
            }
        }

        @JvmField
        val CREATOR: Parcelable.Creator<ComposeKeyboardType> = object : Parcelable.Creator<ComposeKeyboardType> {
            override fun createFromParcel(parcel: Parcel): ComposeKeyboardType = fromParcel(parcel)
            override fun newArray(size: Int): Array<ComposeKeyboardType?> = arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int = 0
}


