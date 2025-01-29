package com.example.clientspace

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ImageOpenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_image_open)

        val imageClose : ImageView = findViewById(R.id.imageClose)

        imageClose.setOnClickListener{
            finish()
        }

        val imageBytes = intent.getByteArrayExtra("imageBytes")
        val imageName = intent.getStringExtra("imageName")

        if (imageBytes == null) {
            finish()
        }
        imageBytes as ByteArray

        val contentImage : ImageView = findViewById(R.id.contentImage)
        contentImage.layoutParams.height = ScreenManager.getScreenWidth(this)
        contentImage.setImageBitmap(FileConverter.byteArrayToImage(imageBytes))

        if (!imageName.isNullOrBlank()) {
            val textImageName : TextView = findViewById(R.id.textImageName)
            textImageName.text = imageName
        }
    }
}