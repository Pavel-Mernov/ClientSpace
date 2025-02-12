package com.example.clientspace

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clientspace.databinding.ActivityChatDetailBinding
import com.example.clientspace.ui.Chat
import com.example.clientspace.ui.Message
import com.example.clientspace.ui.User
import com.example.clientspace.ui.UserRepository
import java.io.IOException
import java.time.LocalDateTime
import com.example.clientspace.ui.File as LocalFile

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

    private lateinit var voiceRecorder: VoiceRecorder

    // id of current user
    private lateinit var curUserId : String

    private var chatId : Int = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение данных из Intent
        curUserId = intent.getStringExtra("curUserId") ?: run {
            val exString = "Cannot get curUserId outside extra"
            Log.e("get user", exString)
            throw Exception(exString)
        }

        // Log.e("cur user id", curUserId)

        chatId = intent.getIntExtra("chatId", -1)
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

        // initialize voice recorder
        voiceRecorder = VoiceRecorder(this)

        // Отображение данных
        binding.chatUserName.text = chatName // Устанавливаем имя
        binding.chatAvatar.setImageBitmap(avatarBitmap) // Устанавливаем аватар

        val btnBack = binding.backButton

        btnBack.setOnClickListener {
            try {
                finish()
            }
            catch (e : Throwable) {
                Log.e("chat empty", e.message ?: "some error")
            }
        }



        // Настройка RecyclerView для сообщений
        val adapter = MessagesAdapter(chat.messages, curUserId, audioPlayerView)
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
                sendMessage()
            }
        }

        sendVoiceBtn.setOnTouchListener{ _, event ->
            if (sendVoiceBtn.tag == "ic_mic") {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!checkAudioPermission()) {
                            requestAudioPermission()
                        } else {
                            voiceRecorder.startRecording()

                            sendVoiceBtn.setImageResource(R.drawable.ic_record_red)
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        if (voiceRecorder.isRecording) {
                            voiceRecorder.stopRecording()

                            val recordedBytes = voiceRecorder.getRecordedBytes()
                            val textFormat = "audio/wav"

                            val voiceFile = LocalFile("Voice message", recordedBytes, textFormat)

                            attachment = voiceFile
                            sendMessage()
                        }
                    }
                }
            }
            else { // if icon is to send
                sendMessage()
            }
            true
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

    private fun sendMessage() {
        val newMessage = Message(curUserId, binding.messageEditText.text.toString(), LocalDateTime.now(), attachment = draft.attachment)

        chat.messages.add(newMessage)
        draft.text = ""
        draft.attachment = null
        binding.messagesRecyclerView.adapter = MessagesAdapter(chat.messages, curUserId, audioPlayerView)

        updateUser()

        binding.messageEditText.text.clear()
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

            // Log.e("file name", attachment.name)
            binding.textFileName.text = if (attachment.name.length >= 29)
                attachment.name.substring(0, 26) + "..."
                else
                    attachment.name
        }


    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}