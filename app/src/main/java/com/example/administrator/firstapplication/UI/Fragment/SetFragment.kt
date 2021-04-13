package com.example.administrator.firstapplication.UI.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.administrator.bluecar.bluetooth.DeviceScanActivity
import com.example.administrator.bluecar.gps.SpeedActivity

import com.example.administrator.firstapplication.Base.BaseFragment
import com.example.administrator.firstapplication.R

import org.jetbrains.anko.startActivity

class SetFragment : BaseFragment() {

    var btn: Button? = null;
    var cs: Button? = null;

    override fun initView(): View? {

        val inflate = View.inflate(context, R.layout.fragment_set, null)
        btn = inflate.findViewById<Button>(R.id.bt_bluetooth);
        cs = inflate.findViewById<Button>(R.id.cesu);
        return inflate;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = View.inflate(context, R.layout.fragment_set, null)
        inflate.findViewById<Button>(R.id.bt_bluetooth).setOnClickListener(View.OnClickListener {
//            val intent = Intent(DeviceScanActivity::class.java)
            context?.startActivity<DeviceScanActivity>();
        })
        inflate.findViewById<Button>(R.id.cesu).setOnClickListener(View.OnClickListener {
            println("======================================================================================")
            context?.startActivity<SpeedActivity>();
        })
        return inflate
    }


    override fun initData() {
        super.initData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

    }



}