package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.ChatRoomActivity
import com.example.abled_food_connect.data.GroupChatListData
import com.example.abled_food_connect.databinding.ChatFragmentListItemBinding

class GroupChatListFragmentAdapter(
    val context: Context,
    val list: ArrayList<GroupChatListData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return customholder(
            ChatFragmentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var mainFragmentItemData: GroupChatListData = list[position]
        val holder = holder as customholder
        holder.bind(mainFragmentItemData)

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

    class customholder(private val binding: ChatFragmentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GroupChatListData) {
            binding.groupChatListTile.text = data.title
            binding.groupChatListShopName.text = data.placeName
            binding.groupChatListNumfoPeople.text = data.nowNumOfPeople.toString()
            binding.groupChatListMessage.text = data.content
            binding.groupChatListDate.text
            when (true) {
                data.nonReadCount > 300 -> {
                    binding.nonRead.visibility = View.VISIBLE
                    binding.nonRead.text = "300+"
                }
                data.nonReadCount == 0 -> {
                    binding.nonRead.visibility = View.INVISIBLE
                }
                else -> {
                    binding.nonRead.visibility = View.VISIBLE
                    binding.nonRead.text = data.nonReadCount.toString()
                }
            }

        }


    }
}