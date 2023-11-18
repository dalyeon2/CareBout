package com.example.carebout.view.community

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.google.android.material.tabs.TabLayoutMediator
import com.example.carebout.databinding.ActivityCommunityBinding
import com.example.carebout.view.IntroActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CommunityActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommunityBinding
    lateinit var adapter: MyAdapter
    var contents: MutableList<String>? = null
    var imageUris: MutableList<Uri>? = mutableListOf()
    var selectedDates: MutableList<String?> = mutableListOf()
    var selectedDay: MutableList<String?> = mutableListOf()

    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newData = result.data?.getStringExtra("result")
            val selectedImageUri = result.data?.getParcelableExtra<Uri>("imageUri")
            val selectedDateResult = result.data?.getStringExtra("selectedDate")
            val selectedDayResult = result.data?.getStringExtra("selectedDay")

            newData?.let {
                contents?.add(0, it)

                val uriToDisplay = selectedImageUri ?: Uri.EMPTY
                imageUris?.add(0, uriToDisplay)

                selectedDates.add(0, selectedDateResult)
                selectedDay.add(0, selectedDayResult)
                // DB에 데이터 추가
                val db = DBHelper(this).writableDatabase
                val contentValues = ContentValues().apply {
                    put("content", it)
                    put("date", selectedDateResult)
                    put("day", selectedDayResult)
                    put("image_uri", uriToDisplay?.toString() ?: "")
                }
                db.insert("TODO_TB", null, contentValues)
                db.close()

                adapter.notifyDataSetChanged()

                // 데이터가 추가되면 RecyclerView를 보이도록 설정
                if (contents?.isNotEmpty() == true) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }
                // 리사이클러뷰 갱신
                adapter.notifyItemInserted(0)
            }
        }
    }

    private val requestRemoveLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val positionToRemove = result.data?.getIntExtra("positionToRemove", -1)

            positionToRemove?.let { position ->
                if (contents?.isNotEmpty() == true && position in 0 until contents!!.size) {
                    // 이미지 파일 삭제
                    val imageUriToRemove = imageUris?.getOrNull(position)
                    imageUriToRemove?.let { uri ->
                        val file = File(uri.path)
                        if (file.exists()) {
                            file.delete()
                        }
                    }

                    // DB에서 데이터 삭제
                    val db = DBHelper(this).writableDatabase
                    val idToRemove = position + 1
                    db.delete("TODO_TB", "_id=?", arrayOf(idToRemove.toString()))
                    db.close()

                    contents?.removeAt(position)
                    selectedDates.removeAt(position)
                    selectedDay.removeAt(position)
                    adapter.notifyItemRemoved(position)

                } else {
                    Log.e("MyApp", "Invalid positionToRemove: $position")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Navigation Drawer 토글 동작 설정
        val toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened, R.string.drawer_closed)
        toggle.syncState()

        binding.mainDrawerView.setNavigationItemSelectedListener {

            true
        }

        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            requestLauncher.launch(intent)
        }

        contents = savedInstanceState?.let {
            it.getStringArrayList("contents")?.toMutableList()
        } ?: let {
            mutableListOf<String>()
        }

        imageUris = savedInstanceState?.let {
            it.getParcelableArrayList<Uri>("imageUris")?.toMutableList()
        } ?: mutableListOf<Uri>()

        val layoutManager = LinearLayoutManager(this)

        val selectedImageUri = intent.getParcelableExtra<Uri>("imageUri")
        selectedImageUri?.let {
            imageUris?.add(0, it)
        }

        adapter = MyAdapter(contents, imageUris, selectedDates, selectedDay)

        // 리사이클러뷰 어댑터에 아이템 클릭 리스너 설정
        adapter.setOnItemClickListener(object : MyAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // 아이템 클릭 시 다른 화면으로 전환
                if (position >= 0 && position < contents!!.size) {
                    val intent = Intent(this@CommunityActivity, StoryActivity::class.java)
                    intent.putExtra("result", contents!![position])
                    intent.putExtra("imageUri", imageUris?.getOrNull(position))

                    val selectedDate = selectedDates.getOrNull(position) ?: getCurrentDate()
                    intent.putExtra("selectedDate", selectedDate)

                    val selectedDay = selectedDay.getOrNull(position) ?: getCurrentDayOfWeek()
                    intent.putExtra("selectedDay", selectedDay)

                    intent.putExtra("position", position)
                    requestRemoveLauncher.launch(intent)
                } else {
                    Log.e("MyApp", "Invalid position: $position")
                }
            }
        })

        val db = DBHelper(this).readableDatabase
        // db.execSQL("DELETE FROM TODO_TB") // 데이터 초기화
        val cursor = db.rawQuery("select * from TODO_TB", null)
        cursor.run {
            val contentIndex = getColumnIndex("content")
            val dateIndex = getColumnIndex("date")
            val dayIndex = getColumnIndex("day")
            val imageUriIndex = getColumnIndex("image_uri")

            while (moveToNext()) {
                if (contentIndex != -1 && dateIndex != -1 && dayIndex != -1 && imageUriIndex != -1) {
                    val content = getString(contentIndex)
                    val date = getString(dateIndex)
                    val day = getString(dayIndex)
                    val imageUriString = getString(imageUriIndex)
                    val imageUri = Uri.parse(imageUriString)

                    // Add data to the lists
                    contents?.add(0, content)
                    selectedDates.add(0, date)
                    selectedDay.add(0, day)
                    imageUris?.add(0, imageUri)
                } else {

                }
            }
        }
        db.close()

        // 데이터가 추가되면 RecyclerView를 보이도록 설정
        if (contents?.isNotEmpty() == true) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )

        // 현재 클릭 중인 탭 tint. 지우지 말아주세요!
        binding.bottomTapBarOuter.diaryImage.imageTintList = ColorStateList.valueOf(Color.parseColor("#6EC677"))
        binding.bottomTapBarOuter.diaryText.setTextColor(Color.parseColor("#6EC677"))

        // 하단 탭바
        bottomTabClick(binding.bottomTapBarOuter, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("contents", ArrayList(contents))
        outState.putParcelableArrayList("imageUris", ArrayList(imageUris))
    }

    // 옵션 메뉴
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "logout" -> {
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        if (item.itemId == R.id.menu_main_setting) {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun getCurrentDayOfWeek(): String {
        val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val koreanDays = arrayOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일")
        return koreanDays.getOrNull(currentDayOfWeek - 1) ?: "표시되지 않음"
    }
}