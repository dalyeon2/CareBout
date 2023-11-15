package com.example.carebout.view.home

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File

class ImageUtil{

    suspend fun save(context: Context, imageUri: Uri): String{
        val fileName:String
        context.contentResolver.query(imageUri, arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME), null, null, null).use {
            it?.moveToNext()
            fileName =  it!!.getString(it.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
        }

        val file = File(context.filesDir,fileName)
        context.contentResolver.openInputStream(imageUri).use {
            file.writeBytes(
                it!!.readBytes()
            )
        }
        return fileName
    }

    fun get(context: HomeActivity, fileName: String): Uri{
        return File(context.filesDir, fileName).toUri()
    }


}