package com.example.abled_food_connect.retrofit


import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.MainFragmentItemData
import retrofit2.Call
import retrofit2.http.*

interface RoomAPI {
    @FormUrlEncoded
    @POST("createRoom.php")
        fun createRoom(
            @Field("title") title:String,
            @Field("info") info:String,
            @Field("numOfPeople")numOfPeople:String,
            @Field("date") date:String,
            @Field("time") time:String,
            @Field("address") address:String,
            @Field("shopName") shopName:String,
            @Field("keyWords") keyWords:String,
            @Field("gender") gender:String,
            @Field("minimumAge") minimumAge:String,
            @Field("maximumAge") maximumAge:String,
            @Field("hostName") hostName:String
        ):Call<String>


        @FormUrlEncoded
    @POST("MainFragmentLoading.php")
    fun loadingRoomGet(
        @Field("userId") userId:String

    ):Call<LoadingRoom>
    }
