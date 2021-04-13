package com.example.administrator.firstapplication.UI.Activity

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.widget.SeekBar
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.Bean.Url
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.Util.AudioService
import com.example.administrator.firstapplication.Util.Iservice
import com.itheima.player.util.StringUtil
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_music_player_bottom.*
import kotlinx.android.synthetic.main.activity_music_player_middle.*
import kotlinx.android.synthetic.main.activity_music_player_top.*

class AudioPlayerActivity : BaseActivity(),View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    var iService:Iservice?=null
    var duration=0
    val MSG_progress=0
    var list:ArrayList<Url>?=null
        val handler=object:Handler(){
            override fun handleMessage(msg: Message) {
                when(msg?.what){
                    MSG_progress->startUpdateProgress()
                }
            }
        }


    val connection by lazy{
        AudioConnection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list =intent!!.getParcelableArrayListExtra<Url>("list")
    }
    fun  onEventMainThread(itemBean:Any){
        //更新UI处理
        audio_title.text=list?.get(iService?.returnid()?:0)?.url
        updatePlayStateButton()
        duration=iService?.getDuration()?:0
        progress_sk.max=duration
        startUpdateProgress()
    }

    private fun startUpdateProgress() {
        val Progress:Int=iService?.getProgress()?:0
        updateProgress(Progress)
        if(Progress==duration){
            state.setImageResource(R.drawable.selector_btn_audio_pause)
            var drawable=audio_anim.drawable as AnimationDrawable
            drawable?.stop()

        }
        handler.sendEmptyMessageDelayed(MSG_progress,500)
    }

    private fun updateProgress(Progress: Int) {
        progress.text=StringUtil.parseDuration(Progress)+"/"+ StringUtil.parseDuration(duration)
        progress_sk.setProgress(Progress)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.state->updateState()
            R.id.pre->{iService?.playPre()}
            R.id.next->{iService?.playNext()}
        }
    }

    private fun updateState() {
        iService?.updatePlayState()
        updatePlayStateButton()
    }

    private fun updatePlayStateButton() {
        var isplaying=iService?.isPlaying()
        isplaying?.let {
            if (isplaying){
                state.setImageResource(R.drawable.selector_btn_audio_play)
                var drawable=audio_anim.drawable as AnimationDrawable
                drawable?.start()
                handler.sendEmptyMessage(MSG_progress)
            }else{
                state.setImageResource(R.drawable.selector_btn_audio_pause)
                var drawable=audio_anim.drawable as AnimationDrawable
                drawable?.stop()
                handler.removeMessages(MSG_progress)
            }
        }

    }


    override fun getLayoutId(): Int {
        return  R.layout.activity_audio_player
    }
    override fun initData() {
        EventBus.getDefault().register(this)

        var intend=intent
        intend.setClass(this,AudioService::class.java)
        bindService(intend,connection,Context.BIND_AUTO_CREATE)
        startService(intend)
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if(!fromUser) return
        iService?.seekTo(progress)
        updateProgress(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
    override fun initListener() {
        super.initListener()
        state.setOnClickListener(this)
//        back.setOnClickListener { finish() }
        progress_sk.setOnSeekBarChangeListener(this)
        pre.setOnClickListener(this)
        next.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        EventBus.getDefault().unregister(this)
        handler.removeCallbacksAndMessages(null)
    }

    inner class AudioConnection:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iService=service as Iservice
        }

    }

}