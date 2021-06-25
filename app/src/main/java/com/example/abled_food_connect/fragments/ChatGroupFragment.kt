package com.example.abled_food_connect.fragments

import android.content.Context
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import com.example.abled_food_connect.adapter.GroupChatListFragmentAdapter
import com.example.abled_food_connect.adapter.MainFragmentAdapter
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import com.example.abled_food_connect.retrofit.RoomAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatGroupFragment : Fragment() {
lateinit var recyclerView: RecyclerView
lateinit var Gadapter: GroupChatListFragmentAdapter
    companion object {
        const val TAG: String = "그룹채팅 프래그먼트 로그"
        fun newInstance(): ChatGroupFragment {
            return ChatGroupFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChatGroupFragment.TAG,"그룹채팅 프래그먼트 onCreate()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ChatGroupFragment.TAG,"그룹채팅 프래그먼트 onAttach()")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_group, container, false)
        recyclerView = view.findViewById(R.id.ChatGroupRCV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadChatList()


        return view
    }


    fun loadChatList(){

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.http_request_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()

        val server = retrofit.create(RoomAPI::class.java)
        server.loadGroupChatList(MainActivity.user_table_id.toString(),MainActivity.loginUserNickname).enqueue(object :Callback<LoadingRoom>{
            override fun onResponse(
                call: retrofit2.Call<LoadingRoom>,
                response: Response<LoadingRoom>
            ) {
                val list: LoadingRoom? = response.body()
                if(list !=null ) {
                    val array: ArrayList<MainFragmentItemData> = list.roomList
                    Gadapter = GroupChatListFragmentAdapter(requireContext(),array)
                    recyclerView.adapter = Gadapter
                }
            }

            override fun onFailure(call: retrofit2.Call<LoadingRoom>, t: Throwable) {

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