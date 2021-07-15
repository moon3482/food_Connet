package com.example.abled_food_connect.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.array.array
import com.example.abled_food_connect.data.ChatItem
import com.example.abled_food_connect.data.ItemType
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

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
            ItemType.STARTANDEND->{
                view = inflater.inflate(R.layout.chat_star_end, parent, false)
                StartAndEnd(view)
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
                        holder.readMembersOthers.visibility = View.VISIBLE
                        holder.readMembersOthers.text = array.size().toString()
                    } else {
                        holder.readMembersOthers.visibility = View.INVISIBLE
                    }
                    leftHolder.itemView.setPadding(0, 0, 0, 0)

                    leftHolder.message.text = chatItem.content
                    leftHolder.time.text = chatItem.sendTime
                    leftHolder.profileImage.load(context.getString(R.string.http_request_base_url) + chatItem.ThumbnailImage)
                    leftHolder.message.setOnLongClickListener {
                        val clipboardManager: ClipboardManager =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData: ClipData =
                            ClipData.newPlainText("", leftHolder.message.text.toString())
                        clipboardManager.setPrimaryClip(clipData)
                        Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                        true
                    }


        } else if (holder is ServerMessage) {

            var chatItem: ChatItem = arrayList[position]
            var centerHolder = (holder as ServerMessage)
            centerHolder.message.text = chatItem.content
        } else if (holder is RightMessage){

            var chatItem: ChatItem = arrayList[position]
            var rightHolder = holder
            val array = JsonParser.parseString(chatItem.readMember) as JsonArray
            if (array.size() > 0) {
                holder.readMembersMy.visibility = View.VISIBLE
                holder.readMembersMy.text = array.size().toString()
            } else {
                holder.readMembersMy.visibility = View.INVISIBLE
            }
            rightHolder.message.text = chatItem.content
            rightHolder.time.text = chatItem.sendTime
            rightHolder.message.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    val clipboardManager: ClipboardManager =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData: ClipData =
                        ClipData.newPlainText("", rightHolder.message.text.toString())
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(context, "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                    return true
                }
            })
        }else if (holder is StartAndEnd){

        }

    }

    override fun getItemViewType(position: Int): Int {
        return arrayList[position].viewType
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    class LeftMessage(view: View) : RecyclerView.ViewHolder(view) {
        var nickname = view.findViewById<TextView>(R.id.chatOthersNickName)
        var message = view.findViewById<TextView>(R.id.chatOthersMessageText)
        var profileImage = view.findViewById<CircleImageView>(R.id.chatOthersImage)
        var time = view.findViewById<TextView>(R.id.chatOthersTimeStamp)
        var readMembersOthers = view.findViewById<TextView>(R.id.readMembersOthersMessage)
    }

    class RightMessage(view: View) : RecyclerView.ViewHolder(view) {
        var message = view.findViewById<TextView>(R.id.chatMyMessageText)
        var time = view.findViewById<TextView>(R.id.chatMyTimeStamp)
        var readMembersMy = view.findViewById<TextView>(R.id.readMembersMyMessage)
    }

    class ServerMessage(view: View) : RecyclerView.ViewHolder(view) {
        var message = view.findViewById<TextView>(R.id.content_text)

    }
    class StartAndEnd(view: View):RecyclerView.ViewHolder(view){

    }
}