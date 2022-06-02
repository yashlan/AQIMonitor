package com.c22_ce02.awmonitorapp.data.model

import com.google.gson.annotations.SerializedName

data class ArticleResponse(

	@field:SerializedName("kind")
	val kind: String,

	@field:SerializedName("items")
	val items: List<ItemsItem>
)

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

	@field:SerializedName("content")
	val content: String,

	@field:SerializedName("selfLink")
	val selfLink: String
)

data class Author(

	@field:SerializedName("displayName")
	val displayName: String
)
