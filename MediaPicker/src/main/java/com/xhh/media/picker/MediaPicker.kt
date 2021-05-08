package com.xhh.media.picker

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.MainThread

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/4/30:3:38 PM
 **/
class MediaPicker private constructor(private val application: Application){
    private val uri by lazy {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    fun startPickerPage(vararg mediaType: Int,onQueryImageSuccess:(()->Unit)?=null) {
        if (mediaType.isEmpty()) {
            Log.e(TAG, "mediaType can't empty")
            return
        }
        try {
            val mediaArray = mutableListOf<String>()
            mediaType.forEachIndexed { index, i ->
                mediaArray[index] = when (i) {
                    IMAGE -> MediaStore.Images.Media._ID
                    VIDEO -> MediaStore.Video.Media._ID
                    AUDIO -> MediaStore.Audio.Media._ID
                    else -> throw Exception("unsupported media type")
                }
            }
            application.contentResolver.query(uri,mediaArray.toTypedArray(),null,null,"DESC")?.use {
                cursor ->
                val ids = mutableListOf<Int>()
                mediaArray.forEach {
                    val id = cursor.getColumnIndex(it)
                    ids.add(id)
                }
                while(cursor.moveToNext()){
                    ids.forEachIndexed { index, i ->
                        when(mediaType[index]){
                            IMAGE-> {//get image
                                val uriId = cursor.getLong(i)
                                val contentUri = ContentUris.withAppendedId(uri,uriId)
                            }
                        }

                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG: String = "MediaPicker"
        const val IMAGE: Int = 0x1
        const val VIDEO: Int = 0x2
        const val AUDIO: Int = 0x3
        private lateinit var mInstance:MediaPicker
        fun getInstance(application: Application):MediaPicker{
            if (!::mInstance.isInitialized){
                mInstance = MediaPicker(application)
            }
            return mInstance
        }

    }
}