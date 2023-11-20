package com.example.carebout.view.home

import MyAdapter
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.databinding.ActivityAddWeightBinding
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.home.db.WeightDao
import com.example.carebout.view.medical.db.AppDatabase
import java.util.Calendar

class AddWeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWeightBinding

    private lateinit var weight: WeightDao

    private lateinit var recyclerView: RecyclerView
    private lateinit var wAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weight = AppDatabase.getInstance(this)!!.weightDao()
        val nowPid = intent.getIntExtra("pid", 0)

        // 상단바 타이틀
        binding.topBarOuter.activityTitle.text = "체중 기록"
        // 상단바 우측 버튼 사용 안함
        binding.topBarOuter.CompleteBtn.visibility = INVISIBLE


        val dataList: MutableList<Pair<Float, String>> = getWeightDataSet(nowPid)
        dataList.sortBy { it.second }

        recyclerView = findViewById(R.id.weightRecycler)
        wAdapter = MyAdapter(this, dataList)

        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
            reverseLayout = true
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = wAdapter

        // 날짜 입력 editText 클릭 시 캘린더 뜨도록
        binding.editD.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            this@AddWeightActivity.let { it ->
                DatePickerDialog(it, { _, year, month, day ->
                    run {
                        binding.editD.setText(year.toString() + "-" + (month + 1).toString() + "-" + day.toString())
                    }
                }, year, month, day)
            }?.show()
        }

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

            weight.insertInfo(Weight(
                nowPid,
                w.text.toString().toFloat(),
                d.text.toString()
            ))

            wAdapter.addItem(Pair(w.text.toString().toFloat(), d.text.toString()))

            w.setText("")
            d.setText("")
        }
    }

    private fun getWeightDataSet(pid: Int): MutableList<Pair<Float, String>> {
        val weightDS = mutableListOf<Pair<Float,String>>()

        for (w in weight.getWeightById(pid)) {
           weightDS.add(Pair(w.weight, w.date))
        }

        return weightDS
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