package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class DirectMessageData(
    @SerializedName("roomName")
    val roomName: String,
    @SerializedName("user_tb_id")
    val user_tb_id: Int,
    @SerializedName("userNicName")
    val userNicName: String,
    @SerializedName("userProfileImage")
    val userProfileImage: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("TextOrImage")
    val TextOrImage: String
)
