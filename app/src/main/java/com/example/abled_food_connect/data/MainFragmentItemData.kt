package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class MainFragmentItemData(
    @SerializedName("title")
    var title: String,
    @SerializedName("info")
    var info: String,
    @SerializedName("numOfPeople")
    var numOfPeople: Int,
    @SerializedName("date")
    var date : String,
    @SerializedName("time")
    var time : String,
    @SerializedName("adress")
    var adress: String,
    @SerializedName("shopName")
    var shopName: String,
    @SerializedName("keyWords")
    var keyWords: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("minimumAge")
    var minimumAge: Int,
    @SerializedName("maximumAge")
    var maximumAge: Int,
    @SerializedName("hostName")
    var hostName:String,
    @SerializedName("roomStatus")
    var roomStatus:Int

){
private fun CreateRoom(){

}
}