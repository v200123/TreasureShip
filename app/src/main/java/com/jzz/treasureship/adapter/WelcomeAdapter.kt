package com.jzz.treasureship.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jzz.treasureship.R



class WelcomeAdapter(list: List<View>) : RecyclerView.Adapter<WelcomeAdapter.ViewPagerViewHolder>() {

    private val drawables = list

    class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView: ImageView = itemView.findViewById(R.id.iv_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_welcome_vp,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = drawables.size



    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        Glide.with(holder.imgView.context).load(drawables[position]).into(holder.imgView)
    }
}