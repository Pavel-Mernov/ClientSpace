package com.example.clientspace

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageConverter {
    fun uriToByteArray(contentResolver: ContentResolver, uri : Uri) : ByteArray? {
        return try {
            val inputStream : InputStream? = contentResolver.openInputStream(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()

            val buffer = ByteArray(1024)

            var len : Int

            while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
            }

            inputStream?.close()

            byteArrayOutputStream.toByteArray()

        } catch (e : Exception) {
            Log.e("uri to byte array", e.message ?: "some exception")
            null
        }
    }

    fun byteArrayToImage(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}