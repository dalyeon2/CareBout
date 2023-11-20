package com.example.carebout.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R

class RecyclerAdapter(private val dataSet: ArrayList<Pair<String, String>>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headText = view.findViewById<TextView>(R.id.inspection)
        val dateText = view.findViewById<TextView>(R.id.date)

        fun bindData(item: Pair<String, String>) {
            headText.text = item.first
            dateText.text = item.second
        }
    }

    fun addItem(item: Pair<String, String>){
        dataSet.add(item)
        notifyItemInserted(dataSet.size-1)
    }
    fun removeItem(position: Int){
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }
}