package com.example.carebout.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R

class MyViewPagerAdapter(context: HomeActivity, profileList: ArrayList<String>): RecyclerView.Adapter<MyViewPagerAdapter.PagerViewHolder>() {
    var item = profileList
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        for(i in item) {
            holder.pet.setImageURI(ImageUtil().get(context, i))
        }

//        holder.pet.setImageResource(item[position])
        //holder.pet.setImageURI(ImageUtil().get())
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.pet_list, parent, false)){

        val pet = itemView.findViewById<ImageView>(R.id.petImage)!!
    }
}