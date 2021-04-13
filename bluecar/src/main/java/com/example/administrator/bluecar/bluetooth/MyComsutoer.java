package com.example.administrator.bluecar.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyComsutoer extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("...........................收到自定义的广播"+intent.getStringExtra("key"));
    }
}
