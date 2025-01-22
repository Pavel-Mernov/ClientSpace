package com.example.clientspace

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientspace.databinding.ActivityChatDetailBinding
import com.example.clientspace.ui.Message
import com.example.clientspace.ui.UserRepository
import java.time.LocalDateTime

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение данных из Intent
        val curUserId = intent.getStringExtra("curUserId") ?: run {
            val exString = "Cannot get curUserId outside extra"
            Log.e("get user", exString)
            throw Exception(exString)
        }

        // Log.e("cur user id", curUserId)

        val chatId = intent.getIntExtra("chatId", -1)
        if (chatId == -1) {
            val exString = "Cannot get chat Id outside extra"
            Log.e("chat Id", exString)
            throw Exception(exString)
        }

        // Log.e("chat id", chatId.toString())

        val user = UserRepository.findUserById(curUserId) ?: run {
            val exString = "No user with Id: $curUserId"
            Log.e("get user", exString)
            throw Exception(exString)
        }

        val chat = user.chats.firstOrNull{ it.chatId == chatId } ?: run {
            val exString = "No chat with such Id : $chatId"
            Log.e("get chat", exString)
            throw Exception(exString)
        }

        val chatName = chat.name
        val avatarBytes = chat.avatarImage // Получаем аватар
        val avatarBitmap = ImageConverter.byteArrayToImage(avatarBytes)
        val messages = chat.messages // Получаем сообщения

        // Отображение данных
        binding.chatUserName.text = chatName // Устанавливаем имя
        binding.chatAvatar.setImageBitmap(avatarBitmap) // Устанавливаем аватар

        val btnBack = binding.backButton

        btnBack.setOnClickListener {

            finish()
        }



        // Настройка RecyclerView для сообщений
        val adapter = MessagesAdapter(messages, curUserId)
        binding.messagesRecyclerView.adapter = adapter
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        val sendVoiceBtn = binding.sendVoiceButton
        val editMsgText = binding.messageEditText

        editMsgText.addTextChangedListener(
            afterTextChanged = { s ->
                if (s.isNullOrBlank()) {
                    sendVoiceBtn.setImageResource(R.drawable.ic_mic)
                    sendVoiceBtn.tag = "ic_mic"
                }
                else {
                    sendVoiceBtn.setImageResource(R.drawable.ic_send)
                    sendVoiceBtn.tag = "ic_send"
                }
            }
        )

        sendVoiceBtn.setOnClickListener{
            if (sendVoiceBtn.tag == "ic_send") {
                // Log.e("Send message", "ready to send")
                val newMessage = Message(curUserId, editMsgText.text.toString(), LocalDateTime.now())

                chat.messages.add(newMessage)
                binding.messagesRecyclerView.adapter = MessagesAdapter(chat.messages, curUserId)

                user.updateChat(chat)
                UserRepository.updateUser(user)

                editMsgText.text.clear()
            }
        }
    }

}