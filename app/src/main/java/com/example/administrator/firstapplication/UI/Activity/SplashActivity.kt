package com.example.administrator.firstapplication.UI.Activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.view.View
import com.example.administrator.bluecar.BlueteethActivity
import com.example.administrator.bluecar.bluetooth.BleSppActivity
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.Base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import java.lang.Exception
import java.lang.RuntimeException

class SplashActivity : BaseActivity(), ViewPropertyAnimatorListener {

    override fun onAnimationEnd(view: View?) {
        startActivityAndFinish<MainActivity>()
    }

    override fun onAnimationCancel(view: View?) {
    }

    override fun onAnimationStart(view: View?) {
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initData() {
        ViewCompat.animate(imageview).scaleX(1.0f).scaleY(1.0f).setListener(this).setDuration(2000)
    }

}