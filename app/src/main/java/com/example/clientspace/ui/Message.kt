package com.example.clientspace.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Message(
    var fromId : String,
    var text : String,
    var time : LocalDateTime,
    var isEdited : Boolean = false, // is message edited, default false
    var attachment : File? = null // attached file
) : Parcelable