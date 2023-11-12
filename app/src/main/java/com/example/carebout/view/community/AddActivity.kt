package com.example.carebout.view.community

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carebout.R
import com.example.carebout.databinding.ActivityAddBinding
import com.example.carebout.view.community.CommunityActivity
import com.example.carebout.view.community.DBHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddBinding

    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newData = result.data?.getStringExtra("result")
            val selectedImageUri = result.data?.data

            // 데이터와 이미지 URI를 CommunityActivity로 전달
            val intent = Intent()
                .putExtra("result", newData)
                .putExtra("imageUri", selectedImageUri)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private val requestGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 갤러리에서 선택한 이미지 처리
                val selectedImage = result.data?.data
                // TODO: 선택한 이미지를 사용하여 원하는 작업 수행
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 현재 날짜 표기
        val currentDate = Calendar.getInstance().time
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()).apply {
            val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
            applyPattern("${koreanDays[Calendar.DAY_OF_WEEK - 1]}")
        }
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        val dayOfWeek = dayFormat.format(currentDate)
        val formattedDate = dateFormat.format(currentDate)

        binding.date.text = formattedDate
        binding.day.text = dayOfWeek

        /*
        // 데이터 전송
        val userEnteredText = binding.addEditView.text.toString()
        val fragment = OneFragment()
        val args = Bundle()
        args.putString("userEnteredText", userEnteredText) // 사용자가 입력한 텍스트를 Bundle에 추가
        fragment.arguments = args
         */
    }

    override fun onCreateOptionsMenu (menu: Menu?): Boolean {
        menuInflater.inflate (R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected (item: MenuItem): Boolean = when (item.itemId) {

        android.R.id.home -> { // 뒤로가기 버튼을 누를 때
            finish()
            true
        }

        R.id.menu_add_save -> {
            /*
            val inputData = binding.addEditView.text.toString()
            val db = DBHelper (this).writableDatabase
            db.execSQL ("insert into TODO_TB (todo) values (?)",
                arrayOf<String>(inputData))
            db.close()
             */

            val intent = intent.putExtra("result", binding.addEditView.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }
        else -> true
    }
}