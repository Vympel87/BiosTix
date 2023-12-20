package com.example.biostix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biostix.databinding.FragmentUserMovieBinding
import com.google.firebase.firestore.FirebaseFirestore

class UserMovie : Fragment() {

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
        movieAdapter = UserMovieAdapter(ArrayList())
        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Ubah jumlah kolom di sini

        retrieveData()
    }

    private fun retrieveData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("movie")
            .get()
            .addOnSuccessListener { documents ->
                val movieSnapshots = ArrayList(documents.documents)
                movieAdapter = UserMovieAdapter(movieSnapshots)
                recyclerView.adapter = movieAdapter
                movieAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch data: $exception", Toast.LENGTH_SHORT).show()
            }
    }
}
