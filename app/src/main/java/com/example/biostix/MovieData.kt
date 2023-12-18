package com.example.biostix

data class MovieData(
    var title: String ?= null,
    var genres: ArrayList<String>,
    var duration: String ?= null,
    var desc: String ?= null,
    var image: String? = null
)

