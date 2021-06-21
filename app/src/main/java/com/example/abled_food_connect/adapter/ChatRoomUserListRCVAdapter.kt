package com.example.abled_food_connect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChatRoomUserData
import de.hdodenhof.circleimageview.CircleImageView

class ChatRoomUserListRCVAdapter(val context: Context, val arrayList: ArrayList<ChatRoomUserData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatroomUserHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_room_user_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatRoomUserData: ChatRoomUserData = arrayList[position]
        val holder = holder as ChatroomUserHolder

        holder.profileImage.load(chatRoomUserData.thumbnailImage)
        holder.userId.text = chatRoomUserData.nickName
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ChatroomUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage = itemView.findViewById<CircleImageView>(R.id.ChatRoomUserProfileImage)
        var userId = itemView.findViewById<TextView>(R.id.chatUserListId)

    }
}