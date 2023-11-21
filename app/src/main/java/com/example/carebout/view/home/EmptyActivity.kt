package com.example.carebout.view.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityEmptyBinding
import com.example.carebout.view.medical.db.AppDatabase

class EmptyActivity : AppCompatActivity() {

    lateinit var binding: ActivityEmptyBinding
    lateinit var db: AppDatabase
    var addedPet = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!

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

    override fun onResume() {
        super.onResume()

        if (db.personalInfoDao().getAllInfo().isNotEmpty()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }else {
            setViewPager()
        }

    }

    private fun setViewPager() {
        val sampleList = arrayListOf<String>("cat", "dog")

        binding.sampleViewPager.offscreenPageLimit = 1 // 앞뒤로 1개씩 미리 로드해놓기
        binding.sampleViewPager.adapter = MyViewPagerAdapter(this, sampleList)
        binding.sampleViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL  // 가로로 페이지 증가
        binding.sampleViewPager.setPageTransformer(ZoomOutPageTransformer())   // 다음과 같은 애니메이션 효과 적용
        binding.sampleIndicator.setViewPager2(binding.sampleViewPager)    // 인디케이터와 뷰페이저 연결
    }

}