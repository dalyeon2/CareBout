package com.example.carebout.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import com.example.carebout.R
import com.example.carebout.databinding.ActivityAddPetBinding
import com.example.carebout.databinding.ActivityHomeBinding


class AddPetActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_pet)

//        val toolbar: androidx.appcompat.widget.Toolbar = binding.topBarOuter.topBar
//        setSupportActionBar(toolbar)
        supportActionBar?.title = "반려동물 등록"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}