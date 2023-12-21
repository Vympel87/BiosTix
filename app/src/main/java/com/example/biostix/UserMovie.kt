package com.example.biostix

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biostix.databinding.FragmentUserMovieBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class UserMovie : Fragment(), UserMovieAdapter.OnItemClickListener {

    private lateinit var binding: FragmentUserMovieBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: UserMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.listMovie
        movieAdapter = UserMovieAdapter(ArrayList(), this)
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        retrieveData()
    }

    override fun onItemClick(documentSnapshot: DocumentSnapshot) {
        val title = documentSnapshot.getString("title") ?: ""
        val genresList  = documentSnapshot.get("genres") as? ArrayList<String> ?: ArrayList()
        val genres = genresList.joinToString(", ")
        val duration = documentSnapshot.getString("duration") ?: ""
        val image = documentSnapshot.getString("image") ?: ""
        val description = documentSnapshot.getString("desc") ?: ""

        val intent = Intent(requireContext(), UserDetailMovie::class.java)
        intent.putExtra("title", title)
        intent.putStringArrayListExtra("genres", genresList)
        intent.putExtra("duration", duration)
        intent.putExtra("image", image)
        intent.putExtra("description", description)
        startActivity(intent)
    }

    private fun retrieveData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("movie")
            .get()
            .addOnSuccessListener { documents ->
                val movieSnapshots = ArrayList(documents.documents)
                movieAdapter.setData(movieSnapshots)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch data: $exception", Toast.LENGTH_SHORT).show()
            }
    }
}
