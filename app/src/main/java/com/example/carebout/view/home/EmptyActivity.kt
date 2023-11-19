package com.example.carebout.view.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityEmptyBinding
import com.example.carebout.view.medical.db.AppDatabase

class EmptyActivity : AppCompatActivity() {

    companion object {
        var emptyActivity: EmptyActivity? = null
    }

    lateinit var binding: ActivityEmptyBinding
    lateinit var db: AppDatabase
    var addedPet = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        emptyActivity = this

        // 현재 클릭 중인 탭 tint
        binding.bottomTapBarOuter.homeImage.imageTintList = ColorStateList.valueOf(Color.parseColor("#6EC677"))
        binding.bottomTapBarOuter.homeText.setTextColor(Color.parseColor("#6EC677"))

        // 하단 탭바 클릭시 이동
        bottomTabClick(binding.bottomTapBarOuter, this)


        binding.goToAddPetBtn.setOnClickListener {
            val intent = Intent(this, AddPetActivity::class.java)
            intent.putExtra("addedPet", addedPet)
            startActivity(intent)
        }
    }

}