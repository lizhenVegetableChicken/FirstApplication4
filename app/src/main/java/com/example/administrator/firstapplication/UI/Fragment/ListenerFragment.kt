package com.example.administrator.firstapplication.UI.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.administrator.bluecar.mscv5plusdemo.TtsDemo
import com.example.administrator.firstapplication.Base.BaseFragment
import com.example.administrator.firstapplication.R
import org.jetbrains.anko.startActivity

class ListenerFragment:BaseFragment() {
    var btn: Button? = null;
    var cs: Button? = null;

    override fun initView(): View? {
        val inflate = View.inflate(context, R.layout.start, null)
        return inflate;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = View.inflate(context, R.layout.start, null)

        /*inflate.findViewById<Button>(R.id.btn1).setOnClickListener(View.OnClickListener {
//            val intent = Intent(DeviceScanActivity::class.java)
            context?.startActivity<IatDemo>();
        })*/
        inflate.findViewById<Button>(R.id.btn2).setOnClickListener(View.OnClickListener {
//            println("======================================================================================")
            context?.startActivity<TtsDemo>()
        })
        return inflate

        return null
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