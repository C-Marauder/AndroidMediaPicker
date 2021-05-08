package com.xhh.media.picker.image.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/8:9:42 AM
 **/
class MediaPickerAdapter<T>(
    private val data: MutableList<T>,
    private val onCreateItemView:(position:Int)->Int,
    private val onCreateViewHolder: (dataBinding:ViewDataBinding)->Unit,
    private val onBind: (dataBinding:ViewDataBinding,position:Int) -> Unit
) : RecyclerView.Adapter<MediaPickerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaPickerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater,viewType,parent,false)
        return MediaPickerViewHolder(onCreateViewHolder,dataBinding,dataBinding.root)
    }

    override fun onBindViewHolder(holder: MediaPickerViewHolder, position: Int) {
        onBind(holder.dataBinding,position)
    }

    override fun getItemViewType(position: Int): Int {
        return onCreateItemView(position)
    }
    override fun getItemCount(): Int = data.size

}

class MediaPickerViewHolder( private val onCreateViewHolder: (dataBinding:ViewDataBinding) -> Unit,val dataBinding: ViewDataBinding,itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        onCreateViewHolder(dataBinding)
    }
}