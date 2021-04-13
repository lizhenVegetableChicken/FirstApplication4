package com.example.administrator.firstapplication.UI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.baidu.location.LocationClient
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.Util.ToolBarManager
import kotlinx.android.synthetic.main.activity_shoumap.*
import org.jetbrains.anko.find
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MarkerOptions
import com.example.administrator.firstapplication.Bean.Place
import com.example.administrator.firstapplication.Bean.Url
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ShouMap:BaseActivity(),ToolBarManager {
    var points =ArrayList<LatLng>()
    var list:List<Place>?=null
    override val toolbar: Toolbar by lazy{
        find<Toolbar>(R.id.toolbar)
    }
    var mBaiduMap: BaiduMap? = null
    var locationClient: LocationClient? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_shoumap
    }
    override fun onDestroy() {
        locationClient?.stop();
        mBaiduMap?.setMyLocationEnabled(false);
        mapView2?.onDestroy();
        super.onDestroy()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backshoumap.setOnClickListener {
            finish()
        }
        @SuppressLint("WrongConstant")
        var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
        var json=preference.getString("jsonplacedata","")
        list= Gson().fromJson<ArrayList<Place>>(json,object: TypeToken<ArrayList<Place>>(){}.type)
        list?.forEach {
            points.add(LatLng(it.latitude,it.longtitude))
        }
    }
    override fun initData() {
        initShouMapBar()
    }
    override fun onStart() {
        super.onStart()
        mBaiduMap = mapView2.map
        //mBaiduMap?.isMyLocationEnabled = true
        mBaiduMap?.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //mBaiduMap?.setMyLocationConfiguration(MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null))
        //initMap()
        //isFirst=true
        if(points.size>0) {
            val mMapStatus = MapStatus.Builder()
                    .target(points.get(points.size - 1))//需要注意的是new LatLng(double latitude,double longitude)这里传入的依次是维度、经度，而不是我们常说的经纬度的这个顺序，如果经纬度填反，容易出现地图空白的情况（刚接触的时候就踩了这个坑，记录一下）
                    .zoom(13f)
                    .build()
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            val mMapStatusUpdate = MapStatusUpdateFactory
                    .newMapStatus(mMapStatus)
            //改变地图状态
            val bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.timg)


            val option = MarkerOptions().position(points.get(points.size - 1))  //设置Marker的位置
                    .icon(bitmap)  //设置Marker图标
                    .zIndex(13)  //设置Marker所在层级
                    .draggable(true)  //设置手势拖拽
            //在地图上添加Marker，并显示
            mBaiduMap?.addOverlay(option)

            mBaiduMap?.setMapStatus(mMapStatusUpdate)

        }
//构建分段颜色索引数组
        if(points.size>1) {
            var colors = ArrayList<Int>();
            colors.add(Integer.valueOf(Color.GREEN));

            var ooPolyline = PolylineOptions().width(10)
                    .colorsValues(colors).points(points);

            var mPolyline = mBaiduMap?.addOverlay(ooPolyline);
        }
    }
}