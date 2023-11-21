package com.example.carebout.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.home.db.Weight


class WeightRecyclerAdapter(
    private val context: Context,
    private val dataSet: MutableList<Weight>,
    private val listener: (Weight) -> Unit) :
    RecyclerView.Adapter<WeightRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.weight_text_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bindData(dataSet[position])

        holder.itemView.setOnClickListener {
            listener(dataSet[position])
        }
    }

    override fun getItemCount() = dataSet.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wText = view.findViewById<TextView>(R.id.wText)
        val dText = view.findViewById<TextView>(R.id.dText)

        fun bindData(weight: Weight) {
            wText.text = weight.weight.toString()
            dText.text = weight.date
        }
    }

    fun addItem(item: Weight){
        dataSet.add(item)
        dataSet.sortBy { it.date }
        notifyDataSetChanged()
    }
    fun removeItem(item: Weight){
        val index = dataSet.indexOf(item)
        dataSet.removeAt(index)
        notifyItemRemoved(index)
    }
}