package com.example.biostix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.biostix.databinding.ActivityUserDetailMovieBinding
import com.example.biostix.databinding.FragmentUserMovieBinding
import com.google.firebase.firestore.auth.User

class UserDetailMovie : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        val title = intent.getStringExtra("title")
        val genres = intent.getStringArrayListExtra("genres")
        val duration = intent.getStringExtra("duration")
        val description = intent.getStringExtra("description")
        val image = intent.getStringExtra("image")

        Log.d("Description", "Description received: $description")

        binding.titleMovie.text = title
        val genresString = genres?.joinToString(", ")
        val genresNoComma = genresString?.replace(",", "")
        binding.genres.text = genresNoComma ?: ""
        binding.duration.text = duration
        binding.deskripsiMovie.text = description ?: ""

        Glide.with(this)
            .load(image)
            .into(binding.imageDetailMovie)
    }

}
