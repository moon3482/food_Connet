package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewFragmentItemData(
    @SerializedName("success")
    var success : String,
    @SerializedName("roomList")
    var roomList : List<Items>
)


data class Items(
    @SerializedName("title")
    val title : String,
    @SerializedName("description")
    val description : String,
    @SerializedName("restaurant_address")
    val restaurant_address : String
)
