package com.example.biostix

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.biostix.databinding.ActivityCreateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding
    private val dbReference = FirebaseFirestore.getInstance().collection("movie")
    private var imageUri: Uri? = null

    private val genreArray = arrayOf(
        "Action",
        "Supernatural",
        "Adventure",
        "Military",
        "Psychology",
        "Horror",
        "Thriller",
        "Spy",
        "Romance",
        "Fantasy",
        "Sci-Fi",
        "Another World",
        "Slice Of Life",
        "Religion",
        "Fable"
    )
    private var selectedGenre = BooleanArray(genreArray.size)
    private val genreList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardGenre.setOnClickListener {
            showGenreDialog()
        }

        binding.imageUpload.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val title = binding.createTitle.text.toString()
            val duration = binding.createDuration.text.toString()
            val description = binding.createDesc.text.toString()

            if (title.isNotEmpty() && duration.isNotEmpty() && description.isNotEmpty() && imageUri != null) {
                uploadData(title, duration, description)
            } else {
                Toast.makeText(
                    this,
                    "Please fill in all fields and select an image",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = applicationContext.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadData(title: String, duration: String, description: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("Images")
        val imageRef = imageUri?.let { uri ->
            val fileExtension = getFileExtension(uri)
            fileExtension?.let {
                storageRef.child("${System.currentTimeMillis()}.$it")
            }
        }

        imageRef?.putFile(imageUri!!)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    val movieData = hashMapOf(
                        "title" to title,
                        "duration" to duration,
                        "description" to description,
                        "pic" to imageUrl.toString(),
                        "genres" to genreList
                    )

                    dbReference.add(movieData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Data added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val fragmentManager = supportFragmentManager
                            val fragmentTransaction = fragmentManager.beginTransaction()

                            val adminCRUDFragment = AdminCRUD()

                            fragmentTransaction.replace(
                                R.id.fragment_container,
                                adminCRUDFragment
                            )
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to add data: ${e.message}",
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
    }
}
