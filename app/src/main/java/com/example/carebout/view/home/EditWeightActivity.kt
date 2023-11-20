package com.example.carebout.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carebout.R
import com.example.carebout.databinding.ActivityEditWeightBinding

class EditWeightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditWeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}