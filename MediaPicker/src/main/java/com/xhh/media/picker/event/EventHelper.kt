package com.xhh.media.picker.event

import androidx.lifecycle.*
import com.xhh.media.picker.data.MediaImage

/**
 *   @Author:小灰灰
 *   @Time:2021/5/9
 *   @Desc:
 */
internal object EventHelper {

    private val mImageEventMap: HashMap<String, MutableLiveData<MutableList<MediaImage>>> by lazy {
        hashMapOf()
    }
    private  var mImageLiveData: MutableLiveData<MutableList<MediaImage>>?=null

    fun observeImage(
        lifecycleOwner: LifecycleOwner,
        onResult: (data: MutableList<MediaImage>) -> Unit) {
        mImageLiveData = MutableLiveData()
        mImageLiveData?.observe(lifecycleOwner, Observer {
            onResult(it)
            mImageLiveData?.removeObservers(lifecycleOwner)
            mImageLiveData = null
        })

    }

    fun postImageValue(data: MutableList<MediaImage>) {
        mImageLiveData?.value = data
    }
}