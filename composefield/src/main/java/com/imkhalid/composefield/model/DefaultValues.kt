package com.imkhalid.composefield.model

import android.os.Parcel
import android.os.Parcelable

data class DefaultValues(
    val id: String,
    val text: String,
    val code: String? = null,
    val form_fields_id: String? = null,
    var isChecked: Boolean = false,
    val dependent_child_fields:List<DependantChild> = emptyList()
) : Parcelable {
    constructor(
        parcel: Parcel
    ) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(text)
        parcel.writeString(code)
        parcel.writeString(form_fields_id)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DefaultValues> {
        override fun createFromParcel(parcel: Parcel): DefaultValues {
            return DefaultValues(parcel)
        }

        override fun newArray(size: Int): Array<DefaultValues?> {
            return arrayOfNulls(size)
        }
    }
}
