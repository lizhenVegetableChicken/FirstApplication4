package com.example.administrator.firstapplication.Util

import com.example.administrator.firstapplication.Base.BaseFragment
import com.example.administrator.firstapplication.R
import com.example.administrator.firstapplication.UI.Fragment.ListenerFragment
import com.example.administrator.firstapplication.UI.Fragment.MapFragment
import com.example.administrator.firstapplication.UI.Fragment.SetFragment

class FragMentUtil private constructor(){
    val lisf by lazy { ListenerFragment() }
    val mapf by lazy { MapFragment() }
    val setf by lazy { SetFragment() }
    companion object {
        val fragmentUtil by lazy {
            FragMentUtil()
        }
    }
    fun getFragment(tabId:Int):BaseFragment?{
        when(tabId){
            R.id.tab_listener->return lisf
            R.id.tab_map->return mapf
            R.id.tab_set->return setf
        }
        return null
    }
}