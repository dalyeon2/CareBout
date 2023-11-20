package com.example.carebout.view.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.db.AppDatabase

class MyViewPagerAdapter(context: HomeActivity, profileList: ArrayList<String>): RecyclerView.Adapter<MyViewPagerAdapter.PagerViewHolder>() {
    val fileNames = profileList
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = fileNames.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
//        val animal = AppDatabase.getInstance(context)!!.personalInfoDao().getInfoById()?.animal

        if (fileNames[position] == "cat") {
//            if (animal == "cat")
            holder.pet.setImageResource(R.drawable.temp_cat)
        }else if(fileNames[position] == "dog"){
                holder.pet.setImageResource(R.drawable.temp_dog)
        } else
            holder.pet.setImageURI(ImageUtil().get(context, fileNames[position]))
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.pet_list, parent, false)){
        val pet = itemView.findViewById<ImageView>(R.id.petImage)!!

        fun bindData() {

        }
    }
}