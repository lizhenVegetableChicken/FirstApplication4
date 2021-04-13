package com.example.administrator.bluecar.bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.bluecar.R;
import com.example.administrator.bluecar.mscv5plusdemo.TtsDemo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 * API?????BLE???????????????????????????GATT???????????
 * ????{@code BluetoothLeService}???????Bluetooth LE API?????
 */
public class BleSppActivity extends Activity implements View.OnClickListener {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = BleSppActivity.class.getSimpleName();
    static long recv_cnt = 0;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    int sendIndex = 0;
    int sendDataLen = 0;
    byte[] sendBuf;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private TextView mDataRecvText;
    private TextView mRecvBytes;
    private TextView mDataRecvFormat;
    private EditText mEditBox;
    private TextView mSendBytes;
    private TextView mDataSendFormat;
    private TextView mNotify_speed_text;
    private long recvBytes = 0;
    private long lastSecondBytes = 0;
    private long sendBytes;
    private StringBuilder mData;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //????????????
                mConnected = true;
                invalidateOptionsMenu();
                updateConnectionState(R.string.connected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED.equals(action)) {
                mBluetoothLeService.connect(mDeviceAddress);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                final byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
//                final StringBuilder stringBuilder = new StringBuilder();
//                 for(byte byteChar : data)
//                      stringBuilder.append(String.format("%02X ", byteChar));
//                Log.v("log",stringBuilder.toString());


                displayData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));


            } else if (BluetoothLeService.ACTION_WRITE_SUCCESSFUL.equals(action)) {
                mSendBytes.setText(sendBytes + " ");
                if (sendDataLen > 0) {
                    Log.v("log", "Write OK,Send again");
                    onSendBtnClicked();
                } else {
                    Log.v("log", "Write Finish");
                }
            }

        }
    };
    //??
    private Timer timer;
    private TimerTask task;

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_SUCCESSFUL);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_NO_DISCOVERED);
        return intentFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.gatt_services_characteristics);
        setContentView(R.layout.ble_spp);


        System.out.println("BleSppActivity的create........................................");

            //TIAOZHUAN 按钮跳到显示页面
        Button button = findViewById(R.id.btn_fly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BleSppActivity.this,TtsDemo.class));
            }
        });

        //??????????
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDataRecvText = (TextView) findViewById(R.id.data_read_text);
        mRecvBytes = (TextView) findViewById(R.id.byte_received_text);
        mDataRecvFormat = (TextView) findViewById(R.id.data_received_format);
        mEditBox = (EditText) findViewById(R.id.data_edit_box);
        mSendBytes = (TextView) findViewById(R.id.byte_send_text);
        mDataSendFormat = (TextView) findViewById(R.id.data_sended_format);
        mNotify_speed_text = (TextView) findViewById(R.id.notify_speed_text);

        Button mSendBtn = (Button) findViewById(R.id.send_data_btn);
        Button mCleanBtn = (Button) findViewById(R.id.clean_data_btn);
        Button mCleanTextBtn = (Button) findViewById(R.id.clean_text_btn);

        mDataRecvFormat.setOnClickListener(this);
        mDataSendFormat.setOnClickListener(this);
        mRecvBytes.setOnClickListener(this);
        mSendBytes.setOnClickListener(this);

        mCleanBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mCleanTextBtn.setOnClickListener(this);
        mDataRecvText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mData = new StringBuilder();

        final int SPEED = 1;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SPEED:
                        lastSecondBytes = recvBytes - lastSecondBytes;
                        mNotify_speed_text.setText(String.valueOf(lastSecondBytes) + " B/s");
                        lastSecondBytes = recvBytes;
                        break;
                }
            }
        };

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = SPEED;
                message.obj = System.currentTimeMillis();
                handler.sendMessage(message);
            }
        };

        timer = new Timer();
        timer.schedule(task, 1000, 1000);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mDeviceName);
        }

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


//        //        //直接跳转到show页面
//        Intent intent1 = new Intent(BleSppActivity.this, TtsDemo.class);
////        intent.putExtra("msg","A");
//        startActivity(intent1);


//        用来测试的
        Button button2 = findViewById(R.id.btn_jump_show);
        EditText et2 = findViewById(R.id.test_data_read);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println("这里运行了..........................1");
