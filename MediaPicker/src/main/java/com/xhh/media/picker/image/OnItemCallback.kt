package com.xhh.media.picker.image

import java.text.ParsePosition

/**
 *  @Author  ： 小灰灰
 *  @Desc ：
 * @Time : 2021/5/8:3:07 PM
 **/
internal interface OnItemCallback {
    fun onItemClick(position: Int)
    fun onItemSelectStatusClick(position: Int)

}