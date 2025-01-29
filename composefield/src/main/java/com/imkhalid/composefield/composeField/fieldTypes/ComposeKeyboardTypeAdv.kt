package com.imkhalid.composefield.composeField.fieldTypes

import android.os.Parcel
import android.os.Parcelable

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

sealed class ComposeKeyboardTypeAdv : Parcelable {

    object CNIC : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<CNIC> = object : Parcelable.Creator<CNIC> {
            override fun createFromParcel(parcel: Parcel) = CNIC
            override fun newArray(size: Int): Array<CNIC?> = arrayOfNulls(size)
        }
    }

    object ID_NO : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<ID_NO> = object : Parcelable.Creator<ID_NO> {
            override fun createFromParcel(parcel: Parcel) = ID_NO
            override fun newArray(size: Int): Array<ID_NO?> = arrayOfNulls(size)
        }
    }

    data class MOBILE_NO(val isSelectionDisabled: Boolean = false, val countryCode: String = "") :
        ComposeKeyboardTypeAdv() {

        constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(), // Read boolean
            parcel.readString() ?: "" // Read string
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeByte(if (isSelectionDisabled) 1 else 0) // Write boolean
            parcel.writeString(countryCode)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<MOBILE_NO> {
            override fun createFromParcel(parcel: Parcel): MOBILE_NO {
                return MOBILE_NO(parcel)
            }

            override fun newArray(size: Int): Array<MOBILE_NO?> {
                return arrayOfNulls(size)
            }
        }
    }

    object EMAIL : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<EMAIL> = object : Parcelable.Creator<EMAIL> {
            override fun createFromParcel(parcel: Parcel) = EMAIL
            override fun newArray(size: Int): Array<EMAIL?> = arrayOfNulls(size)
        }
    }

    object TEXT : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<TEXT> = object : Parcelable.Creator<TEXT> {
            override fun createFromParcel(parcel: Parcel) = TEXT
            override fun newArray(size: Int): Array<TEXT?> = arrayOfNulls(size)
        }
    }

    object NUMBER : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<NUMBER> = object : Parcelable.Creator<NUMBER> {
            override fun createFromParcel(parcel: Parcel) = NUMBER
            override fun newArray(size: Int): Array<NUMBER?> = arrayOfNulls(size)
        }
    }

    object CURRENCY : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<CURRENCY> = object : Parcelable.Creator<CURRENCY> {
            override fun createFromParcel(parcel: Parcel) = CURRENCY
            override fun newArray(size: Int): Array<CURRENCY?> = arrayOfNulls(size)
        }
    }

    object PASSWORD : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<PASSWORD> = object : Parcelable.Creator<PASSWORD> {
            override fun createFromParcel(parcel: Parcel) = PASSWORD
            override fun newArray(size: Int): Array<PASSWORD?> = arrayOfNulls(size)
        }
    }

    object SENSITIVE : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<SENSITIVE> = object : Parcelable.Creator<SENSITIVE> {
            override fun createFromParcel(parcel: Parcel) = SENSITIVE
            override fun newArray(size: Int): Array<SENSITIVE?> = arrayOfNulls(size)
        }
    }

    object NONE : ComposeKeyboardTypeAdv() {
        override fun writeToParcel(parcel: Parcel, flags: Int) {}
        override fun describeContents() = 0

        @JvmField
        val CREATOR: Parcelable.Creator<NONE> = object : Parcelable.Creator<NONE> {
            override fun createFromParcel(parcel: Parcel) = NONE
            override fun newArray(size: Int): Array<NONE?> = arrayOfNulls(size)
        }
    }
}

