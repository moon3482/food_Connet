package com.example.abled_food_connect.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.Data.MainFragmentItemData as MainItemData

class MainFragmentAdapter(val context: Context, val list: ArrayList<MainItemData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CustomHolder(
            LayoutInflater.from(context).inflate(R.layout.main_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val maindata: MainItemData = list.get(position)
        val testholder: CustomHolder = holder as CustomHolder
        testholder.text1.setText(maindata.title)
        testholder.text2.setText(maindata.info)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    class CustomHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text1: TextView = view.findViewById(R.id.textView2)
        var text2: TextView = view.findViewById(R.id.textView3)

    }

}