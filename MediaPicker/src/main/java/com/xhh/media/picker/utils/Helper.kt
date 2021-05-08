package com.xhh.media.picker.utils

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/8:2:35 PM
 **/

@BindingAdapter("thumbnail")
internal fun AppCompatImageView.setThumbnail(bitmap: Bitmap){
    Glide.with(this).load(bitmap).into(this)
}
@BindingAdapter("image_selector")
internal fun AppCompatImageView.setIconStatus(selected:Boolean){
    isSelected = selected
}