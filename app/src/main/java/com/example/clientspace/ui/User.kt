package com.example.clientspace.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var userId : String,
    var image : ByteArray,
    var enterCode : ByteArray? = null,
    var userName : String,
    var description : String = "",
    var isCurrent : Boolean = false,
    val chats : MutableList<Chat> = emptyList<Chat>().toMutableList()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

    fun updateChat(chat : Chat) {
        val indexOfChat = chats.indexOfFirst { it.chatId == chat.chatId }
        chats[indexOfChat] = chat
    }
}