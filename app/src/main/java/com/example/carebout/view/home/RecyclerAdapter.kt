package com.example.carebout.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R

class RecyclerAdapter(private val dataSet: ArrayList<Pair<String, String>>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headText: TextView
        val dateText: TextView

        init {
            headText = view.findViewById(R.id.inspection)
            dateText = view.findViewById(R.id.date)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val data: Pair<String, String> = dataSet[position]
        viewHolder.headText.text = data.first
        viewHolder.dateText.text = data.second
    }

    override fun getItemCount() = dataSet.size


    public fun addItem(a: Pair<String, String>){
        dataSet.add(a)
        notifyItemInserted(dataSet.size-1)
    }
    public fun removeItem(index: Int){
        dataSet.removeAt(index)
        notifyItemRemoved(index)
        //notifyDataSetChanged()
    }
}