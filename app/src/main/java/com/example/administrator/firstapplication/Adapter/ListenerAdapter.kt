package com.example.administrator.firstapplication.Adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.administrator.firstapplication.widget.ListenerItemView

class ListenerAdapter :RecyclerView.Adapter<ListenerAdapter.ListenerHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListenerHolder {
        return ListenerHolder(ListenerItemView(parent?.context))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ListenerHolder, position: Int) {

    }

    class ListenerHolder(itemView:View):RecyclerView.ViewHolder(itemView)
}