package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewChildPageCommentGetData(

    @SerializedName("success")
    var success : Boolean,
    @SerializedName("comment_count")
    var comment_count : String,
    @SerializedName("commentlist")
    var childPageCommentList : List<ReviewChildPageCommentGetDataItem>
)
