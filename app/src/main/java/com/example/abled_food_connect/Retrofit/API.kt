package com.example.abled_food_connect.Retrofit


import com.example.abled_food_connect.Data.MainFragmentItemData
import com.example.abled_food_connect.R
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
        ):Call<MainFragmentItemData>
    }


    //회원가입시 닉네임이 중복되는지 확인한다.
    interface nicNameCheck{
        @FormUrlEncoded
        @POST("user_info/nicname_duplicate_check.php")
        fun checkNicName(
            @Field("nick_name") nick_name:String ):Call<String>
    }
}