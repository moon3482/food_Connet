package com.example.abled_food_connect.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.MainFragmentItemData


class MainFragmentAdapter(val context: Context, val list: ArrayList<MainFragmentItemData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CustomHolder(
            LayoutInflater.from(context).inflate(R.layout.main_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val maindata: MainFragmentItemData = list.get(position)
        val testholder: CustomHolder = holder as CustomHolder
        testholder.roomStatus.setText(maindata.title)
        testholder.shopName.setText(maindata.info)
        if(maindata.roomStatus>5){
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            testholder.roomStatus.setText("모집중")
        }else if(maindata.roomStatus>0){
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            testholder.roomStatus.setText(maindata.roomStatus.toString()+"시간")

        }else{
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            testholder.roomStatus.setText("마감")
        }
        if(maindata.gender.equals("male")){
            testholder.gender.setImageResource(R.drawable.ic_male)
        }
        else if(maindata.gender.equals("female")){
            testholder.gender.setImageResource(R.drawable.ic_female)
        }
        else{
            testholder.gender.setImageResource(R.drawable.ic_maleandfemale)
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }

    class CustomHolder(view: View) : RecyclerView.ViewHolder(view) {
        var roomStatus: TextView = view.findViewById(R.id.tvRoomStatus)
        var shopName: TextView = view.findViewById(R.id.tvShopName)
        var gender: ImageView = view.findViewById(R.id.ivGender)
        var roomTitle: TextView = view.findViewById(R.id.tvRoomTitle)
        var roomDateTime:TextView = view.findViewById(R.id.tvRoomDateTime)
        var roomLocation:TextView = view.findViewById(R.id.tvRoomLocation)
        var roomNumberOfPeople :TextView = view.findViewById(R.id.tvRoomNumberOfPeople)

    }

}