package com.example.biostix

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MovieAdapter(private val movieList: ArrayList<DocumentSnapshot>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_admin, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = movieList[position]

        val title = currentItem.getString("title") ?: ""
        val genres = currentItem.get("genres") as? List<String> ?: emptyList()
        val duration = currentItem.getString("duration") ?: ""
        val image = currentItem.getString("image") ?: ""

        holder.movieTitle.text = title
        holder.movieGenre.text = genres.joinToString(", ")
        holder.movieDuration.text = duration

        Glide.with(holder.itemView.context)
            .load(image)
            .into(holder.movieImage)

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java).apply {
                putExtra("MOVIE_ID", currentItem.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val itemPosition = holder.adapterPosition
            deleteMovie(currentItem.id, holder.itemView.context, itemPosition)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
        val movieGenre: TextView = itemView.findViewById(R.id.movie_genre)
        val movieDuration: TextView = itemView.findViewById(R.id.movie_duration)
        val movieImage: ImageView = itemView.findViewById(R.id.movie_image)
        val updateButton: Button = itemView.findViewById(R.id.edit_btn)
        val deleteButton: Button = itemView.findViewById(R.id.delete_btn)
    }

    private fun deleteMovie(movieId: String, context: Context, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val movieRef = db.collection("movie").document(movieId)

        movieRef.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Movie deleted successfully!", Toast.LENGTH_SHORT).show()
                movieList.removeAt(position)
                notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error deleting movie: $e", Toast.LENGTH_SHORT).show()
            }
    }
}

