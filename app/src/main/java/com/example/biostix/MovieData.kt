package com.example.biostix

data class MovieData(
    var title: String? = null,
    var genres: ArrayList<String> = ArrayList(),
    var duration: String? = null,
    var desc: String? = null,
    var image: String? = null
) {
    constructor() : this(null, ArrayList(), null, null, null)

    constructor(
        titleArg: String?,
        genresArg: ArrayList<String>,
        durationArg: String?,
        descArg: String?,
        imageArg: String?,
        additionalArg: String?
    ) : this() {
        title = titleArg
        genres.addAll(genresArg)
        duration = durationArg
        desc = descArg
        image = imageArg
    }
}