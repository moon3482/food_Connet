package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class DirectMessageRvData(
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
    val TextOrImage: String,
    @SerializedName("sendTime")
    val sendTime: String,
    @SerializedName("message_check")
    val message_check: String

)
