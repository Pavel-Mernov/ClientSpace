package com.example.clientspace.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.time.LocalDateTime

@Parcelize
data class Message(
    val fromId : String,
    var text : String,
    var time : LocalDateTime
) : Parcelable