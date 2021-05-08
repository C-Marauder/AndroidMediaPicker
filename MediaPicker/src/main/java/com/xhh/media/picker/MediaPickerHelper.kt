package com.xhh.media.picker

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.format.Formatter
import android.util.Size
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.xhh.media.picker.data.Constants
import com.xhh.media.picker.data.MediaAudio
import com.xhh.media.picker.data.MediaImage
import com.xhh.media.picker.data.MediaVideo
import com.xhh.media.picker.image.ImagePickerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/6:4:26 PM
 **/
object MediaPickerHelper {
    private val MEDIA_IMAGE_URI: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val MEDIA_VIDEO_URI: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    private val MEDIA_AUDIO_URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private const val MEDIA_IMAGE_ID: String = MediaStore.Images.Media._ID
    private const val MEDIA_VIDEO_ID: String = MediaStore.Video.Media._ID
    private const val MEDIA_AUDIO_ID: String = MediaStore.Audio.Media._ID
    private val mFormatterBuilder: StringBuilder by lazy {
        StringBuilder()
    }
    private val mFormatter: java.util.Formatter by lazy {
        Formatter(mFormatterBuilder, Locale.CHINA)
    }

    private fun formatDuration(duration: Int): String {
        mFormatterBuilder.clear()
        val time = if (duration < 1000) {
            1
        } else {
            duration / 1000
        }
        val format = when {
            time < 60 -> {
                mFormatter.format("00:%02d", time)
            }
            time in 61..3599 -> {
                mFormatter.format("%02d:%02d", time / 60, time % 60)
            }
            else -> {
                val hour = time / 3600
                val minute = time % 3600 / 60
                val second = time % 3600
                mFormatter.format("%02d:%02d:%02d", hour, minute, second)
            }
        }

        return format.toString()

    }

    private fun registerPermission(
        activity: FragmentActivity,
        onDenied: () -> Unit,
        onGranted: () -> Unit
    ) {
        val launcher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                val read = it[Manifest.permission.READ_EXTERNAL_STORAGE]
                val write = it[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                if (read != null && write != null && read && write) {
                    onGranted()
                } else {
                    onDenied()
                }

            }
        launcher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private fun checkPermission(
        activity: FragmentActivity,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        val readCode = PermissionChecker.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeCode = PermissionChecker.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (writeCode == PermissionChecker.PERMISSION_GRANTED && readCode == PermissionChecker.PERMISSION_GRANTED) {
            onGranted()
        } else {
            onDenied()
        }
    }

    fun queryImage(
        fragment: Fragment,
        thumbnailSize: Size,
        onDenied: () -> Unit,
        onResult: (mediaImages: MutableList<MediaImage>) -> Unit
    ) {
        queryImage(fragment.requireActivity(), thumbnailSize, onDenied, onResult)
    }

    fun queryImage(
        activity: FragmentActivity,
        thumbnailSize: Size,
        onDenied: () -> Unit,
        onResult: (mediaImages: MutableList<MediaImage>) -> Unit
    ) {
        checkPermission(activity, onGranted = {
            realQueryImage(activity, thumbnailSize, onResult)
        }, onDenied = {
            registerPermission(activity, onDenied) {
                realQueryImage(activity, thumbnailSize, onResult)
            }
        })

    }

    fun queryAudio(
        activity: FragmentActivity,
        onDenied: () -> Unit,
        onResult: (mediaImages: MutableList<MediaAudio>) -> Unit
    ) {
        checkPermission(activity, onGranted = {
            realQueryAudio(activity, onResult)
        }, onDenied = {
            registerPermission(activity, onDenied) {
                realQueryAudio(activity, onResult)
            }
        })
    }

    private fun realQueryAudio(
        activity: FragmentActivity,
        onResult: (mediaImages: MutableList<MediaAudio>) -> Unit
    ) {
        val contentResolver = activity.applicationContext.contentResolver
        createQueryTask(activity,onQuery = {
            query(
                contentResolver,
                MEDIA_AUDIO_URI,
                id = MEDIA_AUDIO_ID,
                MEDIA_AUDIO_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
            ) { cursor ->
                val mediaAudios = mutableListOf<MediaAudio>()
                val audioIdColumn = cursor.getColumnIndexOrThrow(MEDIA_AUDIO_ID)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                while (cursor.moveToNext()){
                    val audioId = cursor.getLong(audioIdColumn)
                    val artist = cursor.getString(artistColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size  = cursor.getInt(sizeColumn)*1L
                    val formatSize  = Formatter.formatFileSize(activity,size)
                    val audioUri = ContentUris.withAppendedId(MEDIA_AUDIO_URI,audioId)
                    val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        try {
                            contentResolver.loadThumbnail(audioUri, Size(480,480),null)
                        }catch (e:FileNotFoundException){
                            null
                        }
                    }else{
                        null
                    }
                    mediaAudios.add(MediaAudio(audioUri,bitmap,duration, formatDuration(duration),size,formatSize,artist,displayName))

                }
                cursor.close()
                it.postValue(mediaAudios)
            }
        },onResult)
    }

    fun queryVideo(
        activity: FragmentActivity, onDenied: () -> Unit,
        onResult: (mediaImages: MutableList<MediaVideo>) -> Unit
    ) {
        checkPermission(activity, onGranted = {
            realQueryVideo(activity, onResult)
        }, onDenied = {
            registerPermission(activity, onDenied) {
                realQueryVideo(activity, onResult)
            }
        })
    }

    private fun realQueryVideo(
        activity: FragmentActivity,
        onResult: (mediaImages: MutableList<MediaVideo>) -> Unit
    ) {
        val contentResolver = activity.applicationContext.contentResolver
        createQueryTask(activity,onQuery = {
            query(
                contentResolver,
                MEDIA_VIDEO_URI,
                MEDIA_VIDEO_ID,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
            ) { cursor ->
                val mediaVideos = mutableListOf<MediaVideo>()
                val id = cursor.getColumnIndexOrThrow(MEDIA_VIDEO_ID)
                val sizeId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val nameId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                while (cursor.moveToNext()) {
                    val videoId = cursor.getLong(id)
                    val videoName = cursor.getString(nameId)
                    val length = cursor.getInt(sizeId) * 1L
                    val videoSize = Formatter.formatFileSize(activity, length)
                    val videoDuration = cursor.getInt(durationId)
                    val durationFormat = formatDuration(videoDuration)
                    val videoUri = ContentUris.withAppendedId(MEDIA_VIDEO_URI, videoId)
                    val bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                        contentResolver,
                        videoId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null
                    )
                    mediaVideos.add(
                        MediaVideo(
                            videoUri,
                            bitmap,
                            videoDuration,
                            durationFormat,
                            length,
                            videoSize,
                            videoName
                        )
                    )
                }
                cursor.close()
                it.postValue(mediaVideos)

            }
        },onResult)
    }

