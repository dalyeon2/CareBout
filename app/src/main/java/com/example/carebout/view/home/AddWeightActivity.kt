package com.example.carebout.view.home

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
import com.example.carebout.databinding.CustomDialogBinding
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.home.db.WeightDao
import com.example.carebout.view.medical.db.AppDatabase
import java.util.Calendar

class AddWeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWeightBinding

    private lateinit var weightDao: WeightDao

    private lateinit var dialog: Dialog
    private lateinit var wAdapter: WeightRecyclerAdapter
    private lateinit var wRecycler: RecyclerView

    private var nowPid: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weightDao = AppDatabase.getInstance(this)!!.weightDao()
        nowPid = intent.getIntExtra("pid", 0)

        // 상단바 타이틀
        binding.topBarOuter.activityTitle.text = "체중 기록"
        // 상단바 우측 버튼 사용 안함
        binding.topBarOuter.CompleteBtn.visibility = INVISIBLE

        wRecycler = binding.weightRecycler
        wAdapter = WeightRecyclerAdapter(this, getWeightDataSet().toMutableList() ){
            val cdBinding = CustomDialogBinding.inflate(layoutInflater)
            val deleteW = it

            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(cdBinding.root)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)

            // 아니오 버튼
            cdBinding.btnNo.setOnClickListener {
                dialog.dismiss()
            }
            // 예 버튼
            cdBinding.btnYes.setOnClickListener {
                // 몸무게 삭제
                weightDao.deleteInfo(deleteW)
                wAdapter.removeItem(deleteW)

                dialog.dismiss()
            }

            dialog.show()
        }
        binding.weightRecycler.layoutManager = LinearLayoutManager(
            this@AddWeightActivity, RecyclerView.VERTICAL, true).apply {
                stackFromEnd = true
        }
        binding.weightRecycler.adapter = wAdapter

        // 날짜 입력 editText 클릭 시 캘린더 뜨도록
        binding.editD.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            this@AddWeightActivity.let { it ->
                DatePickerDialog(it, { _, year, month, day ->
                    run {
                        binding.editD.setText(String.format("%04d-%02d-%02d", year, month + 1, day)
                        )
                    }
                }, year, month, day)
            }.show()
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

            val insertW = Weight(
                nowPid,
                w.text.toString().toFloat(),
                d.text.toString()
            )
            val weightId = weightDao.insertInfo(insertW)

            wAdapter.addItem(insertW)

            w.text.clear()
            d.text.clear()
        }
    }

    private fun getWeightDataSet(): MutableList<Weight> {
        val weightDS = mutableListOf<Weight>()

        for (w in weightDao.getWeightById(nowPid)) {
           weightDS.add(w)
        }

        weightDS.sortBy { it.date }

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