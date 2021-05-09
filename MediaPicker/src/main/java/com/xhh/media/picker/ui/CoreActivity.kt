package com.xhh.media.picker.ui

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.appbar.MaterialToolbar
import com.xhh.media.picker.R
import com.xhh.media.picker.databinding.ActivityTemplateBinding

/**
 *   @Author:小灰灰
 *   @Time:2021/5/9
 *   @Desc:
 */
internal abstract class CoreActivity<DB:ViewDataBinding> :AppCompatActivity(){
    abstract val layoutId:Int
    protected lateinit var mTemplateBinding: ActivityTemplateBinding
    protected lateinit var mContentBinding:DB
    abstract fun onCreated()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreated()
        mTemplateBinding =  DataBindingUtil.setContentView(this, R.layout.activity_template)
        initToolbar(mTemplateBinding.toolbar)
        onTemplateBindingCreated(mTemplateBinding)
        mContentBinding =  DataBindingUtil.inflate(layoutInflater,layoutId,mTemplateBinding.coordinator,false)
        mTemplateBinding.coordinator.addView(mContentBinding.root,1)
        onContentBindingCreated(mContentBinding)
    }
    abstract fun onTemplateBindingCreated(dataBinding: ActivityTemplateBinding)
    abstract fun onContentBindingCreated(dataBinding:DB)
    private fun initToolbar(toolbar: MaterialToolbar){
        toolbar.setNavigationOnClickListener {
            Log.e("==","===111111111111>")
            onBackPressed()
        }
    }
}