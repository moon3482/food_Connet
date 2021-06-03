package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewCommentGetData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("commentlist")
    var CommentList : List<ReviewCommentGetDataItem>
)
