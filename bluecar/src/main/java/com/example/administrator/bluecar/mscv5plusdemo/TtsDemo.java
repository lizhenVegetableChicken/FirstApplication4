package com.example.administrator.bluecar.mscv5plusdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.bluecar.R;
import com.example.administrator.bluecar.bluetooth.BleSppActivity;
import com.example.administrator.bluecar.speech.setting.TtsSettings;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;


//import com.iflytek.cloud.param.MscKeys;

public class TtsDemo extends Activity implements OnClickListener {
    private static String TAG = TtsDemo.class.getSimpleName();
    private static int selectedNumCloud = 0;


	/*// 默认云端发音人
	public static String voicerCloud="xiaoyan";
	// 默认本地发音人
	public static String voicerLocal="xiaoyan";

	public static String voicerXtts="xiaoyan";
	// 云端发音人列表
	private String[] cloudVoicersEntries;
	private String[] cloudVoicersValue ;
	
	// 本地发音人列表
	private String[] localVoicersEntries;
	private String[] localVoicersValue ;

	// 增强版发音人列表
	private String[] xttsVoicersEntries;
	private String[] xttsVoicersValue;*/
    private static int selectedNumLocal = 0;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    //缓冲进度
    private int mPercentForBuffering = 0;
    //播放进度
    private int mPercentForPlaying = 0;
    // 云端/本地选择按钮
//	private RadioGroup mRadioGroup;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

//	private MyReceiver myReceiver;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private String text = " ";  //接收蓝牙传递过来的信息。
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");

            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


//	用来测试本页面的广播的
//	public void sendBroad(View v) {
//        System.out.println("TtsDemo的sendBroad运行了......................................");
//		EditText et = findViewById(R.id.test_test);
//		Intent intent = new Intent();
//		intent.setAction("com.lizhen.test");
//		intent.putExtra("bytes",et.getText().toString().getBytes());
//		sendBroadcast(intent);
//        System.out.println("..................................结束");
//	}


//	也是测试用的
//	private BroadcastReceiver dataSynReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals("com.example.administrator.firstapplication.dataSynAction")) {
//
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						//接下来执行同步操作
//						text = intent.getStringExtra("msg");
//					}
//				});
//			}
//		}
//	};
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
            Log.d(TtsDemo.TAG, "开始播放：" + System.currentTimeMillis());
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                Log.d(TAG, "session id =" + sid);
            }

            //实时音频流输出参考
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
				Log.e("MscSpeechLog", "buf is =" + buf);
			}*/
        }
    };


	/*//接受蓝牙传递过来的内容。
	Bundle bundle = getIntent().getBundleExtra("bundle");
	text = bundle.getString("msg");
	System.out.println(text);*/


	/*class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//接受蓝牙传递过来的内容。
			text = intent.getStringExtra("msg");
			((EditText) findViewById(R.id.tts_text)).setText(text);
			System.out.println("=============="+text);
		}
	};*/
    //	用来接收蓝牙发来的广播数据，然后调用display()显示
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            System.out.println("我运行了....................................");
//
//            String msg = intent.getStringExtra("msg");
//            text = msg;
//            Toast.makeText(TtsDemo.this, text, Toast.LENGTH_SHORT).show();
//            display();
//        }
//    };

    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ttsdemo);



		System.out.println("TtsDemo的create........................................");

//        Button button = findViewById(R.id.submit_btn);
//        button.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
////				startActivity(new Intent(TtsDemo.this, BleSppActivity.class));
//			}
//		});
//
//        registerReceiver(receiver,
//                new IntentFilter("com.demo.broadcast"));
//		registerReceiver(receiver,new IntentFilter("com.lizhen.test"));


        StringBuffer param = new StringBuffer();
        param.append("appid=" + getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        param.append(",");
        param.append(SpeechConstant.FORCE_LOGIN + "=true");
        SpeechUtility.createUtility(this, param.toString());


        initLayout();
        requestPermissions();

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);

		/*// 云端发音人名称列表
		cloudVoicersEntries = getResources().getStringArray(R.array.voicer_cloud_entries);
		cloudVoicersValue = getResources().getStringArray(R.array.voicer_cloud_values);

		// 本地发音人名称列表
		localVoicersEntries = getResources().getStringArray(R.array.voicer_local_entries);
		localVoicersValue = getResources().getStringArray(R.array.voicer_local_values);

		// 增强版发音人名称列表
		xttsVoicersEntries = getResources().getStringArray(R.array.voicer_xtts_entries);
		xttsVoicersValue = getResources().getStringArray(R.array.voicer_xtts_values);*/

        mSharedPreferences = getSharedPreferences(TtsSettings.PREFER_NAME, Activity.MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);


        //注册BroadcastReceiver
		/*IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.administrator.firstapplication.dataSynAction");
		registerReceiver(dataSynReceiver, filter);*/
