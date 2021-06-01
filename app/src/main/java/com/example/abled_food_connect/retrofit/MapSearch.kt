package com.example.abled_food_connect.retrofit

import com.example.abled_food_connect.data.kakaoDataClass.KakaoLocalSearch
import com.example.abled_food_connect.data.naverDataClass.NaverSearchLocal
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapSearch {

        @GET("v1/search/local.json")
    fun naverMapSearch(
                @Header("X-Naver-Client-Id") clientId: String,
                @Header("X-Naver-Client-Secret") clientSecret: String,
                @Query("query") query: String,
                @Query("display") display: Int? = null,
                @Query("start") start: Int? = null
            ): Call<NaverSearchLocal>

    @GET("v2/local/search/keyword.json")
    fun kakaoMapSearch(
        @Header("Authorization") header :String,
        @Query("query")query:String
    ) : Call<KakaoLocalSearch>
}