    private fun realQueryImage(
        activity: FragmentActivity,
        thumbnailSize: Size,
        onResult: (mediaImages: MutableList<MediaImage>) -> Unit
    ) {
        val contentResolver = activity.applicationContext.contentResolver
        createQueryTask(activity,onQuery = {
            val mediaImages = mutableListOf<MediaImage>()
            query(contentResolver, MEDIA_IMAGE_URI, MEDIA_IMAGE_ID, MEDIA_IMAGE_ID) { cursor ->
                val id = cursor.getColumnIndexOrThrow(MEDIA_IMAGE_ID)
                while (cursor.moveToNext()) {
                    val imageId = cursor.getLong(id)
                    val contentUri = ContentUris.withAppendedId(MEDIA_IMAGE_URI, imageId)
                    var thumbnail: Bitmap?
                    try {
                        thumbnail =
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                contentResolver.loadThumbnail(contentUri, thumbnailSize, null)
                            } else {
                                MediaStore.Images.Thumbnails.getThumbnail(
                                    contentResolver,
                                    imageId,
                                    MediaStore.Images.Thumbnails.MINI_KIND,
                                    null
                                )
                            }
                    } catch (e: IOException) {
                        thumbnail = null
                    }
                    thumbnail?.let {
                        mediaImages.add(MediaImage(contentUri, thumbnail))
                    }

                }
                cursor.close()
                it.postValue(mediaImages)
            }
        },onResult)

    }

    private fun query(
        contentResolver: ContentResolver,
        uri: Uri,
        id: String,
        vararg projection: String,
        onQuery: (c: Cursor) -> Unit
    ) {
        contentResolver.query(uri, projection, null, null, "$id DESC")?.use {
            onQuery(it)
        }
    }

    private fun<T> createQueryTask(lifecycleOwner: FragmentActivity,
                                   onQuery: (liveData:MutableLiveData<MutableList<T>>)->Unit,
                                   onResult:(data:MutableList<T>)->Unit) {
        val liveData = MutableLiveData<MutableList<T>>()
        liveData.observe(lifecycleOwner){
            onResult(it)
        }
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                onQuery(liveData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * @param maxCount 最大选择数量，不传默认是4张
     */
    fun startImagePicker(activity: FragmentActivity,maxCount:Int?=null){
        activity.startActivity(Intent(activity,ImagePickerActivity::class.java).apply {
            putExtra(Constants.PICK_MAX_COUNT,maxCount)
        })
    }
}