package com.example.abled_food_connect.Interfaces

import com.example.abled_food_connect.Data.ReviewFragmentLoadingData
import retrofit2.Call
import retrofit2.http.POST

interface ReviewFragRvUsingInterface {

    //@FormUrlEncoded
    @POST("review/review_test.php")
    fun review_frag_rv_using_interface(
        ): Call<ReviewFragmentLoadingData>



}