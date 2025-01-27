package com.example.clientspace.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.time.LocalDateTime

@Parcelize
data class Message(
    var fromId : String,
    var text : String,
    var time : LocalDateTime,
    val attachments : MutableList<ByteArray> = emptyList<ByteArray>().toMutableList()
        // list of attached files
) : Parcelable