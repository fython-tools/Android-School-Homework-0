package me.fython.schoolexam.ui

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView
import com.gjiazhe.scrollparallaximageview.parallaxstyle.VerticalMovingStyle
import com.squareup.picasso.Picasso
import me.fython.schoolexam.R
import me.fython.schoolexam.model.Photo
import me.fython.schoolexam.util.inflate

class ThumbsListAdapter(
        val data: MutableList<Photo> = mutableListOf()
) : RecyclerView.Adapter<ThumbsListAdapter.ThumbViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbViewHolder {
        return ThumbViewHolder(parent.inflate(R.layout.item_layout))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ThumbViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun onViewRecycled(holder: ThumbViewHolder) {
        holder.onViewRecycled()
    }

    class ThumbViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ScrollParallaxImageView = itemView.findViewById(android.R.id.background)
        val like: Button = itemView.findViewById(R.id.like_button)

        private var data: Photo? = null

        init {
            itemView.setOnClickListener {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                        itemView.context as Activity, image, ViewerActivity.TRANSITION_IMAGE)
                val intent = Intent(itemView.context, ViewerActivity::class.java)
                intent.putExtra(ViewerActivity.EXTRA_DATA, data!!)
                itemView.context.startActivity(intent, options.toBundle())
            }

            image.setParallaxStyles(VerticalMovingStyle())
        }

        fun onBind(data: Photo) {
            this.data = data
            image.setBackgroundColor(Color.parseColor(data.color))
            like.text = data.likes.toString()
            Picasso.with(image.context)
                    .load(data.urls.small)
                    .into(image)
        }

        fun onViewRecycled() {
            Picasso.with(image.context).cancelRequest(image)
        }

    }

}