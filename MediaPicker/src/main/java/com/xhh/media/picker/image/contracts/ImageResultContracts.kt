package com.xhh.media.picker.image.contracts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.xhh.media.picker.data.Constants
import com.xhh.media.picker.data.MediaImage
import com.xhh.media.picker.image.ImagePickerActivity

/**
 *   @Author:小灰灰
 *   @Time:2021/5/8
 *   @Desc:
 */
internal class ImageResultContracts: ActivityResultContract<Int, MutableList<Uri>>() {


    @Suppress("UNCHECKED_CAST")
    override fun parseResult(resultCode: Int, intent: Intent?): MutableList<Uri> {
        if (intent == null || resultCode!=Activity.RESULT_OK){
            return mutableListOf()
        }
        val result = intent.getParcelableArrayExtra(Constants.RESULT_MEDIA_IMAGE) ?: return mutableListOf()
        return  result as MutableList<Uri>
    }

    override fun createIntent(context: Context, input: Int?): Intent {
        return Intent(context,ImagePickerActivity::class.java).putExtra(Constants.PICK_MAX_COUNT,input)
    }
}