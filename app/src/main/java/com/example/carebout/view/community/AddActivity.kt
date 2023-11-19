package com.example.carebout.view.community

import android.app.ActionBar
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
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
import android.view.View
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class AddActivity: AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    private var selectedImageUri: Uri? = null
    private var selectedDate: String? = null
    private var selectDay: String? = null
    private val requestGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    result.data?.data?.let { fileUri ->
                        Glide.with(this)
                            .asBitmap()
                            .load(fileUri)
                            .apply(RequestOptions().fitCenter())
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    binding.userImageView.setImageBitmap(resource)
                                    binding.userImageView.visibility = View.VISIBLE
                                    selectedImageUri = fileUri
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_add)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.bottomIcon.setOnClickListener {
            openGallery()
        }

        // 현재 날짜 표기
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val formattedDay = koreanDays[dayOfWeek - 1]
        val formattedDate = dateFormat.format(currentDate)
        binding.date.text = formattedDate
        binding.day.text = formattedDay

        binding.date.setOnClickListener {
            showDatePickerDialog()
        }
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
            val inputData = binding.addEditView.text.toString()
            val db = DBHelper (this).writableDatabase
            db.execSQL(
                "insert into TODO_TB (content, date, day, image_uri) values (?, ?, ?, ?)",
                arrayOf<String>(
                    inputData,
                    selectedDate ?: "",
                    selectDay ?: "",
                    selectedImageUri?.toString() ?: ""
                )
            )
            db.close()

            val intent = Intent().apply {
                putExtra("result", inputData)
                putExtra("imageUri", selectedImageUri)
                putExtra("selectedDate", selectedDate)
                putExtra("selectedDay", selectDay)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }
        else -> true
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)
                binding.date.text = formattedDate
                selectedDate = formattedDate

                val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
                val formattedDay = koreanDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
                binding.day.text = formattedDay
                selectDay = formattedDay
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 옵션만 설정하고자 true로 지정
        try {
            var inputStream = contentResolver.openInputStream(fileUri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        requestGalleryLauncher.launch(galleryIntent)
    }
}