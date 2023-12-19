package com.example.biostix

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.biostix.databinding.ActivityUpdateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private val dbReference = FirebaseFirestore.getInstance().collection("movie")
    private var imageUri: Uri? = null
    private var movieData: MovieData? = null

    private val genreArray = arrayOf(
        "Action", "Supernatural", "Adventure", "Military", "Psychology",
        "Horror", "Thriller", "Spy", "Romance", "Fantasy", "Sci-Fi",
        "Another World", "Slice Of Life", "Religion", "Fable"
    )
    private var selectedGenre = BooleanArray(genreArray.size)
    private val genreList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("MOVIE_ID")

        if (movieId != null) {
            getOldMovieData(movieId)
        }

        binding.imageUpload.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.btnUpdate.setOnClickListener {
            movieId?.let { updateData(it) }
        }

        binding.cardGenre.setOnClickListener {
            showGenreDialog()
        }
    }

    private lateinit var oldImageRef: StorageReference

    private fun getOldMovieData(movieId: String) {
        dbReference.document(movieId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val movieData = document.toObject(MovieData::class.java)
                    if (movieData != null) {
                        val oldTitle = movieData.title
                        val oldDuration = movieData.duration
                        val oldDesc = movieData.desc
                        val oldImageUrl = movieData.image
                        val oldGenres = movieData.genres

                        if (oldImageUrl != null) {
                            Glide.with(this)
                                .load(oldImageUrl)
                                .into(binding.imageUpload)
                            oldImageRef = FirebaseStorage.getInstance().reference.child(oldImageUrl)
                        } else {
                            binding.imageUpload.setImageResource(R.drawable.palceholder_image)
                        }

                        binding.updateTitle.setText(oldTitle)
                        binding.updateDuration.setText(oldDuration)
                        binding.updateDesc.setText(oldDesc)

                        if (oldGenres != null) {
                            genreList.clear()
                            selectedGenre.fill(false)
                            oldGenres.forEach { genre ->
                                val index = genreArray.indexOf(genre)
                                if (index != -1) {
                                    genreList.add(genre)
                                    selectedGenre[index] = true
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to get old movie data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showGenreDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Select Genre")
            .setCancelable(false)
            .setMultiChoiceItems(genreArray, selectedGenre) { _, which, isChecked ->
                if (isChecked) {
                    genreList.add(genreArray[which])
                } else {
                    genreList.remove(genreArray[which])
                }
            }
            .setPositiveButton("Ok") { dialog, _ ->
                val selectedGenres = genreList.joinToString(", ")
                binding.selectGenre.text = selectedGenres
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("Clear all") { _, _ ->
                selectedGenre.fill(false)
                genreList.clear()
                binding.selectGenre.text = ""
            }

        builder.show()
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.imageUpload.setImageURI(it)
    }

    private fun updateData(movieId: String) {
        val updatedTitle = binding.updateTitle.text.toString()
        val updatedDuration = binding.updateDuration.text.toString()
        val updatedDesc = binding.updateDesc.text.toString()

        if (updatedTitle.isNotEmpty() && updatedDuration.isNotEmpty() && updatedDesc.isNotEmpty() && imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("Images")
            val imageRef = imageUri?.let { uri ->
                val fileExtension = getFileExtension(uri)
                fileExtension?.let {
                    storageRef.child("$movieId.$it")
                }
            }

            imageRef?.putFile(imageUri!!)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        val updatedMovieData = MovieData(
                            updatedTitle,
                            genreList,
                            updatedDuration,
                            updatedDesc,
                            imageUrl.toString()
                        )

                        dbReference.document(movieId)
                            .set(updatedMovieData)
                            .addOnSuccessListener {
                                // Delete old image
                                oldImageRef.delete().addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Old image deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { exception ->
                                    Toast.makeText(
                                        this,
                                        "Failed to delete old image: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                Toast.makeText(
                                    this,
                                    "Data updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to update data: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Failed to upload image: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Please fill in all fields and select an image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = applicationContext.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}
