package com.example.carebout.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carebout.databinding.ActivityAddWeightBinding
import com.example.carebout.view.home.db.PersonalInfoDB
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.home.db.WeightDao

class AddWeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWeightBinding

    private lateinit var weight: WeightDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weight = PersonalInfoDB.getInstance(this)!!.weightDao()

        binding.topBarOuter.activityTitle.text = "체중 기록"

        // 뒤로가기 버튼
        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }

        // 등록 버튼
        binding.topBarOuter.CompleteBtn.setOnClickListener {
            val intt = intent
            val pid = intt.getIntExtra("pid", 0)

            weight.insertInfo(Weight(
                pid,
                3.5f,
                "2023-12-12"
            ))

            var home = HomeActivity.homeActivity

            home?.finish()
            home?.startActivity(Intent(this@AddWeightActivity, HomeActivity::class.java))
            finish()
        }
    }
}