package com.xhh.mediapicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.databinding.DataBindingUtil
import com.xhh.media.picker.MediaPickerHelper
import com.xhh.mediapicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        activityMainBinding.listener = this

    }

    fun startVideoPage(){
        startActivity(Intent(this,MediaActivity::class.java).apply {
            putExtra("type","1")
        })
    }

    fun startImagePage(){
        MediaPickerHelper.startImagePicker(this)
    }
    fun startAudioPage(){
        startActivity(Intent(this,MediaActivity::class.java).apply {
            putExtra("type","3")
        })
    }
}