package com.example.administrator.firstapplication.Util

interface Iservice {
    abstract fun updatePlayState()
    fun isPlaying():Boolean?
    fun getDuration(): Int
    fun getProgress(): Int
    fun seekTo(progress: Int)
    fun playPre()
    fun playNext()
    fun returnid(): Int
}