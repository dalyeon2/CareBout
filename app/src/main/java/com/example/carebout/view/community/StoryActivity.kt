package com.example.carebout.view.community

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import com.example.carebout.R
import com.example.carebout.databinding.ActivityStoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.carebout.databinding.CustomDialogDeleteBinding

class StoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityStoryBinding
    var contents: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow)

        val receivedData = intent.getStringExtra("result")
        val receivedImageUri = intent.getParcelableExtra<Uri>("imageUri")
        val receivedDate = intent.getStringExtra("selectedDate")
        val receivedDay = intent.getStringExtra("selectedDay")

        binding.date.text = receivedDate
        binding.day.text = receivedDay

        if (receivedImageUri != null) {
            loadWithGlide(receivedImageUri)
        } else {
            binding.userImage.visibility = View.GONE
        }

        binding.userStory.text = receivedData

    }
    // 옵션 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_story, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected (item: MenuItem): Boolean = when (item.itemId) {

        android.R.id.home -> { // 뒤로가기 버튼을 누를 때
            finish()
            true
        }

        R.id.menu_edit -> {
            val editIntent = Intent(this, AddActivity::class.java).apply {
                putExtra("isEdit", true)
                putExtra("position", intent.getIntExtra("position", -1))
                putExtra("existingContent", intent.getStringExtra("result"))
                putExtra("existingImageUri", intent.getParcelableExtra<Uri>("imageUri"))
                putExtra("existingDate", intent.getStringExtra("selectedDate"))
                putExtra("existingDay", intent.getStringExtra("selectedDay"))
            }
            startActivity(editIntent)
            finish()
            true
        }

        R.id.menu_remove -> {
            showDeleteCustomDialog()
            true
        }

        else -> true
    }

    private fun showDeleteCustomDialog() {
        val dialogBinding = CustomDialogDeleteBinding.inflate(layoutInflater)

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.dialogTitle.text = "일기를 정말 삭제하시겠습니까?"
        dialogBinding.dialogMessage.text = "삭제한 글은 복원할 수 없어요!"

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnDelete.setOnClickListener {
            val position = intent.getIntExtra("position", -1)

            if (position != -1) {
                dialog.dismiss()

                val resultIntent = Intent().apply {
                    putExtra("positionToRemove", position)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        dialog.show()
    }

    private fun loadWithGlide(imageUri: Uri) {
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .apply(RequestOptions().fitCenter())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.userImage.setImageBitmap(resource)
                    binding.userImage.visibility = View.VISIBLE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}