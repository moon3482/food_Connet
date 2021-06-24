package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.data.ChatRoomUserData
import com.example.abled_food_connect.data.LoadRoomUsers
import de.hdodenhof.circleimageview.CircleImageView

class ChatRoomUserListRCVAdapter(
    val context: Context,
    val arrayList: ArrayList<LoadRoomUsers>,
    val hostName: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatroomUserHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_room_user_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatRoomUserData: LoadRoomUsers = arrayList[position]
        val holder = holder as ChatroomUserHolder

        holder.profileImage.load(context.getString(R.string.http_request_base_url) + chatRoomUserData.userThumbnail)
        holder.userId.text = chatRoomUserData.userNickname
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("writer_user_tb_id", chatRoomUserData.userIndexId.toInt())
            context.startActivity(intent)
        }
        if (hostName == MainActivity.loginUserNickname) {
            holder.userKickButton.visibility = View.VISIBLE


        } else {
            holder.userKickButton.visibility = View.INVISIBLE
        }
        if (chatRoomUserData.userNickname == hostName) {
            holder.hostUserCrown.visibility = View.VISIBLE
        } else {
            holder.hostUserCrown.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ChatroomUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage = itemView.findViewById<CircleImageView>(R.id.ChatRoomUserProfileImage)
        var userId = itemView.findViewById<TextView>(R.id.chatUserListId)
        var userKickButton = itemView.findViewById<ImageButton>(R.id.userKickButton)
        var hostUserCrown = itemView.findViewById<ImageView>(R.id.hostUserCrown)

    }
}