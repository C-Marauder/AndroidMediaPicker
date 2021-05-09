package com.xhh.media.picker.image

import android.app.ActivityOptions
import android.app.Application
import android.content.Intent
import android.util.Size
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.xhh.media.picker.MediaPickerHelper
import com.xhh.media.picker.R
import com.xhh.media.picker.data.Constants
import com.xhh.media.picker.data.ImagePickerStatus
import com.xhh.media.picker.data.MediaImage
import com.xhh.media.picker.databinding.ActivityImagePickerBinding
import com.xhh.media.picker.databinding.ActivityTemplateBinding
import com.xhh.media.picker.databinding.ItemPickerBinding
import com.xhh.media.picker.event.EventHelper
import com.xhh.media.picker.image.adapter.MediaPickerAdapter
import com.xhh.media.picker.image.listener.OnBottomViewClickListener
import com.xhh.media.picker.ui.CoreActivity

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/8:8:41 AM
 **/
internal class ImagePickerActivity : CoreActivity<ActivityImagePickerBinding>(),OnBottomViewClickListener {
    private lateinit var mViewModel: MediaPickerViewModel
    override val layoutId: Int
        get() = R.layout.activity_image_picker

    override fun onCreated() {
        mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MediaPickerViewModel::class.java)
        mViewModel.mPickerStatus.maxCount = intent.getIntExtra(Constants.PICK_MAX_COUNT,4)
    }
    override fun onTemplateBindingCreated(dataBinding: ActivityTemplateBinding) {


    }

    override fun onContentBindingCreated(dataBinding: ActivityImagePickerBinding) {
        dataBinding.data = mViewModel.mPickerStatus
        dataBinding.listener = this
        initRv(dataBinding.rv)
    }


    private fun initRv(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        val screenWidth: Int = resources.displayMetrics.widthPixels / 4
        MediaPickerHelper.queryImage(this, Size(screenWidth, screenWidth), {
            Snackbar.make(mTemplateBinding.root, "权限被拒，即将退出页面", Snackbar.LENGTH_SHORT).show()
        }) {data->
            rv.adapter = MediaPickerAdapter(data,
                onCreateItemView = {
                    R.layout.item_picker
                },
                onCreateViewHolder = {
                    dataBinding ->

                    if (dataBinding is ItemPickerBinding){
                        dataBinding.callback = object :OnItemCallback{
                            override fun onItemClick(position: Int) {
                               val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@ImagePickerActivity,dataBinding.root,"$position")
                                startActivity(Intent(this@ImagePickerActivity,PreviewActivity::class.java).apply {
                                    putExtra(Constants.PREVIEW_INDEX,position)
                                },options.toBundle())
                            }

                            override fun onItemSelectStatusClick(position: Int) {

                                mViewModel.updateItemStatus(data,position)


                            }

                        }
                    }
                },
                onBind = { dataBinding, position ->
                    if (dataBinding is ItemPickerBinding){
                        dataBinding.data = data[position].apply {
                            this.position = position
                        }
                    }
                })
        }

    }

    override fun onPreviewClick() {

    }

    @Suppress("UNCHECKED_CAST")
    override fun onCompleteClick() {
        EventHelper.postImageValue(mViewModel.mSelected)
        finish()


    }




}

internal class MediaPickerViewModel(application: Application) : AndroidViewModel(application) {

    val mSelected:MutableList<MediaImage> by lazy {
        mutableListOf()
    }
    val mPickerStatus :ImagePickerStatus by lazy {
        ImagePickerStatus()
    }

    fun updateItemStatus(data:MutableList<MediaImage>,position:Int){
        val item = data[position]
        val currentCount = mSelected.count()
        if (mPickerStatus.maxCount == currentCount && !item.status){
            return
        }
        item.status = !item.status
        mSelected.firstOrNull {
            it.originalUri == item.originalUri
        }.let {
            if (it == null){

                mSelected.add(item)
            }else{
                item.selectedIndex = null
                mSelected.remove(item)
            }
        }
        val selectedCount = mSelected.count()
        mPickerStatus.pickerEnabled = selectedCount != 0
        mPickerStatus.pickCount = selectedCount
        if (mSelected.isNotEmpty()){
            mSelected.forEachIndexed { index, mediaImage ->
                mediaImage.selectedIndex = "${index+1}"
            }
        }


    }
}