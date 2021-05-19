package com.example.abled_food_connect.Interfaces

import com.example.abled_food_connect.Data.ReviewFragmentItemData
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ReviewFragRvUsingInterface {

    //@FormUrlEncoded
    @POST("review/review_test.php")
    fun review_frag_rv_using_interface(
//        @Field("title") title : String ,
//        @Field("description") description : String ,
//        @Field("restaurant_address") restaurant_address : String
        ): Call<ReviewFragmentItemData>



}