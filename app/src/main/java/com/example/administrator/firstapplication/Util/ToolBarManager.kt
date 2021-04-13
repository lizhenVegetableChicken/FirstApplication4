package com.example.administrator.firstapplication.Util

import android.content.Intent
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.UI.Activity.SettingActivity

interface ToolBarManager {
    val toolbar:Toolbar
    fun initSettingToolbar(){
        toolbar.setTitle("设置界面")
    }
    fun initMapBar(){
        toolbar.setTitle("瞳行")
    }
    fun initShouMapBar(){
        toolbar.setTitle("查看轨迹")
    }
    fun initMainToolBar(){
        toolbar.setTitle("瞳行")
        toolbar.inflateMenu(R.menu.main)
        toolbar.setOnMenuItemClickListener{
            toolbar.context.startActivity(Intent(toolbar.context, SettingActivity::class.java))
            true
        }
//        toolbar.setOnMenuItemClickListener(object :Toolbar.OnMenuItemClickListener{
//            override fun onMenuItemClick(item: MenuItem?): Boolean {
//                when(item?.itemId){
//                    R.id.setting->{
//                        toolbar.context.startActivity(Intent(toolbar.context,SettingActivity::class.java))
//                    }
//                }
//                return true
//            }
//
//        })


    }
}