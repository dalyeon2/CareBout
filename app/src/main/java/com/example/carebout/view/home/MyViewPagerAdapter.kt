package com.example.carebout.view.home

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R

class MyViewPagerAdapter(private val context: Context, private val profileList: MutableList<String>)
    : RecyclerView.Adapter<MyViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(profileList[position])
    }

    override fun getItemCount(): Int = profileList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pet = itemView.findViewById<ImageView>(R.id.petImage)

        fun bindData(item: String) {
            if (item == "cat")
                pet.setImageResource(R.drawable.temp_cat)
            else if(item == "dog")
                pet.setImageResource(R.drawable.temp_dog)
            else if(item == "sample1")
                pet.setImageResource(R.drawable.sample1)
            else if(item == "sample2")
                pet.setImageResource(R.drawable.sample2)
            else
                pet.setImageURI(ImageUtil().get(context, item))
        }
    }

    fun addItem(item: String) {
        profileList.add(item)
        notifyItemInserted(profileList.size-1)
    }

    fun removeItem(position: Int){
        profileList.removeAt(position)
        notifyItemRemoved(position)
    }
}