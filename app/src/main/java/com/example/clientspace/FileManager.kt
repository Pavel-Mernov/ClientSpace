package com.example.clientspace

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import com.example.clientspace.ui.File as LocalFile // we will use java.io.file.here

object FileManager { // object used for opening files
    fun openFile(context : Context, file : LocalFile) {
        try {
            if (file.isImage) {
                openImage(context, file)
                return
            }

            val cacheDir = File(context.cacheDir, "temp_files")

            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val tempFile = File(cacheDir, file.name)

            FileOutputStream(tempFile).use {
                it.write(file.bytes)
            }

            val fileUri : Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, file.type)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
        catch (_: Exception) {

        }
    }

    private fun openImage(context: Context, file : LocalFile) {
        val intent = Intent(context, ImageOpenActivity::class.java).apply {
            putExtra("imageBytes", file.bytes)
            putExtra("imageName", file.name)

        }

        context.startActivity(intent)
    }
}