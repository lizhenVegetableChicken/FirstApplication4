package com.example.administrator.firstapplication.Util

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.administrator.firstapplication.Bean.Url
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.R.id.audio_title
import com.example.administrator.firstapplication.R.id.state
import de.greenrobot.event.EventBus
import kotlinx.android.synthetic.main.activity_music_player_bottom.*

class AudioService:Service() {
    var player:MediaPlayer?=null
    val binder by lazy{AudioBinder()}
    val MOOD_ALL=1
    var mood=1
    var position:Int=0
    var list:List<Url>?=null
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var url=intent!!.getStringExtra("url")
        position=intent!!.getIntExtra("position",0)
        list=intent!!.getParcelableArrayListExtra("list")
        binder.playItem(url)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    inner class AudioBinder:Binder(),Iservice, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
        override fun returnid(): Int {
            return position
        }

        override fun playPre() {
            position=position-1
            if(position==-1){
                position=list!!.size-1
            }
            playItem("https://www.tenfee.top/Audio/${list?.get(position)?.url?:""}")
        }

        override fun playNext() {
            position=(position+1)%list!!.size
            playItem("https://www.tenfee.top/Audio/${list?.get(position)?.url?:""}")
        }

        override fun onCompletion(mp: MediaPlayer?) {
            autoPlayNext()
        }

        override fun seekTo(progress: Int) {
            player?.seekTo(progress)
        }

        override fun getProgress(): Int {
            return player?.currentPosition?:0
        }

        override fun getDuration(): Int {
            return  player?.duration?:0
        }

        override fun updatePlayState() {
            var isplaying=isPlaying()
            isplaying?.let {
                if(isplaying){
                    player?.pause()
                }else{
                    player?.start()
                }
            }
        }
        override fun isPlaying():Boolean?{
            return  player?.isPlaying
        }
        override fun onPrepared(mp: MediaPlayer?) {
            player?.start()
            notifyUpdateUi()
        }

        private fun notifyUpdateUi() {
            EventBus.getDefault().post(0)
        }

        fun playItem(u:String){

            if(player!=null){
                player?.reset()
                player?.release()
                player = null
            }
            if (player!=null){
                player?.reset()
                player?.release()
                player==null
            }
            player=MediaPlayer()
            player?.let {
                it.setOnPreparedListener(this)
                it.setDataSource(u)
                it.setOnCompletionListener(this)
                it.prepareAsync()
            }

        }
         fun autoPlayNext() {
            when(mood){
                MOOD_ALL->{
                    list?.let {
                    position=(position+1)%it.size
                    }
                }
            }
            playItem("https://www.tenfee.top/Audio/${list?.get(position)?.url?:""}")

        }

    }


}