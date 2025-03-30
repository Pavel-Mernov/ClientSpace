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

        // обработчик кликов
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatDetailActivity::class.java).apply {
                putExtra("chat", chat)
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
            if (chat.avatarImage !=  null) {
                avatarImageView.setImageBitmap(FileConverter.byteArrayToImage(chat.avatarImage!!))
            }
            else {
                avatarImageView.setImageResource(R.drawable.ic_default_avatar)
            }

            nameTextView.text = chat.name

            val lastMessage = chat.messages.maxByOrNull { it.time }

            if (lastMessage != null) {
                val lastMessageTime = lastMessage.time
                val lastMessageText = lastMessage.text

                val draft = chat.draft

                timeTextView.text =
                    DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm").format(lastMessageTime)

                if (chat.draft.text.isEmpty() && chat.draft.attachment == null) {
                    lastMessageTextView.text = if (lastMessageText.length > 30)
                        lastMessageText.substring(0, 27) + "..."
                    else lastMessageText
                } else if (chat.draft.attachment != null) {
                    lastMessageTextView.text = "Черновик: image."
                } else {
                    lastMessageTextView.text = "Черновик: " +
                            if (draft.text.length > 20)
                                draft.text.substring(0, 17) + "..."
                            else draft
                }
            }
            else {
                timeTextView.text = ""
                lastMessageTextView.text = "Сообщений нет"
            }
        }
    }
}
