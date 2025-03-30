package com.example.clientspace.ui

import android.os.Parcelable
import com.example.clientspace.ui.Message
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Chat(
    var chatId : Int = -1, // Chat Id
    var avatarImage: ByteArray? = null,  // Avatar source
    var name: String,      // User name
    val messages: MutableList<Message> = emptyList<Message>().toMutableList(),
    val isForTwo: Boolean, // if chat can be only for two
    val otherMembers: MutableList<String>, // list of other user ids
    var draft: Message = Message("", "", LocalDateTime.now()), // last draft message

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
