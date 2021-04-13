package com.example.administrator.firstapplication.UI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.RadioGroup
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.R
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RegisterActivity:BaseActivity(){
    override fun getLayoutId(): Int {
        return 1;
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        var umajor:String=""
//        major.setOnCheckedChangeListener { group, i ->
//            if (i==R.id.radio1){
//                umajor="parent";
//            }else if(i==R.id.radio2){
//                umajor="child";
//            }
//        }
//        commit.setOnClickListener {
//            val username = username.text.toString()
//            val userpass = userpass.text.toString()
//            val userpass2 = userpass2.text.toString()
//            if(username==""||userpass==""||userpass2==""){
//                myToast("输入不能为空")
//            } else if(!userpass.equals(userpass2)){
//                myToast("密码输入不一致")
//            }else{
//               //网络提交代码处
//                myToast(umajor)
//                register(username,userpass,umajor)
//            }
//        }
//
//        back.setOnClickListener {
//            startActivityAndFinish<LoginActivity>()
//        }
//    }
//    fun register(username:String,password:String,umajor:String):String{
//        println(umajor)
//        var boolean:String=""
//        var client= OkHttpClient()
//        var body= FormBody.Builder()
//                .add("username",username)
//                .add("password",password)
//                .add("umajor",umajor)
//                .build()
//        val request = Request.Builder()
//                .url("https://www.tenfee.top/Shouhu/register")
//                .post(body)
//                .build()
//        Thread(Runnable {
//            var response: Response? = null
//            try {
//                //回调
//                response = client.newCall(request).execute()
//                var re=response.body()?.string().toString().replace("\"","")
//                if (re=="success") {
//                    startActivityAndFinish<LoginActivity>()
//                    myToast("注册成功")
//                }else if(re=="false"){
//                    myToast("注册失败")
//                }else{
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }).start()
//        println(boolean)
//        return  boolean
//    }
}