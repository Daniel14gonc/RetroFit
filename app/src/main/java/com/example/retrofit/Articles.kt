package com.example.retrofit

import com.google.gson.annotations.SerializedName

data class Articles (
    @SerializedName("author") var author: String,
    @SerializedName("title") var title: String,
    @SerializedName("description") var description: String,
    @SerializedName("urlToImage") var urlToImage: String
)