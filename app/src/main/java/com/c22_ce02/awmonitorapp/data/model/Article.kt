package com.c22_ce02.awmonitorapp.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    var imageurl: String? = null,
    var title: String? = null,
    var description: String? = null,
    var created_by: String? = null,
    var created_at: String? = null,
    var url: String? = null
) : Parcelable
