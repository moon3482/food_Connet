package com.example.abled_food_connect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChatItem
import com.example.abled_food_connect.data.ItemType
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(context: Context, arrayList: ArrayList<ChatItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val context = context
    val arrayList = arrayList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val context: Context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return when (viewType) {

            ItemType.LEFT_MESSAGE -> {
                view = inflater.inflate(R.layout.chat_others_message, parent, false)
                LeftMessage(view)
            }
            else -> {
                view = inflater.inflate(R.layout.chat_my_message, parent, false)
                RightMessage(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LeftMessage) {

            var chatItem: ChatItem = arrayList[position]
            var leftHolder = (holder as LeftMessage)
            leftHolder.message.text = chatItem.content
            leftHolder.nickname.text = chatItem.name
            leftHolder.time.text = chatItem.sendTime

        } else {

            var chatItem: ChatItem = arrayList[position]
            var rightHolder = (holder as RightMessage)
            rightHolder.message.text = chatItem.content
            rightHolder.time.text = chatItem.sendTime
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return arrayList[position].viewType

    }

    class LeftMessage(view: View) : RecyclerView.ViewHolder(view) {
        var nickname = view.findViewById<TextView>(R.id.chatOthersNickName)
        var message = view.findViewById<TextView>(R.id.chatOthersMessageText)
        var profileImage = view.findViewById<CircleImageView>(R.id.chatOthersImage)
        var time = view.findViewById<TextView>(R.id.chatOthersTimeStamp)
    }

    class RightMessage(view: View) : RecyclerView.ViewHolder(view) {
        var message = view.findViewById<TextView>(R.id.chatMyMessageText)
        var time = view.findViewById<TextView>(R.id.chatMyTimeStamp)
    }
}