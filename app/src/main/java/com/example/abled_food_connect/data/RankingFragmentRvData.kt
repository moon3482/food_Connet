package com.example.abled_food_connect.data

import com.google.gson.annotations.SerializedName

data class RankingFragmentRvData(
    @SerializedName("success")
    var success : Boolean,
    @SerializedName("rankingList")
    var rankingList : List<RankingFragmentRvDataItem>
)
