package com.example.biostix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.biostix.databinding.ActivityUserDetailMovieBinding

class UserDetailMovie : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val genres = intent.getStringArrayListExtra("genres")
        val duration = intent.getStringExtra("duration")
        val description = intent.getStringExtra("description")
        val image = intent.getStringExtra("image")

        binding.titleMovie.text = title
        binding.genres.text = genres?.joinToString(", ") ?: ""
        binding.duration.text = duration
        binding.titleDescMovie.text = description

        Glide.with(this)
            .load(image)
            .into(binding.imageDetailMovie)
    }
}
