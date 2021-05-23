package com.example.abled_food_connect.data


import com.google.gson.annotations.SerializedName

data class KAKAOLocalSearchData(
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("meta")
    val meta: Meta
)