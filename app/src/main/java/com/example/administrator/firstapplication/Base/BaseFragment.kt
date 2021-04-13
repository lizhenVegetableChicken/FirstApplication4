package com.example.administrator.firstapplication.Base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast


abstract class BaseFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initData()
        init()
    }

    protected fun init() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  initView()
        initData()
    }

    abstract fun initView(): View?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
    }

    open protected fun initData() {
    }

    open protected fun initListener() {
    }

    protected  fun myToast(msg:String){
        context?.runOnUiThread {
            toast(msg)
            }
        }
}