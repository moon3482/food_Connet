package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class ReviewParentPageCommentGetData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("total_comment_count")
    var comment_count : String,
    @SerializedName("commentlist")
    var commentList : List<ReviewParentPageCommentGetDataItem>
)
