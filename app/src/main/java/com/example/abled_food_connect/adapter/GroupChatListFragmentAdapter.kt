package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.ChatRoomActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.array.numOfPeople
import com.example.abled_food_connect.data.MainFragmentItemData

class GroupChatListFragmentAdapter(
    val context: Context,
    val list: ArrayList<MainFragmentItemData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return customholder(
            LayoutInflater.from(context).inflate(R.layout.chat_fragment_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var mainFragmentItemData:MainFragmentItemData = list[position]
        val holder = holder as customholder
        holder.shopName.text = mainFragmentItemData.shopName
        holder.numOfPeople.text = mainFragmentItemData.nowNumOfPeople.toString()
        holder.title.text = mainFragmentItemData.title

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("roomId", mainFragmentItemData.roomId)
            intent.putExtra("title", mainFragmentItemData.title)
            intent.putExtra("info", mainFragmentItemData.info)
            intent.putExtra("hostName", mainFragmentItemData.hostName)
            intent.putExtra("address", mainFragmentItemData.address)
            intent.putExtra("date", mainFragmentItemData.date)
            intent.putExtra("shopName", mainFragmentItemData.shopName)
            intent.putExtra("roomStatus", mainFragmentItemData.roomStatus)
            intent.putExtra("numOfPeople", mainFragmentItemData.numOfPeople.toString())
            intent.putExtra("keyWords", mainFragmentItemData.keyWords)
            intent.putExtra("nowNumOfPeople", mainFragmentItemData.nowNumOfPeople.toString())
            intent.putExtra("mapX", mainFragmentItemData.mapX)
            intent.putExtra("mapY", mainFragmentItemData.mapY)
            intent.putExtra("imageUrl", "imageUrl")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
      return list.size
    }

    class customholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var shopName = itemView.findViewById<TextView>(R.id.groupChatListShopName)
        var date = itemView.findViewById<TextView>(R.id.groupChatListDate)
        var message = itemView.findViewById<TextView>(R.id.groupChatListMessage)
        var numOfPeople = itemView.findViewById<TextView>(R.id.groupChatListNumfoPeople)
        var title = itemView.findViewById<TextView>(R.id.groupChatListTile)


    }
}