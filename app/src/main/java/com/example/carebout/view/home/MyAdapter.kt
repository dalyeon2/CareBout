import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.home.db.Weight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyAdapter(private val context: Context, private val dataList: MutableList<Weight>, private val listener: (Weight)->Unit) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(dataList[position])
        holder.itemView.setOnLongClickListener {
            listener(dataList[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wTitle: TextView = itemView.findViewById(R.id.wTitle)
        private val wItem: EditText = itemView.findViewById(R.id.wItem)
        private val dTitle: TextView = itemView.findViewById(R.id.dTitle)
        private val dItem: EditText = itemView.findViewById(R.id.dItem)

        fun bindData(item: Weight) {
            wTitle.text = "몸무게"
            dTitle.text = "측정일"
            wItem.setText(item.weight.toString())
            dItem.setText(item.date)
        }

    }
    fun addItem(item: Weight) {
        dataList.add(item)
        notifyItemInserted(dataList.size-1)
    }

    fun removeItem(item: Weight){
        val index =dataList.indexOf(item)
        dataList.removeAt(index)
        notifyItemRemoved(index)
    }
}