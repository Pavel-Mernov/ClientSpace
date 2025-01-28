package com.example.clientspace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.clientspace.ui.User
import com.example.clientspace.ui.UserRepository

class UserDetailsActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 3

    // Инициализация Views
    private val userImage: ImageView
        get() = findViewById(R.id.userImage)
    private val userNameText: TextView
        get() = findViewById(R.id.userName)
    private val userDescriptionText: TextView
        get() = findViewById(R.id.userDescription)
    private val userIdText: TextView
        get() = findViewById(R.id.userId)
    private val btnEditCode: Button
        get() = findViewById(R.id.btnEditCode)
    private val btnEditImage: Button
        get() = findViewById(R.id.btnEditImage)
    private val btnToMessages: Button
        get() = findViewById(R.id.btnToMessages)

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        // Получаем переданный объект UserId из Intent
        val userId = intent.getStringExtra("userId") ?: throw Exception("No user Id passed")
        user = UserRepository.findUserById(userId) ?: run {
            val exString = "User not given"
            Log.e("Show user", exString)
            throw Exception(exString)

        }

        bind(user)

        btnEditCode.setOnClickListener{
            // trying to set new enter code
            val intent = Intent(this, EditCodeActivity::class.java).apply {
                putExtra("userId", userId)
            }
            startActivity(intent)
        }

        btnEditImage.setOnClickListener{
            openGallery()
        }

        btnToMessages.setOnClickListener{
            // finish activity and go to messages
            finish()
        }


    }

    private fun bind(user : User) {

        userImage.setImageBitmap(FileConverter.byteArrayToImage(user.image))
        userNameText.text = user.userName
        userDescriptionText.text = user.description ?: "No description available"
        userIdText.text = "@${user.userId}"
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val uri = data?.data

            if (uri != null) {
                val imgFile = FileConverter.uriToFile(contentResolver, uri)

                if (imgFile != null)  {
                    user.image = imgFile.bytes
                    UserRepository.updateUser(user)

                    bind(user)
                }
            }
        }
    }
}
