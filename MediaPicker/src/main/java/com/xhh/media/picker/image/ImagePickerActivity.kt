package com.xhh.media.picker.image

import android.os.Bundle
import android.util.Size
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.xhh.media.picker.MediaPickerHelper
import com.xhh.media.picker.R
import com.xhh.media.picker.data.Constants
import com.xhh.media.picker.data.ImagePickerStatus
import com.xhh.media.picker.databinding.ActivityImagePickerBinding
import com.xhh.media.picker.databinding.ItemPickerBinding
import com.xhh.media.picker.image.adapter.MediaPickerAdapter

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/8:8:41 AM
 **/
internal class ImagePickerActivity : AppCompatActivity() {
    private lateinit var mDataBinding: ActivityImagePickerBinding
    private val mPickerStatus :ImagePickerStatus by lazy {
        ImagePickerStatus()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPickerStatus.maxCount = intent.getIntExtra(Constants.PICK_MAX_COUNT,4)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker)
        mDataBinding.data = mPickerStatus
        initToolbar(mDataBinding.toolbar)
        initRv(mDataBinding.rv)

    }
    private fun initToolbar(toolbar: MaterialToolbar){
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun initRv(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        val screenWidth: Int = resources.displayMetrics.widthPixels / 4
        MediaPickerHelper.queryImage(this, Size(screenWidth, screenWidth), {
            Snackbar.make(mDataBinding.root, "权限被拒，即将退出页面", Snackbar.LENGTH_SHORT).show()
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

                            }

                            override fun onItemSelectStatusClick(position: Int) {
                                val currentStatus = data[position].status
                                if (mPickerStatus.pickCount==mPickerStatus.maxCount){
                                    if (currentStatus){
                                        data[position].status = !currentStatus
                                    }
                                }else{
                                    data[position].status = !currentStatus
                                    val count = data.filter {
                                        it.status
                                    }.count()
                                    mPickerStatus.pickerEnabled = count != 0
                                    mPickerStatus.pickCount = count
                                    data[position].selectedIndex = "$count"
                                }


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


}