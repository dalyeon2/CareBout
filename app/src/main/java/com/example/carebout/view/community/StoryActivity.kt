package com.example.carebout.view.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carebout.R
import com.example.carebout.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityStoryBinding
    var contents: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // 데이터 수신
        val receivedData = intent.getStringExtra("data_key")
        // receivedData를 사용하여 데이터 처리
    }

    /*
    override fun onCreateOptionsMenu (menu: Menu?): Boolean {
        menuInflater.inflate (R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

     */

    override fun onOptionsItemSelected (item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> true
    }
}