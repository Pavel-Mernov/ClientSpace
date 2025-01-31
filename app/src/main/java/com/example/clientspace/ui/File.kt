package com.example.clientspace.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class File(val name : String, val bytes : ByteArray, val type : String) : Parcelable {
    val isImage : Boolean
        get() = type.startsWith("image/")

    val isAudio : Boolean
        get() = type.startsWith("audio/")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (!bytes.contentEquals(other.bytes)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }


}