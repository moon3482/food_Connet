package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewDetailViewLikeAndCommentCountCheckData(
    @SerializedName("islikeClicked")
    val islikeClicked : Boolean,
    @SerializedName("like_count")
    val like_count : Int,
    @SerializedName("comment_count")
    val comment_count : Int
)
