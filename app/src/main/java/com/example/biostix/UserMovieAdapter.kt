package com.example.biostix

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot

class UserMovieAdapter(
    private val movieList: ArrayList<DocumentSnapshot>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UserMovieAdapter.MovieViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot)
    }

    fun setData(newList: List<DocumentSnapshot>) {
        movieList.clear()
        movieList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_guest, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = movieList[position]

        val title = currentItem.getString("title") ?: ""
        val image = currentItem.getString("image") ?: ""

        holder.movieTitle.text = title

        Glide.with(holder.itemView.context)
            .load(image)
            .into(holder.movieImage)

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieTitle: TextView = itemView.findViewById(R.id.movie_title)
        val movieImage: ImageView = itemView.findViewById(R.id.movie_image)
    }
}

