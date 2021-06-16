package com.example.abled_food_connect.retrofit


import com.example.abled_food_connect.data.*
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
        @Field("placeName") placeName: String,
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
    @POST("/upload")
    open
    fun uploadImage(@Part image: Part?): Call<Message?>?

    @FormUrlEncoded
    @POST("/groupChat/datelineCheck.php")
    fun timelineCheck(
        @Field("datetime") datetime: String?
    ): Call<String>

    @FormUrlEncoded
    @POST("/groupChat/datelineCheck.php")
    fun getPageMaxNum(
        @Field("roomId") roomId: String?
    ): Call<Int>

    @GET("/groupChat/chatPagenation.php")
    fun pagination(
        @Query("roomId") roomId: String,
        @Query("page") page: Int
    ): Call<paginationData>


}

