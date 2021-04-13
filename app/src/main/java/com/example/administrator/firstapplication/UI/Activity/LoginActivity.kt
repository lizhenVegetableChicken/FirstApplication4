package com.example.administrator.firstapplication.UI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.R
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.jetbrains.anko.toast
import java.io.IOException


class LoginActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.initMession()
        login.setOnClickListener({
            var username: String = username.text.toString()
            var userpass: String = userpass.text.toString()
            //网络请求业务逻辑处
            if (username != "" && userpass != "") {
                    login(username, userpass)
            }else{
                myToast("请输入您的信息")
            }
        })
        register.setOnClickListener({
            startActivityAndFinish<RegisterActivity>()
        })
    }

    fun login(username:String,password:String):String{
        var boolean:String=""
        var client=OkHttpClient()
        var body=FormBody.Builder()
                    .add("username",username)
                    .add("password",password)
                    .build()
        val request = Request.Builder()
                .url("https://www.tenfee.top/Shouhu/login")
                .post(body)
                .build()
        Thread(Runnable {
            var response: Response? = null
            try {
                @SuppressLint("WrongConstant")
                var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
                var editor = preference.edit()
                //回调
                response = client.newCall(request).execute()
                var re=response.body()?.string().toString().replace("\"","")
                if (re=="parent") {
                    editor.putBoolean("isfirst", false)
                    editor.putString("username", username)
                    editor.putBoolean("parent", true)
                    editor.commit()
                    startActivityAndFinish<MainActivity>()
                }else if(re=="child"){
                    editor.putBoolean("isfirst", false)
                    editor.putString("username", username)
                    editor.putBoolean("parent", false)
                    editor.commit()
                    startActivityAndFinish<MapActivity>()
                }else{
                    myToast("密码错误")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
        println(boolean)
        return  boolean
    }
}