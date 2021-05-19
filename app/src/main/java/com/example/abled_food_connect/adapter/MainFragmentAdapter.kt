package com.example.abled_food_connect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.MainFragmentItemData


class MainFragmentAdapter(val context: Context, private val list: ArrayList<MainFragmentItemData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CustomHolder(
            LayoutInflater.from(context).inflate(R.layout.main_page_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val maindata: MainFragmentItemData = list[position]
        val testholder: CustomHolder = holder as CustomHolder
        testholder.roomStatus.text = maindata.title
        testholder.shopName.text = maindata.info
        if(maindata.roomStatus>5){
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_recruitment)
            testholder.roomStatus.text = "모집중"
        }else if(maindata.roomStatus>0){
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_imminent)
            val text : String = context.getString(R.string.room_status_imminent_time)
            testholder.roomStatus.text = String.format(text,maindata.roomStatus)

        }else{
            testholder.roomStatus.setBackgroundResource(R.drawable.main_fragment_rooms_status_deadline)
            testholder.roomStatus.text = "마감"
        }
        if(maindata.gender.equals("male")){
            testholder.gender.setImageResource(R.drawable.ic_male)
        }
        else if(maindata.gender == "female"){
            testholder.gender.setImageResource(R.drawable.ic_female)
        }
        else{
            testholder.gender.setImageResource(R.drawable.ic_maleandfemale)
        }

        if (maindata.maximumAge == maindata.minimumAge){
            testholder.roomAge.text = maindata.maximumAge.toString()
        }else{
            val text : String =context.getString(R.string.limit_age_badge)
            testholder.roomAge.text = String.format(text,maindata.minimumAge,maindata.maximumAge)
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
        var roomAge:TextView = view.findViewById(R.id.tvAge)

    }

}