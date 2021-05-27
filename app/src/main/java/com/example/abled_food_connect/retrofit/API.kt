package com.example.abled_food_connect.retrofit


import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

class API {
    interface foodCoonnectCreateRoom {
        @POST("createRoom.php")
        fun createRoom(
            @Query("title") title:String,
            @Query("info") info:String,
            @Query("numOfPeople")numOfPeople:Int,
            @Query("date") date:String,
            @Query("adress") adress:String,
            @Query("shopName") shopName:String,
            @Query("keyWords") keyWords:String,
            @Query("gender") gender:String,
            @Query("minimumAge") minimumAge:Int,
            @Query("maximumAge") maximumAge:Int
        ):Call<String>
    }
    //회원가입시 닉네임이 중복되는지 확인한다.
    interface nicNameCheck{
        @FormUrlEncoded
        @POST("user_info/nicname_duplicate_check.php")
        fun checkNicName(
            @Field("nick_name") nick_name:String ):Call<String>
    }
    interface reviewWriting{
        // 리뷰작성하기 보내기
        @Multipart
        @POST("review/review_writing.php")
        fun review_Writing_Request(
            //@Part itemphoto: ArrayList<MultipartBody.Part>,
            @Part itemphoto: List<MultipartBody.Part>,
            @Part("room_tb_id") room_tb_id: Int,
            @Part("writer_uid") writer_uid: String,
            @Part("writer_nicname") writer_nicname: String,
            @Part("restaurant_address") restaurant_address: String,
            @Part("restaurant_name") restaurant_name: String,
            @Part("reporting_date") reporting_date: String,
            @Part("appointment_day") appointment_day: String,
            @Part("appointment_time") appointment_time: String,
            @Part("review_description") review_description: String,
            @Part("rating_star_taste") rating_star_taste: Int,
            @Part("rating_star_service") rating_star_service: Int,
            @Part("rating_star_clean") rating_star_clean: Int,
            @Part("rating_star_interior") rating_star_interior: Int): Call<String>
    }
}