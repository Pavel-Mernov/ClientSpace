package com.example.clientspace

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.customview.widget.Openable
import com.example.clientspace.ui.File
import java.io.ByteArrayOutputStream
import java.io.InputStream

object FileConverter {
    fun uriToFile(contentResolver: ContentResolver, uri : Uri) : File? {
        return try {
            val mimeType = contentResolver.getType(uri)

            val inputStream : InputStream? = contentResolver.openInputStream(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()

            val buffer = ByteArray(1024)

            var len : Int

            while (inputStream?.read(buffer).also { len = it ?: -1 } != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
            }

            inputStream?.close()

            val fileName = getFileName(contentResolver, uri)

            File(fileName ?: "", byteArrayOutputStream.toByteArray(), mimeType ?: "")

        } catch (e : Exception) {
            Log.e("uri to byte array", e.message ?: "some exception")
            null
        }
    }

    private fun getFileName(contentResolver: ContentResolver, uri: Uri) : String? {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (nameIndex != -1 && cursor.moveToFirst()) {
                return  cursor.getString(nameIndex)
            }
        }
        return null
    }

    fun byteArrayToImage(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}