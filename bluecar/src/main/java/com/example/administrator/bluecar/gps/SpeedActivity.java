package com.example.administrator.bluecar.gps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.administrator.bluecar.R;

public class SpeedActivity extends AppCompatActivity {

    private TextView txtJingd;
    private TextView txtWeid;
    private TextView txtHaib;
    private TextView txtSud;
    private TextView txtChangd;
    private TextView txtAllChangd;
    private TextView txtShij;
    private TextView txtPinjsd;
    private TextView txtDingwcs;
    private Button btnGo;

    private Boolean blnGo = false;
    private Integer iCount = 0;

    private LocationManager lm = null;
    Context ctx;
    private String TAG = "LANHAI:";

    String ts = "";
    String te = "";
    String tl = "";
    long allsencond = 0;
    long lastsencond = 0;

    double v1 = 0.36;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        //获得ActivityManager服务的对象
        ctx = this.getApplicationContext();
        lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        txtJingd = (TextView) findViewById(R.id.txtJingd);
        txtWeid = (TextView) findViewById(R.id.txtWeid);
        txtHaib = (TextView) findViewById(R.id.txtHaib);
        txtSud = (TextView) findViewById(R.id.txtSud);
        txtChangd = (TextView) findViewById(R.id.txtChangd);
        txtAllChangd = (TextView) findViewById(R.id.txtAllChangd);
        txtShij = (TextView) findViewById(R.id.txtShij);
        txtPinjsd = (TextView) findViewById(R.id.txtPinjsd);
        txtDingwcs = (TextView) findViewById(R.id.txtDingwcs);
        btnGo = (Button) findViewById(R.id.btnGo);

        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
        ts = sdf.format(new Date());//按以上格式 将当前时间转换成字符串
        Log.i(TAG, "STARTDATE:" + ts);

        //初始化（创建）日志文件
        Logfile.oLog();


        //根据读取的日志文件测试距离函数GetDistance
        //TestGetDistance();
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                blnGo = !blnGo;
                iCount = 0;

                txtJingd.setText("0.0");
                txtWeid.setText("0.0");
                txtHaib.setText("109");
                txtSud.setText("40");
                txtChangd.setText("40");
                txtAllChangd.setText("80");
                txtShij.setText("0");
                txtPinjsd.setText("30");
                txtDingwcs.setText("0");


