package com.example.abled_food_connect.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.abled_food_connect.R
import com.example.abled_food_connect.UserProfileActivity
import com.example.abled_food_connect.array.array
import com.example.abled_food_connect.data.ChatRoomUserData
import com.example.abled_food_connect.data.MessageData
import com.example.abled_food_connect.data.member
import com.example.abled_food_connect.retrofit.API
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatRoomJoinSubscriptionRCVAdapter(val context: Context, val arrayList: ArrayList<ChatRoomUserData>,val socket:Socket,val roomId:String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
var gson:Gson = Gson()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChatroomUserHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_room_subscription_list_item, parent, false)
        )
    }
    lateinit var members:String

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
            timelimeCheck(chatRoomUserData)
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.CancleButton.setOnClickListener {
            updateSubscriptionStatus(chatRoomUserData.subscriptionId.toString(),"3")
            arrayList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.InformationButton.setOnClickListener {
            val intent = Intent(context,UserProfileActivity::class.java)
            intent.putExtra("writer_user_tb_id",chatRoomUserData.userIndexId.toInt())
            context.startActivity(intent)
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
        var InformationButton = itemView.findViewById<ImageButton>(R.id.userInfomation)

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
    fun timelimeCheck(chatRoomUserData:ChatRoomUserData) {


        val retrofit =
            Retrofit.Builder()
                .baseUrl("http://52.78.107.230/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java)

        server.timelineCheck("datetime", roomId).enqueue(object : Callback<member> {
            override fun onResponse(call: Call<member>, response: Response<member>) {

                if (response.body()!!.dateline) {
                    members = response.body()!!.members
                    timeLineadd()
                    socket.emit("join",gson.toJson(
                        MessageData("JOINMEMBER",
                            "JOINMEMBER",
                            roomId,
                            chatRoomUserData.nickName, "SERVER",
                            "SERVER",members)
                    ))
                } else {
                    members = response.body()!!.members
                    socket.emit("join",gson.toJson(
                        MessageData("JOINMEMBER",
                            "JOINMEMBER",
                            roomId,
                            chatRoomUserData.nickName, "SERVER",
                            "SERVER",members)
                    ))
                }
            }

            override fun onFailure(call: Call<member>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }
    private fun timeLineadd() {
        socket.emit(
            "TIMELINE",
            gson.toJson(
                MessageData(
                    "TIMELINE",
                    "TIMELINE",
                    roomId,
                    "SERVER", "SERVER",
                    "SERVER"
                ,members)
            )
        )
    }
}