package com.example.carebout.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.carebout.R
import com.example.carebout.databinding.PetListBinding

class MyViewPagerAdapter(profileList: ArrayList<Int>): RecyclerView.Adapter<MyViewPagerAdapter.PagerViewHolder>() {
    var item = profileList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.pet.setImageResource(item[position])
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.pet_list, parent, false)){

        val pet = itemView.findViewById<ImageView>(R.id.petImage)!!
    }
}