                if (blnGo) {
                    btnGo.setText("STOP");

                    SimpleDateFormat sdf = null;
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
                    ts = sdf.format(new Date());//按以上格式 将当前时间转换成字符串
                    Log.i(TAG, "STARTDATE:" + ts);
                    Logfile.wLog("STARTDATE:" + ts + "\r\n");
                    tl = ts;

                    // 查找到服务信息
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setAltitudeRequired(true);
                    criteria.setBearingRequired(true);
                    criteria.setCostAllowed(false);
                    criteria.setPowerRequirement(Criteria.POWER_HIGH);


                    if (ActivityCompat.checkSelfPermission(SpeedActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SpeedActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 通过GPS获取位置

                    //4samsung
                    if (location != null){
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        double atd = location.getSpeed();
                        txtJingd.setText(String.valueOf(lng));     //"经度："+
                        txtWeid.setText(String.valueOf(lat));      //"纬度："+
                        txtHaib.setText(String.valueOf(atd));      //"返回速度："+

                        sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
                        te=sdf.format(new Date());
                        Log.i(TAG,"ENDDATE:"+te);
                        Logfile.wLog("ENDDATE:"+te+"\r\n");

                        try {
                            allsencond=(sdf.parse(te).getTime()-sdf.parse(ts).getTime())/1000;
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Logfile.wLog(Logfile.FormatStackTrace(e));
                        }

                        txtShij.setText(String.valueOf(allsencond));      //"时间："+
                        Logfile.wLog("时间:"+String.valueOf(allsencond)+"\r\n");
                        Log.i(TAG,"时间:"+String.valueOf(allsencond)+"\r\n");

                    }else{
                        location =lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null){
                            //txtJingd.setText(location.toString());
                        }
                    }
                }
                else
                {
                    btnGo.setText("GO");
                    lm.removeUpdates(locListener);
                }

            }
        });

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,9000, 1, locListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }



    //检查gps定位是否开启
    public boolean IsGpsOn()
    {
        boolean blnGps=false;

        //获得手机是不是设置了GPS开启状态true：gps开启，false：GPS未开启
        boolean GPS_status = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (GPS_status){
            blnGps=true;
        }else{
            blnGps=false;
        }

        return blnGps;
    }

    //启动gps配置界面
    public void GpsSet()
    {
        //4samsung
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);

        //如果还是没有打开gps，就关闭程序
        if(IsGpsOn())
        {

        }
        else
        {
            new AlertDialog.Builder(this).setTitle("信息").setMessage("GPS必须开启才能定位").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Logfile.cLog();
                            SpeedActivity.this.finish();
                            System.exit(0);
                        }
                    }).show();
        }
    }

    //按backup键退出程序
    @Override
    public void onBackPressed()
    {

        new AlertDialog.Builder(this).setTitle("信息").setMessage("退出?").setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Logfile.cLog();
                        SpeedActivity.this.finish();
                        System.exit(0);

                        //android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).show();

    }

    //注册的定位监听函数
    LocationListener locListener=new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            iCount++;

            Log.i(TAG,location.toString());
            Log.i(TAG,"定位次数:"+String.valueOf(iCount));
            Logfile.wLog("\r\n定位次数:"+String.valueOf(iCount)+"\r\n");
            //Logfile.wLog(location.toString()+"\n");

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            double atd = location.getSpeed();
            double dchangd=0.0;

            Log.i(TAG,"纬度:"+String.valueOf(lat));
            Log.i(TAG,"经度:"+String.valueOf(lng));
            Log.i(TAG,"返回速度:"+String.valueOf(atd));
            Logfile.wLog("纬度:"+String.valueOf(lat)+"\r\n");
            Logfile.wLog("经度:"+String.valueOf(lng)+"\r\n");
            Logfile.wLog("返回速度:"+String.valueOf(atd)+"\r\n");

            if (iCount!=1){

                double lat1 =Double.parseDouble(txtWeid.getText().toString());
                double lng1 =Double.parseDouble(txtJingd.getText().toString());

                //方法一：结果参考当前目录下日志文件：fangfa1.txt
                //长度:60.038391933172356
                //dchangd=111.12*cos{1/[sinΦAsinΦB十 cosΦAcosΦBcos(λB-λA)]}
                //dchangd=111.12*Math.cos(1/(Math.sin(lng)*Math.sin(lng1)+Math.cos(lng)*Math.cos(lng1)*Math.cos(lat1-lat)));

                //方法二：结果参考当前目录下日志文件：fangfa2.txt
                //距离:7647.241717104357
//    			dchangd=getDistance(lng1,lat1,lng,lat);
//    			Logfile.wLog("dchangd:"+String.valueOf(dchangd)+"\n");

                //方法三：
                //距离:7655.0
                //http://hi.baidu.com/widebright/item/d95cb7f3980e8a0a84d278c5
                //public static double GetDistance(double lat1, double lng1, double lat2, double lng2)

//	    	        纬度:23.13442011
//   	 	        经度:113.35193399
//   	 	        纬度:23.1344553
//    		        经度:113.35192364
//    		        纬度:23.13447677
//    		        经度:113.35193923

//    	        0.004058084674400899
//    	        0.002873879060827857
//    	        111.31949079327356
//    	        111.31949079327356

//    	        Log.i(TAG,String.valueOf(GetDistance(23.1344553,113.35192364,23.13442011,113.35193399)));
//    	        Log.i(TAG,String.valueOf(GetDistance(23.13447677,113.35193923,23.1344553,113.35192364)));
//    	        Log.i(TAG,String.valueOf(GetDistance(0,1,0,0)));
//    	        Log.i(TAG,String.valueOf(GetDistance(1,0,0,0)));


                dchangd=GetJuli(lat1,lng1,lat,lng);
                Logfile.wLog("GetDistance(lat1,lng1,lat,lng):\r\n"+String.valueOf(lat1)+","
                        +String.valueOf(lng1)+";"+String.valueOf(lat)+","+String.valueOf(lng)+"\r\n");
                Logfile.wLog("距离dchangd:"+String.valueOf(dchangd)+"\r\n");

                txtAllChangd.setText(String.valueOf(dchangd+Double.parseDouble(txtAllChangd.getText().toString())));  //"总距离："+
                txtChangd.setText(String.valueOf(dchangd));  //"距离："+
                Log.i(TAG,"总距离:"+txtAllChangd.getText().toString());
                Log.i(TAG,"距离:"+txtChangd.getText().toString());
                Logfile.wLog("总距离:"+txtAllChangd.getText().toString()+"\r\n");
                Logfile.wLog("距离:"+txtChangd.getText().toString()+"\r\n");

                //当前时间减去测试时间   这个的除以1000得到秒，相应的60000得到分，3600000得到小时
                SimpleDateFormat sdf=null;
                sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
                te=sdf.format(new Date());//按以上格式 将当前时间转换成字符串
                Log.i(TAG,"LASTDATE:"+tl);
                Logfile.wLog("LASTDATE:"+tl+"\n");
                Log.i(TAG,"ENDDATE:"+te);
                Logfile.wLog("ENDDATE:"+te+"\n");

                try {
                    lastsencond=(sdf.parse(te).getTime()-sdf.parse(tl).getTime())/1000;
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Logfile.wLog(Logfile.FormatStackTrace(e));
                }
                tl=te;

                txtSud.setText(String.valueOf(dchangd/lastsencond)+"/"+String.valueOf(dchangd/lastsencond*v1));    //"速度："
                Log.i(TAG,"速度:"+String.valueOf(dchangd/lastsencond)+"/"+String.valueOf(dchangd/lastsencond*v1));
                Logfile.wLog("速度:"+String.valueOf(dchangd/lastsencond)+"/"+String.valueOf(dchangd/lastsencond*v1)+"\r\n");
            }

            txtJingd.setText(String.valueOf(lng));     //"经度："+
            txtWeid.setText(String.valueOf(lat));      //"纬度："+
            txtHaib.setText(String.valueOf(atd));      //"返回速度："+

            SimpleDateFormat sdf=null;
            sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
            te=sdf.format(new Date());//按以上格式 将当前时间转换成字符串
            Log.i(TAG,"ENDDATE:"+te);
            Logfile.wLog("ENDDATE:"+te+"\n");
            try {
                allsencond=(sdf.parse(te).getTime()-sdf.parse(ts).getTime())/1000;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Logfile.wLog(Logfile.FormatStackTrace(e));
            }

            txtShij.setText(String.valueOf(allsencond));      //"时间："+
            txtPinjsd.setText(String.valueOf(Double.parseDouble(txtAllChangd.getText().toString())/allsencond));    //"平均速度："+
            txtDingwcs.setText(String.valueOf(iCount)); //"定位次数："+

            Log.i(TAG,"时间:"+String.valueOf(allsencond));
            Log.i(TAG,"平均速度:"+String.valueOf(Double.parseDouble(txtAllChangd.getText().toString())/allsencond)
                    +"/"+String.valueOf(Double.parseDouble(txtAllChangd.getText().toString())/allsencond*v1));
            Logfile.wLog("时间:"+String.valueOf(allsencond)+"\r\n");
            Logfile.wLog("平均速度:"+String.valueOf(Double.parseDouble(txtAllChangd.getText().toString())/allsencond)
                    +"/"+String.valueOf(Double.parseDouble(txtAllChangd.getText().toString())/allsencond*v1));
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // TODO Auto-generated method stub

        }

    };


    /// 方法二：
    /// 获取两个经纬度之间的距离
    /// </summary>
    /// <param name="LonA">经度A</param>
    /// <param name="LatA">纬度A</param>
    /// <param name="LonB">经度B</param>
    /// <param name="LatB">经度B</param>
    /// <returns>距离（千米）</returns>