//
//        Intent intent = getIntent();
//        text = intent.getStringExtra("msg");
        display();

    }

    public void display() {
        EditText editText = ((EditText) findViewById(R.id.tts_text));
        ImageView img = findViewById(R.id.img);
        if (text == null) {
            Toast.makeText(this, "没有接收到蓝牙传递数据", Toast.LENGTH_SHORT).show();
        } else {
            switch (text) {
                case "A":
                    text = "前方2米内有" + "小汽车";
                    editText.setText(text);
                    img.setImageResource(R.drawable.car1);
                    break;
                case "a":
                    text = "后方2米内有" + "小汽车";
                    editText.setText(text);
                    img.setImageResource(R.drawable.car2);
                    break;
                case "B":
                    text = "前方2米内有" + "公交车";
                    editText.setText(text);
                    img.setImageResource(R.drawable.bus2);
                    break;
                case "b":
                    text = "后方2米内有" + "公交车";
                    editText.setText(text);
                    img.setImageResource(R.drawable.bus3);
                    break;
                case "C":
                    text = "前方2米内有" + "行人";
                    editText.setText(text);
                    img.setImageResource(R.drawable.person1);
                    break;
                case "c":
                    text = "后方2米内有" + "行人";
                    editText.setText(text);
                    img.setImageResource(R.drawable.person3);
                    break;
                case "D":
                    text = "前方2米内有" + "自行车";
                    editText.setText(text);
                    img.setImageResource(R.drawable.bike1);
                    break;
                case "d":
                    text = "后方2米内有" + "自行车";
                    editText.setText(text);
                    img.setImageResource(R.drawable.bike2);
                    break;
                case "E":
                    text = "前方2米内有" + "交通信号灯";
                    editText.setText(text);
                    img.setImageResource(R.drawable.light1);
                    break;
                case "e":
                    text = "后方2米内有" + "交通信号灯";
                    editText.setText(text);
                    img.setImageResource(R.drawable.light2);
                    break;
                case "G":

                    break;
                default:
                    showTip("此功能将于近期开放，敬请期待。");
                    break;
            }

            //开始播报
            setParam();
            mTts.startSpeaking(text, mTtsListener);
        }
    }
    /**
     * 发音人选择。
     */
	/*private void showPresonSelectDialog() {
		int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();// 选择在线合成
		if (checkedRadioButtonId == R.id.tts_radioCloud) {
			new AlertDialog.Builder(this).setTitle("在线合成发音人选项")
					.setSingleChoiceItems(cloudVoicersEntries, // 单选框有几项,各是什么名字
							selectedNumCloud, // 默认的选项
							new DialogInterface.OnClickListener() { // 点击单选框后的处理
								public void onClick(DialogInterface dialog,
													int which) { // 点击了哪一项
									voicerCloud = cloudVoicersValue[which];

									if ("catherine".equals(voicerCloud) || "henry".equals(voicerCloud) || "vimary".equals(voicerCloud)) {
										((EditText) findViewById(R.id.tts_text)).setText(R.string.text_tts_source_en);
									} else {
										((EditText) findViewById(R.id.tts_text)).setText(R.string.text_tts_source);
									}
									selectedNumCloud = which;
									dialog.dismiss();
								}
							}).show();

			// 选择本地合成
		} else if (checkedRadioButtonId == R.id.tts_radioLocal) {
			new AlertDialog.Builder(this).setTitle("本地合成发音人选项")
					.setSingleChoiceItems(localVoicersEntries, // 单选框有几项,各是什么名字
							selectedNumLocal, // 默认的选项
							new DialogInterface.OnClickListener() { // 点击单选框后的处理
								public void onClick(DialogInterface dialog,
													int which) { // 点击了哪一项
									voicerLocal = localVoicersValue[which];
									if ("catherine".equals(voicerLocal) || "henry".equals(voicerLocal) || "vimary".equals(voicerLocal)) {
										((EditText) findViewById(R.id.tts_text)).setText(R.string.text_tts_source_en);
									} else {
										((EditText) findViewById(R.id.tts_text)).setText(R.string.text_tts_source);
									}
									selectedNumLocal = which;
									dialog.dismiss();
								}
							}).show();
		} else if (checkedRadioButtonId == R.id.tts_radioXtts) {
			new AlertDialog.Builder(this).setTitle("增强版合成发音人选项")
					.setSingleChoiceItems(xttsVoicersEntries, // 单选框有几项,各是什么名字
							selectedNumLocal, // 默认的选项
							new DialogInterface.OnClickListener() { // 点击单选框后的处理
								public void onClick(DialogInterface dialog,
													int which) { // 点击了哪一项
									voicerXtts = xttsVoicersValue[which];
									//Toast.makeText(this,voicerXtts,Toast.LENGTH_LONG);
									System.out.println("sssssss:" + voicerXtts);
									if ("catherine".equals(voicerXtts) || "henry".equals(voicerXtts) || "vimary".equals(voicerXtts)) {
										((EditText) findViewById(R.id.tts_text)).setText(R.string.text_tts_source_en);
									} else {
										((EditText) findViewById(R.id.tts_text)).setText(R.string.text_tts_source);
									}
									selectedNumLocal = which;
									dialog.dismiss();
								}
							}).show();
		}
	}*/

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        findViewById(R.id.tts_play).setOnClickListener(this);

        findViewById(R.id.tts_cancel).setOnClickListener(this);
		/*findViewById(R.id.tts_pause).setOnClickListener(this);
		findViewById(R.id.tts_resume).setOnClickListener(this);*/
        findViewById(R.id.image_tts_set).setOnClickListener(this);

		/*findViewById(R.id.tts_btn_person_select).setOnClickListener(this);

		mRadioGroup=((RadioGroup) findViewById(R.id.tts_rediogroup));
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.tts_radioCloud) {
					mEngineType = SpeechConstant.TYPE_CLOUD;
				} else if (checkedId == R.id.tts_radioLocal) {
					mEngineType = SpeechConstant.TYPE_LOCAL;
				} else if (checkedId == R.id.tts_radioXtts) {
					mEngineType = SpeechConstant.TYPE_XTTS;
				}
			}
		} );*/
    }

    @Override
    public void onClick(View view) {
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化");
            return;
        }

        int id = view.getId();
        if (id == R.id.image_tts_set) {
            Intent intent = new Intent(TtsDemo.this, TtsSettings.class);
            startActivity(intent);

            // 开始合成
            // 收到onCompleted 回调时，合成结束、生成合成音频
            // 合成的音频格式：只支持pcm格式
        } else if (id == R.id.tts_play) {

            // 设置参数
            setParam();
            Log.d(TAG, "准备点击： " + System.currentTimeMillis());
            int code = mTts.startSpeaking(text, mTtsListener);
//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

            if (code != ErrorCode.SUCCESS) {
                showTip("语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
            // 取消合成
        } else if (id == R.id.tts_cancel) {
            mTts.stopSpeaking();
		/*	// 暂停播放
		} else if (id == R.id.tts_pause) {
			mTts.pauseSpeaking();
			// 继续播放
		} else if (id == R.id.tts_resume) {
			mTts.resumeSpeaking();*/
            // 选择发音人
        }
		/*else if (id == R.id.tts_btn_person_select) {
			showPresonSelectDialog();
		}*/
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    /**
     * 参数设置
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            //设置使用云端引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			/*//设置发音人
			mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);*/

        } else if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
            //设置使用本地引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
            //设置发音人
//			mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
            //设置发音人
//			mTts.setParameter(SpeechConstant.VOICE_NAME,voicerXtts);
        }
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        //	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");


    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "tts";
        if (mEngineType.equals(SpeechConstant.TYPE_XTTS)) {
            type = "xtts";
        }
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
		/*if(mEngineType.equals(SpeechConstant.TYPE_XTTS)){
			tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, type+"/"+TtsDemo.voicerXtts+".jet"));
		}else {
			tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, type + "/" + TtsDemo.voicerLocal + ".jet"));
		}*/

        return tempBuffer.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
//		unRegisterReceiver();
    }

//	private void unRegisterReceiver() {
//		unregisterReceiver(dataSynReceiver);
//	}

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
