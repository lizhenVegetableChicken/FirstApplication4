package com.example.administrator.firstapplication.Util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

open class GetIntenet {

    fun <T> getIntenet(url:String):List<T>?{
        var list= mutableListOf<T>()
        Thread(object :Runnable{
            override fun run() {
                var client= OkHttpClient()
                val request= Request.Builder()
                        .url(url)
                        .get()
                        .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }
                    override fun onResponse(call: Call, response: Response) {
                        println(response?.body()?.string())
                        var result=response?.body()?.string()
                        var gson= Gson()
                        list=gson.fromJson<MutableList<T>>(result,object:TypeToken<List<T>>(){}.type)
                    }
                })
            }
        })
        return list
    }

}