//                String s = et2.getText().toString();
//                Intent intent8 = new Intent(BleSppActivity.this, TtsDemo.class);
//                intent8.putExtra("msg", s);
//                System.out.println("这里运行了");
//                startActivity(intent8);
                test();
            }
        });

    }


     // 用来跑通 displayData -> BleSpp的广播接收
    public void test() {

        displayData(new byte[]{'a'});
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_connect) {
            mBluetoothLeService.connect(mDeviceAddress);
            return true;
        } else if (itemId == R.id.menu_disconnect) {
            mBluetoothLeService.disconnect();
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // mConnectionState.setText(resourceId);
            }
        });
    }

    //????
    public void convertText(final TextView textView, final int convertTextId) {
        final Animation scaleIn = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_in);
        Animation scaleOut = AnimationUtils.loadAnimation(this,
                R.anim.text_scale_out);
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(convertTextId);
                textView.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        textView.startAnimation(scaleOut);
    }

    //???????????
    private String getHexString() {
        String s = mEditBox.getText().toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f') ||
                    ('A' <= c && c <= 'F')) {
                sb.append(c);
            }
        }
        if ((sb.length() % 2) != 0) {
            sb.deleteCharAt(sb.length());
        }
        return sb.toString();
    }


    private byte[] stringToBytes(String s) {
        byte[] buf = new byte[s.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            try {
                buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }

    public String asciiToString(byte[] bytes) {
        char[] buf = new char[bytes.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char) bytes[i];
            sb.append(buf[i]);
        }
        return sb.toString();
    }

    public String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            sb.append(' ');
        }
        return sb.toString();
    }


    private void getSendBuf() {
        sendIndex = 0;
        if (mDataSendFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
            sendBuf = mEditBox.getText().toString().trim().getBytes();
        } else {
            sendBuf = stringToBytes(getHexString());
        }
        sendDataLen = sendBuf.length;
    }

    private void onSendBtnClicked() {
        if (sendDataLen > 20) {
            sendBytes += 20;
            final byte[] buf = new byte[20];
            // System.arraycopy(buffer, 0, tmpBuf, 0, writeLength);
            for (int i = 0; i < 20; i++) {
                buf[i] = sendBuf[sendIndex + i];
            }
            sendIndex += 20;
            mBluetoothLeService.writeData(buf);
            sendDataLen -= 20;
        } else {
            sendBytes += sendDataLen;
            final byte[] buf = new byte[sendDataLen];
            for (int i = 0; i < sendDataLen; i++) {
                buf[i] = sendBuf[sendIndex + i];
            }
            mBluetoothLeService.writeData(buf);
            sendDataLen = 0;
            sendIndex = 0;
        }
    }

    private void displayData(byte[] buf) {
        recvBytes += buf.length;
        recv_cnt += buf.length;

        if (recv_cnt >= 1024) {
            recv_cnt = 0;
            mData.delete(0, mData.length() / 2); //UI?????512??????APP??
        }



        String s;
        if (mDataRecvFormat.getText().equals("Ascii")) {
            s = asciiToString(buf);
            mData.append(s);
        } else {
            s = bytesToString(buf);
            mData.append(s);
        }

        System.out.println("。。。。。。。buff："+buf);

        System.out.println(".............蓝牙接受的数值：" + s);

//        给显示页面发送广播
        mDataRecvText.setText(mData.toString());
        mRecvBytes.setText(recvBytes + " ");
        sendBroadCast(s);
    }

    public void sendBroadCast(String s) {

        System.out.println("第二次广播运行了........................");

        Intent intent = new Intent();
        intent.setAction("com.demo.broadcast");//设置Action
//        intent.setPackage("com.example.administrator.bluecar");//设置包名使广播只能被app内接收者接收
        intent.putExtra("msg", s);
        sendBroadcast(intent);
        System.out.println("第二次广播运行结束.........................");

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.data_received_format) {
            if (mDataRecvFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
                convertText(mDataRecvFormat, R.string.data_format_hex);
            } else {
                convertText(mDataRecvFormat, R.string.data_format_default);
            }
        } else if (id == R.id.data_sended_format) {
            if (mDataSendFormat.getText().equals(getResources().getString(R.string.data_format_default))) {
                convertText(mDataSendFormat, R.string.data_format_hex);
            } else {
                convertText(mDataSendFormat, R.string.data_format_default);
            }
        } else if (id == R.id.byte_received_text) {
            recvBytes = 0;
            lastSecondBytes = 0;
            convertText(mRecvBytes, R.string.zero);
        } else if (id == R.id.byte_send_text) {
            sendBytes = 0;
            convertText(mSendBytes, R.string.zero);
        } else if (id == R.id.send_data_btn) {
            getSendBuf();
            onSendBtnClicked();
        } else if (id == R.id.clean_data_btn) {
            mData.delete(0, mData.length());
            mDataRecvText.setText(mData.toString());
        } else if (id == R.id.clean_text_btn) {
            mEditBox.setText("");
        }
    }
}
