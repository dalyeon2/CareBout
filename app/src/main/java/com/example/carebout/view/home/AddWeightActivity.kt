package com.example.carebout.view.home

import MyAdapter
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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






        recyclerView = findViewById(R.id.weightRecycler)
        wAdapter = MyAdapter(this,weight.getWeightById(nowPid).toMutableList()){
            //getWeightDataSet(nowPid)){

            //val builder: AlertDialog.Builder = Builder(this)
            AlertDialog
                .Builder(this)
                .setMessage("삭제")
                .setPositiveButton("예"){ _, _ ->
                    weight.deleteInfo(it)
//                    wAdapter.removeItem()
                }
                .create()
                .show()
        }

        recyclerView.layoutManager = LinearLayoutManager(
            this@AddWeightActivity, RecyclerView.VERTICAL, true).apply {
                stackFromEnd = true
        }
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
                        binding.editD.setText(String.format("%04d-%02d-%02d", year, month + 1, day)
                        )
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

            val weightId = weight.insertInfo(Weight(
                nowPid,
                w.text.toString().toFloat(),
                d.text.toString()
            ))

            wAdapter.addItem(Weight(
                nowPid,
                w.text.toString().toFloat(),
                d.text.toString()
            ).also { it.weightId = weightId.toInt() }
            )

            w.setText("")
            d.setText("")
        }
    }

    private fun getWeightDataSet(pid: Int): MutableList<Pair<Float, String>> {
        val weightDS = mutableListOf<Pair<Float,String>>()

        for (w in weight.getWeightById(pid)) {
           weightDS.add(Pair(w.weight, w.date))
        }

        weightDS.sortBy { it.second }

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