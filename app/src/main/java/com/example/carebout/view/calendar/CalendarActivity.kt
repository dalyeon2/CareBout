package com.example.carebout.view.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityCalendarBinding
import com.example.carebout.view.calendar.database.AppDatabase
import com.example.carebout.view.calendar.database.CalendarEventEntity
import com.example.carebout.view.calendar.database.CalendarEventRepository
import com.example.carebout.view.calendar.decorator.CalendarViewModel
import com.example.carebout.view.calendar.decorator.EventDecorator
import com.example.carebout.view.calendar.decorator.SaturdayDecorator
import com.example.carebout.view.calendar.decorator.SundayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CalendarActivity : AppCompatActivity() {
    private var userID: String = "userID"

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var listView: ListView
    private val datesWithEvents = mutableSetOf<CalendarDay>()
    private lateinit var eventDecorator: EventDecorator
    private val data = mutableMapOf<CalendarDay, MutableList<String>>()
    private val adapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1
        )
    }
    private lateinit var database: AppDatabase
    private lateinit var calendarEventRepository: CalendarEventRepository
    private lateinit var viewModel: CalendarViewModel

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendarView = findViewById(R.id.calendarView)
        listView = findViewById(R.id.list)
        listView.adapter = adapter

        calendarView.addDecorator(SundayDecorator()) // 일요일은 빨간색
        calendarView.addDecorator(SaturdayDecorator()) // 토요일은 파란색
        eventDecorator = EventDecorator(this)
        calendarView.addDecorator(eventDecorator)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(CalendarViewModel::class.java)
        viewModel.setCalendarActivity(this) // CalendarActivity 설정
        viewModel.setCalendarView(calendarView)
        database = AppDatabase.getDatabase(applicationContext)
        calendarEventRepository = CalendarEventRepository(database.calendarEventDao())
        if (savedInstanceState == null) {
            // 액티비티가 처음으로 생성될 때만 호출
            viewModel.initData()
        }

        Log.d("CalendarActivity", "onCreate: initData called")
        val button = findViewById<Button>(R.id.addplan)
        button.setOnClickListener {
            showAddEventDialog()
        }
        calendarView.setOnDateChangedListener { _, date, _ ->
            if (date != null) {
                // 선택된 날짜가 null이 아닌 경우에만 실행
                updateListView(date)
            } else {
                Log.e("CalendarViewModel", "Selected date is null")
            }
        }
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position) // 클릭한 항목 가져오기
            showAlertDialog(selectedItem)
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position) // 길게 클릭한 항목 가져오기
            showEditDialog(selectedItem, position)
            true // 이벤트 처리를 완료했음을 알림
        }
        // 탭 클릭시 화면 전환을 위한 함수입니다. 지우지 말아주세요!
        bottomTabClick(binding.bottomTapBarOuter, this)
        if (savedInstanceState != null) {
            updateListView(calendarView.selectedDate)
        }
    }

    private fun showEditDialog(itemText: String?, position: Int) {
        val input = EditText(this)
        input.setText(itemText)
        input.gravity = Gravity.CENTER

        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("항목 수정")
            .setView(input)
            .setPositiveButton("확인") { dialog, which ->
                val editedText = input.text.toString()
                if (editedText.isNotEmpty()) {
                    // 수정 작업 수행
                    adapter.remove(itemText) // 원래 항목 삭제
                    adapter.insert(editedText, position) // 수정된 항목 추가
                    adapter.notifyDataSetChanged() // ListView 업데이트

                    Toast.makeText(this, "수정되었습니다!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss() // AlertDialog 닫기
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss() // AlertDialog 닫기
            }
            .create()
            .show()
    }

    private fun showAlertDialog(itemText: String?) {
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("일정 삭제")
            .setMessage(itemText)
            .setPositiveButton("삭제") { dialog, which ->
                val selectedDate = calendarView.selectedDate // 선택한 날짜 가져오기
                val events = data[selectedDate] ?: mutableListOf()
                events.remove(itemText) // 해당 날짜의 항목에서 제거
                data[selectedDate] = events // 항목을 제거한 데이터로 업데이트
                adapter.remove(itemText) // ListView에서 해당 항목 삭제
                adapter.notifyDataSetChanged() // ListView 업데이트
                Toast.makeText(this, "삭제되었습니다!", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // AlertDialog 닫기
            }
            .setNegativeButton("취소") { dialog, which ->
                dialog.dismiss() // AlertDialog 닫기
            }
            .create()
            .show()
    }

    fun updateListView(selectedDate: CalendarDay) {
        val events = data[selectedDate] ?: mutableListOf()
        Log.d("CalendarViewModel", "updateListView: Events for $selectedDate: $events")

        adapter.clear()
        if (events.isNotEmpty()) {
            adapter.addAll(events)
            Log.d("CalendarViewModel", "updateListView: Number of items in the adapter: ${adapter.count}")
        } else {
            Log.d("CalendarViewModel", "updateListView: Events list is empty for $selectedDate")
        }
        adapter.notifyDataSetChanged()
    }

    private fun showAddEventDialog() {
        val et = EditText(this)
        et.gravity = Gravity.CENTER
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("일정추가")
            .setView(et)
            .setCancelable(false)
            .setPositiveButton("확인") { _, _ ->
                val selectedDate = calendarView.selectedDate // 선택한 날짜 가져오기
                val eventText = et.text.toString()

                // Room 데이터베이스에 이벤트 삽입
                val eventEntity =
                    CalendarEventEntity(date = selectedDate.date.time, eventText = eventText)
                insertEvent(eventEntity)

                // 데이터 맵 업데이트
                val events = data[selectedDate] ?: mutableListOf()
                events.add(eventText)
                data[selectedDate] = events

                // UI 및 데코레이터 업데이트
                viewModel.eventsUpdated.observe(this) {
                    // LiveData가 업데이트되면 ListView를 업데이트
                    updateListView(calendarView.selectedDate)
                }

                // LiveData 업데이트
                viewModel.updateEvents()

                Toast.makeText(this, "저장되었습니다!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소") { _, _ ->
                Toast.makeText(this, "취소했습니다!", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    private fun insertEvent(eventEntity: CalendarEventEntity) {
        // Room 데이터베이스에 이벤트 삽입
        GlobalScope.launch(Dispatchers.IO) {
            calendarEventRepository.insertEvent(eventEntity)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("userID", userID)
        // datesWithEvents를 ParcelableArrayList로 변환하여 저장
        outState.putParcelableArrayList("datesWithEvents", ArrayList(datesWithEvents.toList()))
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        userID = savedInstanceState.getString("userID", "userID")!!
        viewModel.initData()

        // LiveData Observer 추가
        viewModel.observerCount.observe(this) { count ->
            Log.d("CalendarActivity", "onCreate: LiveData observers count=$count")

            viewModel.data.observe(this, dataObserver)
        }
    }
    private val dataObserver = Observer<MutableMap<CalendarDay, MutableList<String>>> { updatedData ->
        viewModel.datesWithEvents.observe(this) { updatedDatesWithEvents ->
            updatedDatesWithEvents?.let {
                // 선택된 날짜가 변경될 때만 ListView를 업데이트합니다.
                if (calendarView.selectedDate != null) {
                    Log.d("CalendarActivity", "onCreate: LiveData updated for selectedDate=${calendarView.selectedDate}")
                    updateListView(calendarView.selectedDate)
                }
            }
        }
    }
}
