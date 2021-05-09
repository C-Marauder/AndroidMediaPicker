package com.xhh.media.picker.data

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import com.xhh.media.picker.BR
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/4/30:4:14 PM
 **/
internal interface Constants{
    companion object{
        const val PICK_MAX_COUNT:String = "pick_max_count"
        const val RESULT_MEDIA_IMAGE:String = "result_image"
    }
}

/**
 * @param originalUri 图片的原始地址
 * @param thumbnail 图片的缩略图
 */
data class MediaImage(val originalUri: Uri, val thumbnail: Bitmap?):BaseObservable() {

    @get:Bindable
    var status:Boolean =false
    set(value) {
        field = value
        notifyPropertyChanged(BR.status)
    }
    @get:Bindable
    var selectedIndex:String?=null
    set(value) {
        field =value
        notifyPropertyChanged(BR.selectedIndex)
    }
    @IgnoredOnParcel
    @get:Bindable
    var position:Int?=null
    set(value) {
        field = value
        notifyPropertyChanged(BR.position)
    }
}

data class MediaVideo(val uri: Uri,val thumbnail: Bitmap?,val duration:Int,val durationFormat:String,val length:Long,val size:String,val name:String)
data class MediaAudio(val uri: Uri,val cover: Bitmap?,val duration:Int,val durationFormat:String,val length:Long,val size:String,val artist:String,val name:String)

internal  class ImagePickerStatus(var maxCount:Int = 4):BaseObservable(){
    @get:Bindable
    var pickerEnabled:Boolean = false
    set(value) {
        field = value
        notifyPropertyChanged(BR.pickerEnabled)
    }
    @get:Bindable
    var pickCount:Int = 0
    set(value) {
        field = value
        pickerCount = "完成"+if (value == 0){
            ""
        }else{
            "($value/$maxCount)"
        }
        notifyPropertyChanged(BR.pickCount)
    }
    @get:Bindable
    var pickerCount:String="完成"
    set(value) {
        field = value
        notifyPropertyChanged(BR.pickerCount)
    }

    fun checkPick(status:Boolean,onPick:()->Unit){
        if (status){
            onPick()
        }else{
            if (pickCount<maxCount){
                onPick()
            }
        }

    }
}