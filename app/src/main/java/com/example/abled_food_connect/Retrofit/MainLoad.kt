package com.example.abled_food_connect.Retrofit

import android.app.Activity
import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.abled_food_connect.Adapter.MainFragmentAdapter
import com.example.abled_food_connect.R
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Handler


class MainLoad(var context:Context,var array:ArrayList<MainFragmentItemData>) {

//      fun  load():ArrayList<MainFragmentItemData> {
//
//            var gson: Gson =GsonBuilder()
//                .setLenient()
//                .create()
//
//            var retrofit = Retrofit.Builder()
//                .baseUrl(context.getString(R.string.http_request_base_url))
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//
//            var server = retrofit.create(RoomAPI::class.java)
//
//            server.loadingRoomget("5QG1D09RqpORYfb7-8VRda46wVfQ20lMrWfAUu9oE8s")
//                .enqueue(object :Callback<LoadingRoom>{
//                    override fun onResponse(
//                        call: Call<LoadingRoom>,
//                        response: Response<LoadingRoom>
//                    ) {
////                            var loadingRoom : LoadingRoom? = response.body()
////                            var success :Boolean = loadingRoom!!.success
////                        if (success){
////
////                             array = loadingRoom.roomList
//
//                        }
//                    }
//
////                    override fun onFailure(call: Call<LoadingRoom>, t: Throwable) {
////
////                    }
////
////                })
////          return array
//        }
   }


