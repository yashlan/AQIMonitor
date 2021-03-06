package com.c22_ce02.awmonitorapp.data.response

import android.media.Image
import android.widget.ImageView
import com.google.gson.annotations.SerializedName

data class ArticleResponse(

    @field:SerializedName("items")
    val items: List<ItemsItem>

) {
    data class ItemsItem(

        @field:SerializedName("author")
        val author: Author,

        @field:SerializedName("id")
        val id: String,

        @field:SerializedName("published")
        val published: String,

        @field:SerializedName("title")
        val title: String,

        @field:SerializedName("url")
        val url: String,

        @field:SerializedName("images")
        val images: List<Images>?,

        @field:SerializedName("content")
        val content: String,

        @field:SerializedName("selfLink")
        val selfLink: String
    ) {
        data class Author(

            @field:SerializedName("displayName")
            val displayName: String
        )
        data class Images(
            @field:SerializedName("url")
            val url: String
        )
    }


}




