package com.example.abled_food_connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abled_food_connect.adapter.ChatRoomJoinSubscriptionRCVAdapter
import com.example.abled_food_connect.data.ChatRoomSubscriptionResult
import com.example.abled_food_connect.data.ChatRoomUserData
import com.example.abled_food_connect.data.MessageData
import com.example.abled_food_connect.data.RoomData
import com.example.abled_food_connect.databinding.ActivityJoinRoomSubscriptionBinding
import com.example.abled_food_connect.retrofit.RoomAPI
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JoinRoomSubscriptionActivity : AppCompatActivity() {
    val binding by lazy { ActivityJoinRoomSubscriptionBinding.inflate(layoutInflater) }
    lateinit var roomId: String
    lateinit var socket: Socket
    lateinit var gson:Gson
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        roomId = intent.getStringExtra("roomId").toString()
        gson = Gson()



    }


    override fun onStart() {
        super.onStart()
        init()
    }

    override fun onStop() {
        super.onStop()
        socket.emit("left", gson.toJson(RoomData(MainActivity.loginUserNickname, roomId,MainActivity.user_table_id)))
        socket.disconnect()
    }

    private fun hostSubscriptionCheck(roomId: String) {
        val retrofit =
            Retrofit.Builder()
                .baseUrl(getString(R.string.http_request_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build()

        val server = retrofit.create(RoomAPI::class.java).hostSubscriptionCheck(this.roomId)
            .enqueue(object : Callback<ChatRoomSubscriptionResult> {
                override fun onResponse(
                    call: Call<ChatRoomSubscriptionResult>,
                    response: Response<ChatRoomSubscriptionResult>
                ) {
                    val list: ChatRoomSubscriptionResult? = response.body()
                    if (list!!.success) {
                        val userList = ArrayList<ChatRoomUserData>()
                        for(item in list.userList){
                            if(item.status == 0||item.status ==1 ){
                                userList.add(item)
                            }
                        }

                        binding.joinRoomSubscriptionRCV.layoutManager =
                            LinearLayoutManager(this@JoinRoomSubscriptionActivity)
                        binding.joinRoomSubscriptionRCV.adapter =
                            ChatRoomJoinSubscriptionRCVAdapter(
                                this@JoinRoomSubscriptionActivity,
                                userList,socket,roomId
                            )

                        if(userList.size>0){
                           binding.joinRoomSubscriptionRCV.visibility = View.VISIBLE
                            binding.tvNonSubscription.visibility = View.GONE
                        }else{
                            binding.joinRoomSubscriptionRCV.visibility =View.GONE
                            binding.tvNonSubscription.visibility=View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<ChatRoomSubscriptionResult>, t: Throwable) {

                }
            })
    }

    fun init() {
        socket = IO.socket(getString(R.string.chat_socket_url))
        socket.connect()
        socket.on(
            Socket.EVENT_CONNECT,
            Emitter.Listener {
                socket.emit(
                    "enter",
                    gson.toJson(RoomData(MainActivity.loginUserNickname, roomId,MainActivity.user_table_id))
                )
            })

        hostSubscriptionCheck(roomId)
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