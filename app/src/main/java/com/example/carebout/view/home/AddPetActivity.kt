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
        setContentView(binding.root)

        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }
    }
}