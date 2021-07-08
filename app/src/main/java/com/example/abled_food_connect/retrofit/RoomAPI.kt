package com.example.abled_food_connect.retrofit


import com.example.abled_food_connect.data.*
import retrofit2.Call
import retrofit2.http.*

interface RoomAPI {
    @FormUrlEncoded
    @POST("createRoom.php")
    fun createRoom(
        @Field("userIndexId") userIndexId: String,
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
        @Field("hostName") hostName: String,
        @Field("map_x") map_x: String,
        @Field("map_y") map_y: String
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
        @Field("nickName") nickName: String,
        @Field("userIndexId") userIndexId: String

    ): Call<API.joinRoomClass>


    @retrofit2.http.Multipart
    @POST("/upload")
    open
    fun uploadImage(@Part image: Part?): Call<Message?>?

    @FormUrlEncoded
    @POST("/groupChat/datelineCheck.php")
    fun timelineCheck(
        @Field("datetime") datetime: String?,
        @Field("roomId") roomId: String
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

    @FormUrlEncoded
    @POST("/groupChat/joinSubscription.php")
    fun joinSubscription(
        @Field("roomId") roomId: String?,
        @Field("userIndexId") userIndexId: String?
    ): Call<String>

    @FormUrlEncoded
    @POST("/groupChat/subscriptionCheck.php")
    fun hostSubscriptionCheck(
        @Field("roomId") roomId: String?,

        ): Call<ChatRoomSubscriptionResult>

    @FormUrlEncoded
    @POST("/groupChat/subscriptionUpdate.php")
    fun hostSubscriptionStatusUpdate(
        @Field("subNumber") subNumber: String?,
        @Field("status") status: String?

    ): Call<String>

    @FormUrlEncoded
    @POST("/groupChat/joinRoomMember.php")
    fun joinRoomMember(
        @Field("roomId") roomId: String
    ): Call<ArrayList<LoadRoomUsers>>

    @FormUrlEncoded
    @POST("/groupChat/exitRoom.php")
    fun exitRoom(
        @Field("roomId") roomId: String,
        @Field("userIndexId") userIndexId: String,
        @Field("userNickName") userNickName: String
    ): Call<String>

    @FormUrlEncoded
    @POST("groupChat/LoadGroupChatList.php")
    fun loadGroupChatList(
        @Field("userIndexId") userIndexId: String,
        @Field("userNickName") userNickName: String


    ): Call<LoadingRoom>


    @FormUrlEncoded
    @POST("groupChat/mapActivityTimeCheck.php")
    fun roomStatusTime(
        @Field("roomId")roomId: String
    ):Call<Double>

    @FormUrlEncoded
    @POST("groupChat/mapActivityMember.php")
    fun memberLocation(
        @Field("roomId")roomId: Int
    ):Call<GroupChatLocationData>
}

