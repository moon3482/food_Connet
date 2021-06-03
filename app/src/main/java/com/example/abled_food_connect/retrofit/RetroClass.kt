package com.example.abled_food_connect.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroClass {
    private const val BASE_URL = "http://52.78.107.230/"


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val _api = retrofit.create(RoomAPI::class.java)
    val api: RoomAPI
        get() = _api
}