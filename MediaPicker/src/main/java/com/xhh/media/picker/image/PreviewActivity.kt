package com.xhh.media.picker.image

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.xhh.media.picker.MediaPickerHelper
import com.xhh.media.picker.R
import com.xhh.media.picker.data.Constants
import com.xhh.media.picker.databinding.ActivityPreviewBinding
import com.xhh.media.picker.databinding.ActivityTemplateBinding
import com.xhh.media.picker.databinding.ItemMediaImageBinding
import com.xhh.media.picker.image.adapter.MediaPickerAdapter
import com.xhh.media.picker.ui.CoreActivity

/**
 *   @Author:小灰灰
 *   @Time:2021/5/9
 *   @Desc:
 */
internal class PreviewActivity : CoreActivity<ActivityPreviewBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_preview
    private var mIndex:Int = 0
    override fun onCreated() {
        mIndex = intent.getIntExtra(Constants.PREVIEW_INDEX,0)
    }

    override fun onTemplateBindingCreated(dataBinding: ActivityTemplateBinding) {

    }

    override fun onContentBindingCreated(dataBinding: ActivityPreviewBinding) {
        dataBinding.viewPager.transitionName = "$mIndex"
        init(dataBinding.viewPager)
    }

    private fun init(viewPager: ViewPager2) {
        viewPager.adapter = MediaPickerAdapter(MediaPickerHelper.mImageCache,
            onCreateItemView = {
                R.layout.item_media_image
            },
            onCreateViewHolder = {

            },
            onBind = { dataBinding, position ->
                if (dataBinding is ItemMediaImageBinding){
                    dataBinding.data = MediaPickerHelper.mImageCache[position]
                }
            })
        viewPager.currentItem = mIndex
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()
    }

}