//  public static double getDistance(double LonA, double LatA, double LonB, double LatB)
//  {
//      // 东西经，南北纬处理，只在国内可以不处理(假设都是北半球，南半球只有澳洲具有应用意义)
//      double MLonA = LonA;
//      double MLatA = LatA;
//      double MLonB = LonB;
//      double MLatB = LatB;
//      // 地球半径（千米）
//      double R = 6371.004;
//      double C = Math.sin(rad(LatA)) * Math.sin(rad(LatB)) + Math.cos(rad(LatA)) * Math.cos(rad(LatB)) * Math.cos(rad(MLonA - MLonB));
//      return (R * Math.acos(C));
//  }
//
//  private static double rad(double d)
//  {
//      return d * Math.PI / 180.0;
//  }


    //方法三：google maps采用方法
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    public static double GetJuli(double lat1, double lng1, double lat2, double lng2)
    {
        double EARTH_RADIUS = 6378.137;
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));

        s = s * EARTH_RADIUS*1000;

        return s;
    }

    //原想处理一下过长的double数值，后来嫌麻烦没有加到代码里面
    public double myround(double s1)
    {
        double s2=s1;
        s2 = Math.round(s2 * 10000) / 10000;
        return s2;
    }

    private void TestGetDistance()
    {
        File fin =null;
        FileInputStream inStream=null;
        String sline=null;

        double lat1=0.0;
        double lng1=0.0;
        double lat2=0.0;
        double lng2=0.0;
        boolean blnLng=false;

        //GetDistance(double lat1, double lng1, double lat2, double lng2)

        //打开文件
        try{
            fin = new File("/mnt/sdcard/fangfa301.txt");
            inStream = new FileInputStream(fin);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        try {
            while ((sline = reader.readLine()) != null) {
                //Log.i(TAG,sline);

                if (sline.startsWith("纬度:"))
                {
                    lat1=Double.parseDouble(sline.substring(sline.indexOf(":")+1));
                }

                if (sline.startsWith("经度:"))
                {
                    lng1=Double.parseDouble(sline.substring(sline.indexOf(":")+1));
                    blnLng=true;
                }

                if (lat1!=0.0 && lng1!=0.0 && lat2!=0.0 && lng2!=0.0 && lng1!=lng2)
                {
                    Log.i(TAG,"纬度1:"+String.valueOf(lat1)+" 经度1:"+String.valueOf(lng1));
                    Log.i(TAG,"纬度2:"+String.valueOf(lat2)+" 经度2:"+String.valueOf(lng2));
                    Log.i(TAG,String.valueOf(GetJuli(lat1, lng1, lat2, lng2)));
                    Log.i(TAG,String.valueOf(GetJuli(lat2, lng2,lat1, lng1)));
                }

                if (blnLng)
                {
                    lat2=lat1;
                    lng2=lng1;
                    blnLng=false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //关闭文件
        try{
            inStream.close();
            fin=null;
        }
        catch(Exception e){
            e.printStackTrace();
        }



    }

    public static void checkPermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission =
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //动态申请
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        }
    }

}



/*
注意，一定要在AndroidManiFest.xml文件中注册权限
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
Android Location提供两种获取地理位置的方式：
一种是GPS（LocationManager.GPS_PROVIDER）,一种是Google网络地图（LocationManager.NETWORK_PROVIDER）
该程序只管第一种定位方式，因为网络gps精度很差，误差在1km左右


如何根据经纬度计算两点之间的距离：
地球赤道上环绕地球一周走一圈共 40075.04公里
而一圈分成360°
而每1°(度)有60'
每一度一秒在赤道上的距离计算如下：
40075.04km/360°=111.31955km
111.31955km/60'=1.8553258km=1855.3m
而每一分又有60秒
每一秒就代表 1855.3m/60=30.92m
任意两点距离计算公式为
d＝111.12cos{1/[sinΦAsinΦB十 cosΦAcosΦBcos(λB-λA)]}
其中:A点经度，纬度分别为λA和ΦA， B点的经度、纬度分别为λB和ΦB，d为距离

地球上所有地方的纬度一分的距离都是约等于1.86公里，也就是一度等于1.86＊60＝111公里。
不同纬度处的经度线上的一分的实际距离是不同的，219国道基本在东经29－38度之间，29度处的一分经线长约1.63公里，38度处的一分经线长约1.47公里。

*/



