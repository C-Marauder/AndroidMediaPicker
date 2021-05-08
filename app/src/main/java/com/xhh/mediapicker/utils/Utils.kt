package com.xhh.mediapicker.utils

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.xhh.mediapicker.R

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/7:2:19 PM
 **/

@BindingAdapter("thumbnail")
fun AppCompatImageView.setLocalImageUri(thumbnail: Bitmap?){

    Glide.with(this).load(thumbnail).placeholder(R.drawable.icon_album).centerCrop().into(this)

}