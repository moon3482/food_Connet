package com.example.abled_food_connect.retrofit


import com.example.abled_food_connect.data.JoinRoomCheck
import com.example.abled_food_connect.data.LoadingRoom
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RoomAPI {
    @FormUrlEncoded
    @POST("createRoom.php")
    fun createRoom(
        @Field("title") title: String,
        @Field("info") info: String,
        @Field("numOfPeople") numOfPeople: String,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("address") address: String,
        @Field("roadAddress") roadaddress: String,
        @Field("shopName") shopName: String,
        @Field("keyWords") keyWords: String,
        @Field("gender") gender: String,
        @Field("minimumAge") minimumAge: String,
        @Field("maximumAge") maximumAge: String,
        @Field("hostName") hostName: String
    ): Call<String>


    @FormUrlEncoded
    @POST("MainFragmentLoading.php")
    fun loadingRoomGet(
        @Field("userId") userId: String

    ): Call<LoadingRoom>


    @FormUrlEncoded
    @POST("RoomJoinCheck.php")
    fun joinRoomCheck(
        @Field("roomId") roomId: String,
        @Field("userId") userId: String,
        @Field("hostName") hostName: String

    ): Call<JoinRoomCheck>

    @FormUrlEncoded
    @POST("RoomJoin.php")
    fun joinRoom(
        @Field("roomId") roomId: String,
        @Field("userId") userId: String

    ): Call<API.joinRoomClass>
}
