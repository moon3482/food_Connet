package com.example.abled_food_connect.interfaces

import com.example.abled_food_connect.data.ReviewFragmentLoadingData
import retrofit2.Call
import retrofit2.http.POST

interface ReviewFragRvUsingInterface {

    //@FormUrlEncoded
    @POST("review/review_list_get.php")
    fun review_frag_rv_using_interface(
    ): Call<ReviewFragmentLoadingData>



}