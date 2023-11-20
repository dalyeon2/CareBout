package com.example.carebout.view.home

import MyAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.databinding.ActivityAddWeightBinding
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.home.db.WeightDao
import com.example.carebout.view.medical.db.AppDatabase

class AddWeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWeightBinding

    private lateinit var weight: WeightDao

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weight = AppDatabase.getInstance(this)!!.weightDao()
        val nowPid = intent.getIntExtra("pid", 0)

        // 상단바 타이틀
        binding.topBarOuter.activityTitle.text = "체중 기록"
        // 상단바 우측 버튼 사용 안함
        binding.topBarOuter.CompleteBtn.visibility = GONE

        val dataList: MutableList<Pair<Float, String>> = getWeightDataSet(nowPid)

        recyclerView = findViewById(R.id.weightRecycler)
        adapter = MyAdapter(this, dataList)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // 뒤로가기 버튼
        binding.topBarOuter.backToActivity.setOnClickListener {

            finish()
        }

        // 추가 버튼
        binding.addMoreBtn.setOnClickListener {
            val w = binding.editW
            val d = binding.editD

            if (!isValid(w, d))
                return@setOnClickListener

            adapter.addItem(Pair(w.text.toString().toFloat(), d.text.toString()))
        }
    }

    private fun getWeightDataSet(pid: Int): MutableList<Pair<Float, String>> {
        val weightDS = mutableListOf<Pair<Float,String>>()

        for (w in weight.getWeightById(pid)) {
           weightDS.add(Pair(w.weight, w.date))
        }

        return weightDS
    }

    private fun saveWeight(wList: ArrayList<Weight>) {
        val intt = intent
        val pid = intt.getIntExtra("pid", 0)

        weight.insertInfo(Weight(
            pid,
            3.5f,
            "2023-12-12"
        ))
    }

    private fun isValid(weight: EditText, date: EditText) : Boolean {
        val w = weight
        val d = date

        if (w.text.isNullOrBlank())
            w.error = ""
        else if (d.text.isNullOrBlank())
            d.error = ""
        else
            return true

        return false
    }
}