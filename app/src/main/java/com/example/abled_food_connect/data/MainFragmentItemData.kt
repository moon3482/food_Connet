package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MainFragmentItemData(
    @SerializedName("roomId")
    var roomId: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("info")
    var info: String,
    @SerializedName("nowNumOfPeople")
    var nowNumOfPeople: Int,
    @SerializedName("numOfPeople")
    var numOfPeople: Int,
    @SerializedName("date")
    var date: String,
    @SerializedName("time")
    var time: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("roadAddress")
    var roadAddress: String,
    @SerializedName("shopName")
    var shopName: String,
    @SerializedName("placeName")
    var placeName: String,
    @SerializedName("keyWords")
    var keyWords: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("minimumAge")
    var minimumAge: Int,
    @SerializedName("maximumAge")
    var maximumAge: Int,
    @SerializedName("hostName")
    var hostName: String,
    @SerializedName("roomStatus")
    var roomStatus: Double,
    @SerializedName("map_x")
    var mapX:Double,
    @SerializedName("map_y")
    var mapY:Double

) {
    private fun CreateRoom() {

    }
}