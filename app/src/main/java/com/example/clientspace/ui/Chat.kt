package com.example.clientspace.ui

import android.os.Parcelable
import com.example.clientspace.ui.Message
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val chatId : Int, // Chat Id
    val avatarImage: ByteArray,  // Avatar source
    val name: String,      // User name
    val messages: MutableList<Message>


) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        return chatId == other.chatId
    }

    override fun hashCode(): Int {
        return chatId.hashCode()
    }
}
