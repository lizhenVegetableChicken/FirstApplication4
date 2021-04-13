package com.example.administrator.firstapplication.Base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import okhttp3.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException


abstract class BaseActivity :AppCompatActivity(){
    val needPermissions = arrayOf<String>(Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SYNC_SETTINGS,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECORD_AUDIO)
    val PERMISSON_REQUESTCODE = 110
    val LOCATION_CODE = 1315
    var isNeedCheck = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initListener()
        initData()

    }
    open fun initMession(){
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    open fun upload(file:File,url:String,childname:String,length:String){
        val size = file.length()//文件长度
        val httpClient = OkHttpClient()
        val mediaType = MediaType.parse("application/octet-stream")//设置类型，类型为八位字节流
        val requestBody = RequestBody.create(mediaType, file)//把文件与类型放入请求体

        val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload", file.getName(), requestBody)//文件名,请求体里的文件
                .build()

        val request = Request.Builder()
                .url(url)
                .post(multipartBody)
                .build()
        val call = httpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                myToast("录音上传失败")
            }

            override fun onResponse(call: Call, response: Response) {
                inserturl(file.name,childname,length)
            }
        })
        //file.delete()
    }
    fun loadDatas(){
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var editor = preference.edit()
        var username = preference?.getString("childname", "")
        var client= OkHttpClient()
        var body= FormBody.Builder()
                .add("username",username)
                .build()
        val request = Request.Builder()
                .url("https://www.tenfee.top/Shouhu/list")
                .post(body)
                .build()
        Thread(Runnable {
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                if(response!!.isSuccessful){
                    var json=response.body()?.string().toString()
                    editor.putString("jsondata",json)
                    editor.commit()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

    open fun loadplaces(){
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var editor = preference.edit()
        var username = preference?.getString("childname", "")
        var client= OkHttpClient()
        var body= FormBody.Builder()
                .add("username",username)
                .build()
        val request = Request.Builder()
                .url("https://www.tenfee.top/Shouhu/getplaces")
                .post(body)
                .build()
        Thread(Runnable {
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                if(response!!.isSuccessful){
                    var json=response.body()?.string().toString()
                    editor.putString("jsonplacedata",json)
                    editor.commit()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

    fun inserturl(url:String,childname:String,length: String){
        var client= OkHttpClient()
        var body= FormBody.Builder()
                .add("username",childname)
                .add("url",url)
                .add("length",length)
                .build()
        val request = Request.Builder()
                .url("https://www.tenfee.top/Shouhu/inserturl")
                .post(body)
                .build()
            Thread(Runnable {
                var response: Response? = null
                try {
                    response = client.newCall(request).execute()
                    if(response!!.isSuccessful){
                    }else{
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()

    }


     open protected fun initData() {
     }

     open protected fun initListener() {
     }

     abstract fun getLayoutId(): Int
     protected fun myToast(msg:String){
         runOnUiThread{
             toast(msg)
         }
     }
    inline fun <reified T: Activity> startActivityAndFinish(){
        startActivity<T>()
        finish()//跳转之后关闭欢迎界面
    }

    fun checkPermissions(permissions: Array<String>) {
        val needRequestPermissonList = findDeniedPermissions(permissions)
        if (null != needRequestPermissonList && needRequestPermissonList.size > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toTypedArray(),
                    PERMISSON_REQUESTCODE)
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     */
    fun findDeniedPermissions(permissions: Array<String>): List<String> {
        var needRequestPermissonList = ArrayList<String>()
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(this,
                            perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(
                            this, perm)) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, paramArrayOfInt: IntArray) {
        when (requestCode) {
            PERMISSON_REQUESTCODE -> if (!verifyPermissions(paramArrayOfInt)) {
                isNeedCheck = false
            }
            LOCATION_CODE -> {
                if (paramArrayOfInt.size > 0 && paramArrayOfInt[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户同意。

                } else {
                    // 权限被用户拒绝了。
                    myToast("定位权限被禁止，相关地图功能无法使用！")
                }

            }
        }

    }


    /**
     * 检测是否说有的权限都已经授权
     */
    fun verifyPermissions(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
 }