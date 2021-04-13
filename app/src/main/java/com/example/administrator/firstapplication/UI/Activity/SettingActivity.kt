package com.example.administrator.firstapplication.UI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.v7.widget.Toolbar
import com.example.administrator.firstapplication.Base.BaseActivity
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.Util.ToolBarManager
import org.jetbrains.anko.find
import android.preference.Preference.OnPreferenceClickListener
import android.view.View
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File


class SettingActivity():PreferenceActivity(),ToolBarManager{
    override val toolbar: Toolbar by lazy{
        find<Toolbar>(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initSettingToolbar()
//        setout.setOnClickListener {
//            @SuppressLint("WrongConstant")
//            var preference = getSharedPreferences("txt", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING)
//            var editor = preference.edit()
//            editor.clear()
//            editor.commit()
//            startActivity<LoginActivity>()
//            finish()
//        }
    }
    fun deleteFilesByDirectory(directory:File)
    {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (item in directory.listFiles()){
                item.delete()
            }
        }
    }

}