package com.example.abled_food_connect.Data

import com.google.gson.annotations.SerializedName

data class ReviewFragmentLodingDataItem(
    @SerializedName("id")
    val id : String,
    @SerializedName("room_tb_id")
    val room_tb_id : String,
    @SerializedName("writer_uid")
    val writer_uid : String,
    @SerializedName("writer_nicname")
    val writer_nicname : String,
    @SerializedName("restaurant_address")
    val restaurant_address : String,
    @SerializedName("restaurant_name")
    val restaurant_name : String,
    @SerializedName("reporting_date")
    val reporting_date : String,
    @SerializedName("appointment_day")
    val appointment_day : String,
    @SerializedName("appointment_time")
    val appointment_time : String,
    @SerializedName("review_description")
    val review_description : String,
    @SerializedName("rating_star_taste")
    val rating_star_taste : String,
    @SerializedName("rating_star_service")
    val rating_star_service : String,
    @SerializedName("rating_star_clean")
    val rating_star_clean : String,
    @SerializedName("rating_star_interior")
    val rating_star_interior : String,
    @SerializedName("review_picture_0")
    val review_picture_0 : String,
    @SerializedName("review_picture_1")
    val review_picture_1 : String,
    @SerializedName("review_picture_2")
    val review_picture_2 : String

)
