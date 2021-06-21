package com.example.abled_food_connect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.ChatRoomUserData
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.RoomAPI
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatRoomJoinSubscriptionRCVAdapter(val context: Context, val arrayList: ArrayList<ChatRoomUserData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatroomUserHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_room_subscription_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatRoomUserData: ChatRoomUserData = arrayList[position]
        val holder = holder as ChatroomUserHolder
        updateSubscriptionStatus(chatRoomUserData.subscriptionId.toString())
        holder.profileImage.load(context.getString(R.string.http_request_base_url)+chatRoomUserData.thumbnailImage)
        holder.userId.text = chatRoomUserData.nickName
        holder.OkButton.setOnClickListener {
            updateSubscriptionStatus(chatRoomUserData.subscriptionId.toString(),"2")
            val join = API()
            join.joinRoom(context,chatRoomUserData.roomId,chatRoomUserData.nickName,chatRoomUserData.userIndexId)
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.CancleButton.setOnClickListener {
            updateSubscriptionStatus(chatRoomUserData.subscriptionId.toString(),"3")
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ChatroomUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage = itemView.findViewById<CircleImageView>(R.id.chatRoomSubscriptionUserProfileImage)
        var userId = itemView.findViewById<TextView>(R.id.chatRoomSubscriptionUserListId)
        var OkButton = itemView.findViewById<Button>(R.id.SubscriptionOK)
        var CancleButton = itemView.findViewById<Button>(R.id.SubscriptionCancel)

    }
    private fun updateSubscriptionStatus(subNumber:String,status:String?="1"){
        val retrofit =
            Retrofit.Builder()
                .baseUrl(context.getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java).hostSubscriptionStatusUpdate(subNumber,status).enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.body()=="true"){

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }
        })

    }
    private fun createOkHttpClient(): OkHttpClient {
        //Log.d ("TAG","OkhttpClient");
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }
}