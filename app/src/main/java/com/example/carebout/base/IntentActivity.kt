package com.example.carebout.base

import android.app.Activity
import android.content.Intent
import com.example.carebout.databinding.BottomTapBinding
import com.example.carebout.view.calendar.CalendarActivity
import com.example.carebout.view.community.CommunityActivity
import com.example.carebout.view.home.HomeActivity
import com.example.carebout.view.medical.MedicalActivity

fun bottomTabClick(btmBinding: BottomTapBinding, nowActivity: Activity){
    btmBinding.goToHome.setOnClickListener {
        val intent = Intent(nowActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        nowActivity.startActivity(intent)
        nowActivity.overridePendingTransition(0,0)
        nowActivity.finish()
    }
    btmBinding.goToCalendar.setOnClickListener {
        val intent = Intent(nowActivity, CalendarActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        nowActivity.startActivity(intent)
        nowActivity.overridePendingTransition(0,0)
        nowActivity.finish()
    }
    btmBinding.goToCommunity.setOnClickListener {
        val intent = Intent(nowActivity, CommunityActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        nowActivity.startActivity(intent)
        nowActivity.overridePendingTransition(0,0)
        nowActivity.finish()
    }
    btmBinding.goToMedical.setOnClickListener {
        val intent = Intent(nowActivity, MedicalActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        nowActivity.startActivity(intent)
        nowActivity.overridePendingTransition(0,0)
        nowActivity.finish()
    }
}