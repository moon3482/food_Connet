package com.example.abled_food_connect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.DirectMessageData

class DirectMessageRvAdapter (private val mydatas: MutableList<DirectMessageData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var datas = mydatas

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        val view : View?
        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_my_message,
                    parent,
                    false
                )
                MyMessageViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_others_message,
                    parent,
                    false
                )
                YourMessageViewHolder(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_my_image,
                    parent,
                    false
                )
                MyImageViewHolder(view)
            }

            3 -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_others_image,
                    parent,
                    false
                )
                YourImageViewHolder(view)
            }

            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.direct_message_chat_my_message,
                    parent,
                    false
                )
                MyMessageViewHolder(view)
            }
        }
    }
    override fun getItemCount(): Int = datas.size

    override fun getItemViewType(position: Int): Int {
        var type : Int = 0

        if(datas[position].TextOrImage =="Text" && datas[position].user_tb_id == MainActivity.user_table_id){
            type = 0
        } else if(datas[position].TextOrImage =="Text" && datas[position].user_tb_id != MainActivity.user_table_id){
            type = 1
        } else if(datas[position].TextOrImage =="Image" && datas[position].user_tb_id == MainActivity.user_table_id){
            type = 2
        } else if(datas[position].TextOrImage =="Image" && datas[position].user_tb_id != MainActivity.user_table_id){
            type = 3
        }
        return type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {



        when(getItemViewType(position)) {
            0 -> {
                (holder as MyMessageViewHolder).bind(datas[position])

            }
            1 -> {
                (holder as YourMessageViewHolder).bind(datas[position])

            }
            2 -> {
                (holder as MyImageViewHolder).bind(datas[position])

            }
            3 -> {
                (holder as YourImageViewHolder).bind(datas[position])

            }

            else -> {
                (holder as MyMessageViewHolder).bind(datas[position])
            }
        }
    }

    inner class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val MyMessage: TextView = view.findViewById(R.id.chatMyMessageText)
//        private val txtAge: TextView = view.findViewById(R.id.tv_rv_age)
//        private val imgProfile: ImageView = view.findViewById(R.id.img_rv_photo)

        fun bind(item: DirectMessageData) {
            MyMessage.text = item.message
            //txtAge.text = item.age.toString()
            //Glide.with(itemView).load(item.image).into(imgProfile)

        }
    }



    inner class YourMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val YourImgProfile: ImageView = view.findViewById(R.id.chatOthersImage)
        private val YourNicName: TextView = view.findViewById(R.id.chatOthersNickName)
        private val YourMessage: TextView = view.findViewById(R.id.chatOthersMessageText)


        fun bind(item: DirectMessageData) {

            Glide.with(YourImgProfile.context).load(item.userProfileImage).into(YourImgProfile)
            YourNicName.text = item.userNicName
            YourMessage.text = item.message


        }
    }
    inner class MyImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val MyImage: TextView = view.findViewById(R.id.chatMyMessageText)


        fun bind(item: DirectMessageData) {
            MyImage.text = item.message
            //Glide.with(itemView).load(item.image).into(imgProfile)

        }
    }

    inner class YourImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val YourImgProfile: ImageView = view.findViewById(R.id.chatOthersImage)
        private val YourNicName: TextView = view.findViewById(R.id.chatOthersNickName)
        private val YourMessage: TextView = view.findViewById(R.id.chatOthersMessageText)

        fun bind(item: DirectMessageData) {
            Glide.with(YourImgProfile.context).load(item.userProfileImage).into(YourImgProfile)
            YourNicName.text = item.userNicName
            YourMessage.text = item.message

        }
    }
}