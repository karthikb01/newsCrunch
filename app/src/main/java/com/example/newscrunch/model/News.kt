package com.example.newscrunch.model

import java.time.LocalDateTime

//this data class represents a model for the news that will be fetched from the api
//note : i haven't used all the fields from the individual news json objects
//that would be an improvement if suppose i want to show the description or the content of the news in say a new activity
data class News(
    val source : String,
    val title: String,
    val author: String,
    val url: String,
    val image: String,
    val publishedAt: LocalDateTime
)