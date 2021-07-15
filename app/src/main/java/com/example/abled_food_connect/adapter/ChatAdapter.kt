package com.example.abled_food_connect.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChatItem
import com.example.abled_food_connect.data.ItemType
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.ArrayList

class ChatAdapter(context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val context = context
    var arrayList = ArrayList<ChatItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val context: Context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



        return when (viewType) {

            ItemType.LEFT_MESSAGE -> {
                view = inflater.inflate(R.layout.chat_others_message, parent, false)
                LeftMessage(view)
            }
            ItemType.CENTER_MESSAGE -> {
                view = inflater.inflate(R.layout.chat_server_item, parent, false)
                ServerMessage(view)
            }
            ItemType.STARTANDEND -> {
                view = inflater.inflate(R.layout.chat_star_end, parent, false)
                StartAndEnd(view)
            }
            ItemType.LEFT_IMAGE_MESSAGE -> {
                view = inflater.inflate(R.layout.chat_others_image_message, parent, false)
                LeftImageMessage(view)
            }
            ItemType.RIGHT_IMAGE_MESSAGE -> {
                view = inflater.inflate(R.layout.chat_my_image_message, parent, false)
                RightImageMessage(view)
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
            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
            if (array.size() > 0) {
                holder.lfreadMembersOthers.visibility = View.VISIBLE
                holder.lfreadMembersOthers.text = array.size().toString()
            } else {
                holder.lfreadMembersOthers.visibility = View.INVISIBLE
            }
            leftHolder.itemView.setPadding(0, 0, 0, 0)

            leftHolder.lfmessage.text = chatItem.content
            leftHolder.lftime.text = chatItem.sendTime
            leftHolder.lfprofileImage.load(context.getString(R.string.http_request_base_url) + chatItem.ThumbnailImage)
            leftHolder.lfmessage.setOnLongClickListener {
                val clipboardManager: ClipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData: ClipData =
                    ClipData.newPlainText("", leftHolder.lfmessage.text.toString())
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                true
            }


        } else if (holder is ServerMessage) {

            var chatItem: ChatItem = arrayList[position]
            var centerHolder = holder
            centerHolder.message.text = chatItem.content
        } else if (holder is RightMessage) {

            var chatItem: ChatItem = arrayList[position]
            var rightHolder = holder
            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
            if (array.size() > 0) {
                holder.rireadMembersMy.visibility = View.VISIBLE
                holder.rireadMembersMy.text = array.size().toString()
            } else {
                holder.rireadMembersMy.visibility = View.INVISIBLE
            }
            rightHolder.rimessage.text = chatItem.content
            rightHolder.ritime.text = chatItem.sendTime
            rightHolder.rimessage.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    val clipboardManager: ClipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData: ClipData =
                        ClipData.newPlainText("", rightHolder.rimessage.text.toString())
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                    return true
                }
            })
        } else if (holder is LeftImageMessage) {
            var chatItem: ChatItem = arrayList[position]
            var leftHolder = holder
            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
            if (array.size() > 0) {
                holder.readMembersOthers.visibility = View.VISIBLE
                holder.readMembersOthers.text = array.size().toString()
            } else {
                holder.readMembersOthers.visibility = View.INVISIBLE
            }
            leftHolder.itemView.setPadding(0, 0, 0, 0)

            leftHolder.message.load(context.getString(R.string.http_request_base_url)+chatItem.content)
            leftHolder.time.text = chatItem.sendTime
            leftHolder.profileImage.load(context.getString(R.string.http_request_base_url) + chatItem.ThumbnailImage)
        }
        else if (holder is RightImageMessage) {
            var chatItem: ChatItem = arrayList[position]
            var rightHolder = holder
            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
            if (array.size() > 0) {
                holder.readMembersMy.visibility = View.VISIBLE
                holder.readMembersMy.text = array.size().toString()
            } else {
                holder.readMembersMy.visibility = View.INVISIBLE
            }
            rightHolder.message.load(context.getString(R.string.http_request_base_url)+chatItem.content)

            rightHolder.time.text = chatItem.sendTime
        } else if (holder is StartAndEnd) {

        }

    }

    override fun getItemViewType(position: Int): Int {
        return arrayList[position].viewType
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    class LeftMessage(view: View) : RecyclerView.ViewHolder(view) {
        var lfnickname = view.findViewById<TextView>(R.id.chatOthersNickName)
        var lfmessage = view.findViewById<TextView>(R.id.chatOthersMessageText)
        var lfprofileImage = view.findViewById<CircleImageView>(R.id.chatOthersImage)
        var lftime = view.findViewById<TextView>(R.id.chatOthersTimeStamp)
        var lfreadMembersOthers = view.findViewById<TextView>(R.id.readMembersOthersMessage)
    }

    class RightMessage(view: View) : RecyclerView.ViewHolder(view) {
        var rimessage = view.findViewById<TextView>(R.id.chatMyMessageText)
        var ritime = view.findViewById<TextView>(R.id.chatMyTimeStamp)
        var rireadMembersMy = view.findViewById<TextView>(R.id.readMembersMyMessage)
    }

    class LeftImageMessage(view: View) : RecyclerView.ViewHolder(view) {
        var nickname = view.findViewById<TextView>(R.id.ImageChatOthersNickName)
        var message = view.findViewById<ImageView>(R.id.ImageChatOthersImage)
        var profileImage = view.findViewById<CircleImageView>(R.id.ImageChatOthersProFileImage)
        var time = view.findViewById<TextView>(R.id.ImageChatOthersTimeStamp)
        var readMembersOthers = view.findViewById<TextView>(R.id.ImageReadMembersOthersMessage)
    }

    class RightImageMessage(view: View) : RecyclerView.ViewHolder(view) {
        var message = view.findViewById<ImageView>(R.id.ImageChatMyImage)
        var time = view.findViewById<TextView>(R.id.ImageChatMyTimeStamp)
        var readMembersMy = view.findViewById<TextView>(R.id.ImageReadMembersMyMessage)
    }

    class ServerMessage(view: View) : RecyclerView.ViewHolder(view) {
        var message = view.findViewById<TextView>(R.id.content_text)

    }

    class StartAndEnd(view: View) : RecyclerView.ViewHolder(view) {

    }
}