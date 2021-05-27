package com.example.abled_food_connect.data


import com.google.gson.annotations.SerializedName

data class LoginDataClass(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userNickname")
    val userNickname: String
)