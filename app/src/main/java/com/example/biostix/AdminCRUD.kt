package com.example.biostix

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biostix.databinding.FragmentAdminCRUDBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminCRUD.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminCRUD : Fragment() {

    private lateinit var binding: FragmentAdminCRUDBinding
    private lateinit var movieAdapter: AdminMovieAdapter
    private val movieList = ArrayList<MovieData>()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdminCRUDBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.addBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CreateActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        val db = FirebaseFirestore.getInstance()

        db.collection("movie")
            .get()
            .addOnSuccessListener { documents ->
                val movieSnapshots = ArrayList<DocumentSnapshot>()
                for (document in documents) {
                    movieSnapshots.add(document)
                }

                movieAdapter = AdminMovieAdapter(movieSnapshots)
                binding.listMovie.layoutManager = LinearLayoutManager(requireContext())
                binding.listMovie.adapter = movieAdapter
                movieAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch data: $exception", Toast.LENGTH_SHORT).show()
            }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminCRUD.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminCRUD().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}