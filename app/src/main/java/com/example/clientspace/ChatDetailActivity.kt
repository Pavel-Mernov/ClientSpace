package com.example.clientspace

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientspace.databinding.ActivityChatDetailBinding
import com.example.clientspace.ui.Chat
import com.example.clientspace.ui.Message
import com.example.clientspace.ui.User
import com.example.clientspace.ui.UserRepository
import java.time.LocalDateTime

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDetailBinding

    private val REQUEST_CODE_PICK_FILE = 100

    private lateinit var user : User
    private lateinit var chat : Chat

    private val sendVoiceBtn
        get() = binding.sendVoiceButton

    private val attachmentLayout
        get() = binding.attachmentLayout

    private val audioPlayerView : AudioPlayerView
        get() = binding.audioPlayerView

    private val draft
        get() = chat.draft

    private var attachment
        get() = draft.attachment
        set(value) {
            draft.attachment = value
        }

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

        user = UserRepository.findUserById(curUserId) ?: run {
            val exString = "No user with Id: $curUserId"
            Log.e("get user", exString)
            throw Exception(exString)
        }

        chat = user.chats.firstOrNull{ it.chatId == chatId } ?: run {
            val exString = "No chat with such Id : $chatId"
            Log.e("get chat", exString)
            throw Exception(exString)
        }

        val chatName = chat.name
        val avatarBytes = chat.avatarImage // Получаем аватар
        val avatarBitmap = FileConverter.byteArrayToImage(avatarBytes)
        val messages = chat.messages // Получаем сообщения

        // Отображение данных
        binding.chatUserName.text = chatName // Устанавливаем имя
        binding.chatAvatar.setImageBitmap(avatarBitmap) // Устанавливаем аватар

        val btnBack = binding.backButton

        btnBack.setOnClickListener {

            finish()
        }



        // Настройка RecyclerView для сообщений
        val adapter = MessagesAdapter(messages, curUserId, audioPlayerView)
        binding.messagesRecyclerView.adapter = adapter
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)


        val editMsgText = binding.messageEditText

        val draftText = draft.text

        editMsgText.setText(draftText)

        updateAttachments()
        updateSendIcon()

        binding.btnRemoveAttachment.setOnClickListener{
            attachment = null // remove attachment
            updateUser()
        }

        editMsgText.addTextChangedListener(
            afterTextChanged = { s ->
                draft.text = editMsgText.text.toString()
                updateUser()

            }
        )

        sendVoiceBtn.setOnClickListener{
            if (sendVoiceBtn.tag == "ic_send") {
                // Log.e("Send message", "ready to send")
                val newMessage = Message(curUserId, editMsgText.text.toString(), LocalDateTime.now(), attachment = draft.attachment)

                chat.messages.add(newMessage)
                draft.text = ""
                draft.attachment = null
                binding.messagesRecyclerView.adapter = MessagesAdapter(chat.messages, curUserId, audioPlayerView)

                updateUser()

                editMsgText.text.clear()
            }
        }

        val btnAttach : ImageButton = binding.attachButton
        btnAttach.setOnClickListener{
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // Укажите тип файла, например "image/*" для изображений или "*/*" для всех файлов
            addCategory(Intent.CATEGORY_OPENABLE) // Гарантирует, что будут показаны только открываемые файлы
        }
        startActivityForResult(Intent.createChooser(intent, "Выберите файл"), REQUEST_CODE_PICK_FILE)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            val fileUri : Uri? = data?.data

            if (fileUri != null) {
                handleFile(fileUri)
            }
        }
    }

    private fun handleFile(uri: Uri) {
        val file = FileConverter.uriToFile(contentResolver, uri)

        if (file != null) {
            attachment = file

            updateUser()
        }
    }



    private fun updateUser() {

        updateAttachments()
        updateSendIcon()

        user.updateChat(chat)
        UserRepository.updateUser(user)
    }

    private fun updateSendIcon() {
        if (chat.draft.text.isBlank() && chat.draft.attachment == null) {
            sendVoiceBtn.setImageResource(R.drawable.ic_mic)
            sendVoiceBtn.tag = "ic_mic"
        }
        else {
            sendVoiceBtn.setImageResource(R.drawable.ic_send)
            sendVoiceBtn.tag = "ic_send"
        }
    }

    private fun updateAttachments() {
        val attachment = chat.draft.attachment

        if (attachment == null) {
            attachmentLayout.visibility = View.GONE
        }
        else {
            attachmentLayout.visibility = View.VISIBLE

            if (attachment.isAudio) {
                binding.imageAttachedFile.setImageResource(R.drawable.ic_play)
            }
            else if (attachment.isImage) { // attachmentIsAnImage
                binding.imageAttachedFile.setImageBitmap(
                    FileConverter.byteArrayToImage(attachment.bytes))
            }
            else {
                binding.imageAttachedFile.setImageResource(R.drawable.ic_file)

            }

            binding.imageAttachedFile.setOnClickListener{
                if (attachment.isAudio) {
                    binding.audioPlayerView.setAudioFile(attachment)
                }
                else {
                    FileManager.openFile(this, attachment)
                }
            }

            binding.textFileName.text = attachment.name
        }


    }
}