package com.example.administrator.bluecar.gps;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logfile {

	public static String sLog;
	public static File fout =null;
	public static FileOutputStream outStream=null;

	public static void oLog()
	{
        //初始化日志文件
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	sLog = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        try{
        	fout = new File(sLog,getLogName()+".txt");
        	outStream = new FileOutputStream(fout);
        }
        catch(Exception e){
        	e.printStackTrace();
        }
	}


    //写日志
	public static void wLog(String arg1)
	{
		String sData="";
		sData=arg1;
		//sData=getLogName()+":"+arg1;
        try{
    		outStream.write(sData.getBytes());
    		outStream.flush();
    		sData="";
        }
        catch(Exception e){
        	e.printStackTrace();
        }
	}

    //关闭日志
	public static void cLog()
	{
        try{
        	outStream.flush();
        	outStream.close();
        	fout=null;
        }
        catch(Exception e){
        	e.printStackTrace();
        }

	}

    public static String getLogName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
        return dateFormat.format(date);
    }

    public static String FormatStackTrace(Throwable throwable) {
        if(throwable==null) return "";
        String rtn = throwable.getStackTrace().toString();
        try {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            printWriter.flush();
            writer.flush();
            rtn = writer.toString();
            printWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
        }
        return rtn;
    }


}
