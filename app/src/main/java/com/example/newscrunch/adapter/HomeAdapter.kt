package com.example.newscrunch.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.newscrunch.R
import com.example.newscrunch.model.News
import com.squareup.picasso.Picasso

//adapter class for the news recycler view
class HomeAdapter(private val context: Context, private val itemList: ArrayList<News>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    //    view holder for the recycler view
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //    initializing the individual fields in the recycler using id's
        val tvSource : TextView = view.findViewById(R.id.tvSource)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val imgImage: ImageView = view.findViewById(R.id.imgImage)

        //    liContent represents a single card / news and can be clicked to navigate to the browser
        val liContent: RelativeLayout = view.findViewById(R.id.liContent)
    }

    //    inflates the view that the viewholder will hold
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.home_recycler_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val news = itemList[position]

//        setting the content for individual fields
        holder.tvSource.text = news.source
        holder.tvTitle.text = news.title
        if (news.author != "null")
            holder.tvAuthor.text = "Author: ${news.author}"

//        handling click on a news to open browser
        holder.liContent.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
            val browserChooserIntent =
                Intent.createChooser(browserIntent, "Choose browser of your choice")
            startActivity(context, browserChooserIntent, null)
        }

//        loads the image using the urlToImage
        Picasso.get().load(news.image).error(R.drawable.ic_error)
            .into(holder.imgImage)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}