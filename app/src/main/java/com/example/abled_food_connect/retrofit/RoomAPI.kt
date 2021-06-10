package com.example.abled_food_connect.retrofit


import com.example.abled_food_connect.data.JoinRoomCheck
import com.example.abled_food_connect.data.LoadingRoom
import com.example.abled_food_connect.data.Message
import retrofit2.Call
import retrofit2.http.*

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
        @Field("roadAddress") roadAddress: String,
        @Field("placeName") placeName:String,
        @Field("shopName") shopName: String,
        @Field("keyWords") keyWords: String,
        @Field("gender") gender: String,
        @Field("minimumAge") minimumAge: String,
        @Field("maximumAge") maximumAge: String,
        @Field("hostName") hostName: String
    ): Call<API.createRoomHost>


    @FormUrlEncoded
    @POST("MainFragmentLoading.php")
    fun loadingRoomGet(
        @Field("userId") userId: String

    ): Call<LoadingRoom>


    @FormUrlEncoded
    @POST("RoomJoinCheck.php")
    fun joinRoomCheck(
        @Field("roomId") roomId: String,
        @Field("nickName") nickName: String,
        @Field("hostName") hostName: String

    ): Call<JoinRoomCheck>

    @FormUrlEncoded
    @POST("RoomJoin.php")
    fun joinRoom(
        @Field("roomId") roomId: String,
        @Field("nickName") nickName: String

    ): Call<API.joinRoomClass>


    @retrofit2.http.Multipart
    @POST("/upload")  open
    fun uploadImage(@Part image: Part?): Call<Message?>?
}
