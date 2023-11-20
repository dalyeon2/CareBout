package com.example.carebout.view.medical.Clinic

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.db.Clinic

class ClinicAdapter2(private val context: Context)
    : RecyclerView.Adapter<ClinicAdapter2.MyViewHolder>() {

    //초기화
    private var clinicList: ArrayList<Clinic> = ArrayList<Clinic>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tagText: TextView = itemView.findViewById(R.id.tag_text)
        val dateText: TextView = itemView.findViewById(R.id.date_text)
        val hospitalText: TextView = itemView.findViewById(R.id.hospital_text)
//        val etcText: TextView = itemView.findViewById(R.id.etc_text)

    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.clinic_recyclerview2, parent, false)

        return MyViewHolder(view)
    }

    //데이터 설정
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val clinicData = clinicList[position]

        //데이터 변수에 담기
        var uDate = clinicList[holder.bindingAdapterPosition].date
        var uHospital = clinicList[holder.bindingAdapterPosition].hospital
        var uEtc = clinicList[holder.bindingAdapterPosition].etc

        var uTagB = clinicList[holder.bindingAdapterPosition].tag_blood //혈액
        var uTagX = clinicList[holder.bindingAdapterPosition].tag_xray //X-ray
        var uTagU = clinicList[holder.bindingAdapterPosition].tag_ultrasonic //초음파
        var uTagC = clinicList[holder.bindingAdapterPosition].tag_ct //CT
        var uTagM = clinicList[holder.bindingAdapterPosition].tag_mri //MRI
        var uTagCheckup = clinicList[holder.bindingAdapterPosition].tag_checkup //정기

        //데이터 적용
        holder.dateText.text = uDate
        holder.hospitalText.text = uHospital
//        holder.etcText.text = uEtc

        var data = ArrayList<String>()

        if (uTagB == true) {
            data.add("혈액")
        }

        if (uTagX == true) {
            data.add("X-ray")
        }

        if (uTagU == true) {
            data.add("초음파")
        }

        if (uTagC == true) {
            data.add("CT")
        }

        if (uTagM == true) {
            data.add("MRI")
        }

        if (uTagCheckup == true) {
            data.add("정기")
        }

        val tagText = data.joinToString(" ") { "#$it" }
        holder.tagText.text = tagText

        holder.itemView.setOnClickListener {

            Log.i("size", clinicList.size.toString())

            var intent: Intent = Intent(context, ClinicUpdateActivity::class.java)

            intent.putExtra("clinicId", clinicList[holder.bindingAdapterPosition].clinicId) // 아이템의 고유 ID 전달
            Log.i("id", clinicList[holder.bindingAdapterPosition].clinicId.toString())
            //값 담기
            intent.putExtra("uDate", uDate)
            intent.putExtra("uHospital", uHospital)
            intent.putExtra("uEtc", uEtc)

            intent.putExtra("uTagB", uTagB)
            intent.putExtra("uTagX", uTagX)
            intent.putExtra("uTagU", uTagU)
            intent.putExtra("uTagC", uTagC)
            intent.putExtra("uTagM", uTagM)
            intent.putExtra("uTagCheckup", uTagCheckup)

            context.startActivity(intent)
        }

    }

    //아이템 갯수
    override fun getItemCount(): Int {
        return clinicList.size
    }

    //아이템 등록
    fun setClinicList(clinicList: ArrayList<Clinic>){
        this.clinicList = clinicList
        //데이터 재설정
        notifyDataSetChanged()
    }

    fun deleteClinicList(position: Int) {
        this.clinicList.removeAt(position)
    }



}