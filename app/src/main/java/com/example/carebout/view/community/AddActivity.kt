package com.example.carebout.view.community

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.carebout.R
import com.example.carebout.databinding.ActivityAddBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.view.View
import android.view.Window
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.Date
import android.widget.TextView
import com.example.carebout.databinding.CustomDialogDeleteBinding
import com.example.carebout.databinding.CustomDialogLayoutBinding

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

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)

        binding.bottomIcon.setOnClickListener {
            openGallery()
        }

        if (intent.getBooleanExtra("isEdit", false)) {
            val existingContent = intent.getStringExtra("existingContent")
            val existingImageUri = intent.getParcelableExtra<Uri>("existingImageUri")

            binding.addEditView.setText(existingContent)

            existingImageUri?.let {
                Glide.with(this)
                    .asBitmap()
                    .load(it)
                    .apply(RequestOptions().fitCenter())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.userImageView.setImageBitmap(resource)
                            binding.userImageView.visibility = View.VISIBLE
                            selectedImageUri = it
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }

            selectedDate = intent.getStringExtra("existingDate")
            selectDay = intent.getStringExtra("existingDay")

            binding.date.text = selectedDate
            binding.day.text = selectDay
        }

        if (selectedDate == null) {
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

            binding.dateClick.setOnClickListener {
                if (!intent.getBooleanExtra("isEdit", false)) {
                    showDatePickerDialog()
                }
            }
        }
    }

    override fun onCreateOptionsMenu (menu: Menu?): Boolean {
        menuInflater.inflate (R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        showCustomDialog()
    }

    override fun onOptionsItemSelected (item: MenuItem): Boolean = when (item.itemId) {

        android.R.id.home -> { // 뒤로가기 버튼을 누를 때
            showCustomDialog()
            true
        }

        R.id.menu_add_save -> {
            val inputData = binding.addEditView.text.toString().trim()

            if (selectedDate == null) {
                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                selectedDate = dateFormat.format(currentDate)
                selectDay = getDayOfWeek(currentDate)
            }

            val db = DBHelper(this).writableDatabase

            if (intent.getBooleanExtra("isEdit", false)) {
                val contentValues = ContentValues().apply {
                    put("content", inputData)
                    put("image_uri", selectedImageUri?.toString() ?: "")
                }

                setResult(Activity.RESULT_OK, intent)
                finish()

                val rowsAffected = db.update(
                    "TODO_TB",
                    contentValues,
                    "date = ?",
                    arrayOf(selectedDate)
                )

                if (rowsAffected > 0) {
                    showCustomToast("일기가 성공적으로 수정되었습니다.")
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                } else {
                    showCustomToast("일기 수정에 실패했습니다.")
                }
            } else {
                val existingEntryCount = DatabaseUtils.queryNumEntries(
                    db,
                    "TODO_TB",
                    "date = ?",
                    arrayOf(selectedDate)
                )

                if (existingEntryCount > 0) {
                    showCustomToast("이미 같은 날짜의 일기가 있습니다.")
                } else if (inputData.isEmpty()) {
                    showCustomToast("내용을 입력해주세요.")
                } else {
                    val intent = Intent().apply {
                        putExtra("result", inputData)
                        putExtra("imageUri", selectedImageUri)
                        putExtra("selectedDate", selectedDate)
                        putExtra("selectedDay", selectDay)
                    }

                    setResult(Activity.RESULT_OK, intent)
                    finish()

                    showCustomToast("일기가 성공적으로 저장되었습니다.")
                }
            }

            db.close()
            true
        }

        else -> true
    }

    private fun showCustomDialog() {
        val dialogBinding = CustomDialogLayoutBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.dialogTitle.text = "일기 쓰기를 그만두시겠습니까?"
        dialogBinding.dialogMessage.text = "작성한 내용은 저장되지 않아요!"

        dialogBinding.btnExit.setOnClickListener {
            dialog.dismiss()
            super.onBackPressed()
        }

        dialogBinding.btnContinue.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private var currentToast: Toast? = null
    private fun showCustomToast(message: String) {
        currentToast?.cancel()

        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout))

        val text = layout.findViewById<TextView>(R.id.custom_toast_text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout

        val toastDurationInMilliSeconds: Long = 3000
        toast.duration =
            if (toastDurationInMilliSeconds > Toast.LENGTH_LONG) Toast.LENGTH_LONG else Toast.LENGTH_SHORT

        toast.setGravity(Gravity.BOTTOM, 0, 200)

        currentToast = toast

        toast.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        requestGalleryLauncher.launch(galleryIntent)
    }

    private fun getDayOfWeek(date: Date): String {
        val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
        val calendar = Calendar.getInstance().apply { time = date }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return koreanDays[dayOfWeek - 1]
    }
}