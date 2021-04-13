package com.example.administrator.firstapplication.UI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.Toolbar
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.Bean.Time
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.Util.ToolBarManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_mapactivity.*
import kotlinx.android.synthetic.main.fragment_map.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MapActivity:BaseActivity(),ToolBarManager{
    override val toolbar: Toolbar by lazy{
        find<Toolbar>(R.id.toolbar)
    }
    var mBaiduMap: BaiduMap? = null
    var locationClient: LocationClient? = null
    var isFirst: Boolean=true
    var Latitude: Double = 0.0
    var Longtitude: Double = 0.0
    var Latitude2:Double=0.0
    lateinit var soundfile:File
    var Longtitude2: Double = 0.0
    var freetime=10
    var soundtime=10
    override fun getLayoutId(): Int {
        return R.layout.activity_mapactivity
    }

    override fun onStart() {
        super.onStart()
        initMapBar()
        mBaiduMap = mapView3.map
        mBaiduMap?.isMyLocationEnabled = true
        mBaiduMap?.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap?.setMyLocationConfiguration(MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))
        initMap()
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        freetime = preference.getInt("freetime",10)
        soundtime=preference.getInt("soundtime",10)
        val executor = Executors.newScheduledThreadPool(1)
        executor.scheduleWithFixedDelay(
                EchoServer(),
                2,
                (freetime+soundtime).toLong(),
                java.util.concurrent.TimeUnit.SECONDS)
//        var dir = File(Environment.getExternalStorageDirectory(),"Sounds")
//        var soundFile = File(dir,"a.amr");
//        this.upload(soundFile,"https://www.tenfee.top/Shouhu/upload","child",10.toString())
    }

    fun initMap() {

        locationClient = LocationClient(this)
//声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        val myLocationListener = MyLocationListener()
//注册监听函数
        locationClient?.registerLocationListener(myLocationListener)
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02")
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000)
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(true)
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = true
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true)
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
//可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode()
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient?.locOption = locationOption
//开始定位
        locationClient?.start()

    }

    override fun onResume() {
        super.onResume()
        select()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        out.setOnClickListener {
            @SuppressLint("WrongConstant")
            var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
            var editor = preference.edit()
            editor.clear()
            editor.commit()
            startActivity<LoginActivity>()
            finish()
        }
    }

    override fun onDestroy() {
        locationClient?.stop();
        mBaiduMap?.setMyLocationEnabled(false);
        mapView3?.onDestroy();
        super.onDestroy()
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            if (location == null || mapView3 == null) {
                return
            }
            var ll = LatLng(location.getLatitude(),location.getLongitude())
            val converter = CoordinateConverter()
            converter.from(CoordinateConverter.CoordType.GPS)
            // sourceLatLng待转换坐标
            converter.coord(ll)
            val desLatLng = converter.convert()
            val locData = MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(desLatLng.latitude)
                    .longitude(desLatLng.longitude)
                    .build()
            mBaiduMap?.setMyLocationData(locData)

            if(isFirst){
                var builder: MapStatus.Builder  =  MapStatus.Builder();
                builder.target(desLatLng).zoom(16.0f);
                mBaiduMap?.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirst=false
            }

            Latitude = desLatLng.latitude
            Longtitude = desLatLng.longitude
            println(Latitude.toString()+" "+Longtitude.toString())


        }

    }

    fun select(){//获取录音市场及间隔时长
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var username = preference.getString("username", "")
        var editor = preference.edit()

        var client= OkHttpClient()
        var body= FormBody.Builder()
                .add("username",username)
                .build()
        val request = Request.Builder()
                .url("https://www.tenfee.top/Shouhu/time")
                .post(body)
                .build()
        Thread(Runnable {
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                if(response!!.isSuccessful){
                    var json=response.body()?.string().toString()
                    var gson=Gson()
                    try {
                        var time=gson.fromJson(json,Time::class.java)
                        editor.putInt("soundtime",time.soundtime)
                        editor.putInt("freetime",time.freetime)
                        editor.commit()
                        println(time.freetime.toString()+" ------------------- "+time.soundtime.toString())
                    }catch (e:Exception){
                        throw RuntimeException()
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

    fun place(latitude:Double,longtitude:Double){
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var username = preference.getString("username", "")
        var editor = preference.edit()

        var client= OkHttpClient()
        var body= FormBody.Builder()
                .add("username",username)
                .add("latitude",latitude.toString())
                .add("longtitude",longtitude.toString())
                .build()
        val request = Request.Builder()
                .url("https://www.tenfee.top/Shouhu/place")
                .post(body)
                .build()
        if(this.Latitude2==latitude&&this.Longtitude2==longtitude){
        }else{
        Thread(Runnable {
            var response: Response? = null
            try {
                response = client.newCall(request).execute()
                if(response!!.isSuccessful){
                    this.Latitude2=latitude
                    this.Longtitude2=longtitude
                }else{
                    myToast("地图位置上传失败")
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
        }

    }

    inner class EchoServer : Runnable {
        lateinit var mr:MediaRecorder
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var username = preference.getString("username", "")
        override fun run() {
            try {
                    var dir = File(Environment.getExternalStorageDirectory(),"Sounds");
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    var soundFile = File(dir,SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()).toString().replace(" ","_")+".amr")
                    if(!soundFile.exists()){
                    soundFile.createNewFile();
                    soundfile=soundFile
                    }
                    mr=MediaRecorder()
                    mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
                    mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
                    mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
                    mr.setOutputFile(soundFile.getAbsolutePath( ));
                    mr.setMaxDuration(soundtime*1000)
                    mr.prepare();
                    mr.start();  //开始录制
                    mr.setOnInfoListener(object:MediaRecorder.OnInfoListener{
                        override fun onInfo(m: MediaRecorder?, what: Int, extra: Int) {
                            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                                mr.stop()
                                mr.release()
                                //this@MapActivity.upload(soundfile.absolutePath,"http://127.0.0.1:8080//Shouhu/upload")
                                username?.let { this@MapActivity.upload(soundfile,"http://127.0.0.1:8080//Shouhu/upload", it,soundtime.toString()) }
                                if(Latitude!=0.0){
                                    place(Latitude,Longtitude)
                                }
                            }
                        }
                    })
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }


}
