package com.example.clientspace

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientspace.ui.Chat
import java.time.format.DateTimeFormatter

class ChatLinkAdapter(private val chats: List<Chat>, private val curUserId : String) : RecyclerView.Adapter<ChatLinkAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_link, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)

        // Установите обработчик кликов
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatDetailActivity::class.java).apply {
                putExtra("chatId", chat.chatId)
                putExtra("curUserId", curUserId)
                }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = chats.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ImageView = itemView.findViewById(R.id.chat_avatar)
        private val nameTextView: TextView = itemView.findViewById(R.id.chat_name)
        private val timeTextView : TextView = itemView.findViewById(R.id.chat_time)
        private val lastMessageTextView : TextView = itemView.findViewById(R.id.chat_message)


        fun bind(chat: Chat) {
            avatarImageView.setImageBitmap(ImageConverter.byteArrayToImage(chat.avatarImage))
            nameTextView.text = chat.name

            val lastMessage = chat.messages.maxByOrNull { it.time }!!

            val lastMessageTime = lastMessage.time
            val lastMessageText = lastMessage.text

            timeTextView.text = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm").format(lastMessageTime)

            lastMessageTextView.text = if (lastMessageText.length > 30)
                lastMessageText.substring(0, 27) + "..."
            else lastMessageText

        }
    }
}
