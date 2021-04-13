package com.example.administrator.firstapplication.UI.Activity

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.example.administrator.bluecar.mscv5plusdemo.TtsDemo
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.Util.FragMentUtil
import com.example.administrator.firstapplication.Util.ToolBarManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find


class MainActivity : BaseActivity(),ToolBarManager{

    override val toolbar: Toolbar by lazy{
            find<Toolbar>(R.id.toolbar)
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
    override fun initData() {
        initMainToolBar()
    }
    override fun initListener() {
        bottomBar.setOnTabSelectListener {
            val transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container,FragMentUtil.fragmentUtil.getFragment(it),it.toString())
            transaction.commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onStart() {
        super.onStart()
//        loadDatas()
//        loadplaces()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        /*startActivityAndFinish<TtsDemo>()*/
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
