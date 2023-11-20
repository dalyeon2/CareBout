package com.example.carebout.view.medical.Inoc

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.db.Inoculation

class InoculationAdapter(private val context: Context)
    : RecyclerView.Adapter<InoculationAdapter.MyViewHolder>() {

    //초기화
    private var inocList: ArrayList<Inoculation> = ArrayList<Inoculation>()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val tag1: ToggleButton = itemView.findViewById(R.id.toggle1)
//        val tag2: ToggleButton = itemView.findViewById(R.id.toggle2)

        val tagText: TextView = itemView.findViewById(R.id.tag_text)
        val dateText: TextView = itemView.findViewById(R.id.date_text)
        val dueText: TextView = itemView.findViewById(R.id.due_text)
        val hospitalText: TextView = itemView.findViewById(R.id.hospital_text)
        val etcText: TextView = itemView.findViewById(R.id.etc_text)

    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.inoc_recyclerview, parent, false)

        return MyViewHolder(view)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val inocData = inocList[position]

        //데이터 변수에 담기
        var uDate = inocList[holder.bindingAdapterPosition].date
        var uDue = inocList[holder.bindingAdapterPosition].due
        var uHospital = inocList[holder.bindingAdapterPosition].hospital
        var uEtc = inocList[holder.bindingAdapterPosition].etc

        var uTagDHPPL = inocList[holder.bindingAdapterPosition].tag_DHPPL
        var uTagC = inocList[holder.bindingAdapterPosition].tag_Corona
        var uTagKC = inocList[holder.bindingAdapterPosition].tag_KC
        var uTagCVRP = inocList[holder.bindingAdapterPosition].tag_CVRP
        var uTagFL = inocList[holder.bindingAdapterPosition].tag_FL
        var uTagFID = inocList[holder.bindingAdapterPosition].tag_FID
        var uTagR = inocList[holder.bindingAdapterPosition].tag_Rabies
        var uTagH = inocList[holder.bindingAdapterPosition].tag_Heartworm

        //데이터 적용
        holder.dateText.text = uDate
        holder.dueText.text = uDue
        holder.hospitalText.text = uHospital
        holder.etcText.text = uEtc

        if (uTagDHPPL == true) {
            holder.tagText.text = "DHPPL"
        } else if (uTagC == true) {
            holder.tagText.text = "코로나"
        } else if (uTagKC == true) {
            holder.tagText.text = "켄넬코프"
        } else if (uTagCVRP == true) {
            holder.tagText.text = "CVRP"
        } else if (uTagFL == true) {
            holder.tagText.text = "백혈병"
        } else if (uTagFID == true) {
            holder.tagText.text = "복막염"
        } else if (uTagR == true) {
            holder.tagText.text = "광견병"
        } else if (uTagH == true) {
            holder.tagText.text = "심장사상충"
        }

        holder.itemView.setOnClickListener {

            Log.i("size", inocList.size.toString())

            var intent: Intent = Intent(context, InoculationUpdateActivity::class.java)

            intent.putExtra("inocId", inocList[holder.bindingAdapterPosition].inocId) // 아이템의 고유 ID 전달
            Log.i("id", inocList[holder.bindingAdapterPosition].inocId.toString())
            //값 담기
            intent.putExtra("uDate", uDate)
            intent.putExtra("uDue", uDue)
            intent.putExtra("uHospital", uHospital)
            intent.putExtra("uEtc", uEtc)

            intent.putExtra("uTagDHPPL", uTagDHPPL)
            intent.putExtra("uTagC", uTagC)
            intent.putExtra("uTagKC", uTagKC)
            intent.putExtra("uTagCVRP", uTagCVRP)
            intent.putExtra("uTagFL", uTagFL)
            intent.putExtra("uTagFID", uTagFID)
            intent.putExtra("uTagR", uTagR)
            intent.putExtra("uTagH", uTagH)

//            Log.i("adpter in tag1", "${uTagDHPPL} ${uTagC} ${uTagKC} ${uTagCVRP.toString()}")
//            Log.i("adpter in tag2", "${uTagFL} ${uTagFID} ${uTagR} ${uTagH.toString()}")

            context.startActivity(intent)
        }

    }

    //아이템 갯수
    override fun getItemCount(): Int {
        return inocList.size
    }

    //아이템 등록
    fun setInoculationList(inocList: ArrayList<Inoculation>){
        this.inocList = inocList
        //데이터 재설정
        notifyDataSetChanged()
    }

    fun deleteInoculationList(position: Int) {
        this.inocList.removeAt(position)
    }



}