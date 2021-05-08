package com.xhh.mediapicker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xhh.media.picker.MediaPickerHelper
import com.xhh.media.picker.data.MediaVideo
import com.xhh.mediapicker.data.Video
import com.xhh.mediapicker.databinding.ActivityMediaBinding
import com.xhh.mediapicker.databinding.ItemMediaBinding

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/7:11:48 AM
 **/
class MediaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = intent.getStringExtra("type")
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMediaBinding>(this, R.layout.activity_media)
        initRV(dataBinding.rv)
        dataBinding.title = when (type) {
            "1" -> {
                getAllVideo(dataBinding)
                "全部视频"
            }
            "3" -> {
                Log.e("==", "==>>>>>")
                getAllAudio(dataBinding)
                "全部音频"
            }
            else -> ""
        }

    }

    private fun initRV(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)

    }

    private fun getAllVideo(dataBinding: ActivityMediaBinding) {
        MediaPickerHelper.queryVideo(this, onDenied = {
            Snackbar.make(dataBinding.root, "无读写权限", Snackbar.LENGTH_SHORT).show()
        }) {
            if (!it.isNullOrEmpty()) {
                dataBinding.rv.adapter = MediaAdapter(it, { holder ->

                }) { dataBinding, holder, position ->
                    dataBinding.video = it[position]
                }

            } else {
                Snackbar.make(dataBinding.root, "未发现视频数据", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAllAudio(dataBinding: ActivityMediaBinding) {
        MediaPickerHelper.queryAudio(this, onDenied = {
            Snackbar.make(dataBinding.root, "无读写权限", Snackbar.LENGTH_SHORT).show()
        }) {
            if (!it.isNullOrEmpty()) {
                dataBinding.rv.adapter = MediaAdapter(it, { holder ->

                }) { dataBinding, holder, position ->
                    dataBinding.audio = it[position]
                }

            } else {
                Snackbar.make(dataBinding.root, "未发现音频数据", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}

class MediaAdapter<T>(
    private val data: MutableList<T>,
    private val onViewHolderCreated: (holder: MediaViewHolder) -> Unit,
    private val onBind: (dataBinding: ItemMediaBinding, holder: MediaViewHolder, position: Int) -> Unit
) : RecyclerView.Adapter<MediaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = DataBindingUtil.inflate<ItemMediaBinding>(
            layoutInflater,
            R.layout.item_media,
            parent,
            false
        )
        return MediaViewHolder(dataBinding.root, dataBinding, onViewHolderCreated)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        onBind(holder.dataBinding, holder, position)
    }

    override fun getItemCount(): Int = data.size

}

class MediaViewHolder(
    itemView: View,
    val dataBinding: ItemMediaBinding,
    private val onViewHolderCreated: (holder: MediaViewHolder) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    init {
        onViewHolderCreated(this)
    }
}