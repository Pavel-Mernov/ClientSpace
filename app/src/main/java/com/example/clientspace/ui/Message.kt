package com.example.clientspace.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Message(
    var fromId : String,
    var text : String,
    var time : LocalDateTime,
    var attachment : File? = null // attached file
) : Parcelable