package com.example.carebout.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.db.AppDatabase

class MyViewPagerAdapter(context: HomeActivity, profileList: ArrayList<String>, pid: Int): RecyclerView.Adapter<MyViewPagerAdapter.PagerViewHolder>() {
    val fileNames = profileList
    val context = context
    val pid = pid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = fileNames.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val animal = AppDatabase.getInstance(context)!!.personalInfoDao().getInfoById(pid)?.animal

        if (fileNames[position] == "") {
            if (animal == "cat")
                holder.pet.setImageResource(R.drawable.temp_cat)
            else
                holder.pet.setImageResource(R.drawable.temp_dog)
        } else
            holder.pet.setImageURI(ImageUtil().get(context, fileNames[position]))
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.pet_list, parent, false)){
        val pet = itemView.findViewById<ImageView>(R.id.petImage)!!
    }
}