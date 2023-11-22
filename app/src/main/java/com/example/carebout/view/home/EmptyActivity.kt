package com.example.carebout.view.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.viewpager2.widget.ViewPager2
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityEmptyBinding
import com.example.carebout.view.medical.db.AppDatabase

class EmptyActivity : AppCompatActivity() {

    lateinit var binding: ActivityEmptyBinding
    lateinit var db: AppDatabase
    private var addedPet = false

    private lateinit var viewPager: ViewPager2
    private var currentPage = 0
    private val DELAY_MS: Long = 2500 // 2.5초 간격으로 스와이프
    private val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            viewPager.currentItem = (currentPage + 1) % (viewPager.adapter?.itemCount ?: 1)
//            if (currentPage < viewPager.childCount - 1) {
//                viewPager.currentItem = currentPage + 1
//            } else {
//                viewPager.currentItem = 0
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        viewPager = binding.sampleViewPager

        // 현재 클릭 중인 탭 tint
        binding.bottomTapBarOuter.homeImage.imageTintList = ColorStateList.valueOf(Color.parseColor("#6EC677"))
        binding.bottomTapBarOuter.homeText.setTextColor(Color.parseColor("#6EC677"))

        // 하단 탭바 클릭시 이동
        bottomTabClick(binding.bottomTapBarOuter, this)

        // 반려동물 등록 페이지로
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
            startAutoSwipe()
        }

    }

    private fun setViewPager() {
        val sampleList = arrayListOf<String>("sample1", "sample2")

        viewPager.offscreenPageLimit = 1 // 앞뒤로 1개씩 미리 로드해놓기
        viewPager.adapter = MyViewPagerAdapter(this, sampleList)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL  // 가로로 페이지 증가
        viewPager.setPageTransformer(ZoomOutPageTransformer())   // 다음과 같은 애니메이션 효과 적용
        binding.sampleIndicator.setViewPager2(binding.sampleViewPager)    // 인디케이터와 뷰페이저 연결
    }

    private fun startAutoSwipe() {
        handler.postDelayed(runnable, DELAY_MS)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // 새로운 페이지가 선택될 때 호출되는 메서드
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                // 페이지 스크롤 상태가 변경될 때 호출되는 메서드
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    // 스크롤이 멈추면 다시 자동 스와이프 시작
                    handler.postDelayed(runnable, DELAY_MS)
                } else {
                    // 스크롤 중이면 자동 스와이프 중단
                    handler.removeCallbacks(runnable)
                }
            }
        })
    }

}