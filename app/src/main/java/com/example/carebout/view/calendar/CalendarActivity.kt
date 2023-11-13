package com.example.carebout.view.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityCalendarBinding
import com.example.carebout.view.calendar.decorator.EventDecorator
import com.example.carebout.view.calendar.decorator.SaturdayDecorator
import com.example.carebout.view.calendar.decorator.SundayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class CalendarActivity : AppCompatActivity() {
    var userID: String = "userID"

    lateinit var binding: ActivityCalendarBinding
    lateinit var calendarView: MaterialCalendarView
    lateinit var listView: ListView
    val datesWithEvents = mutableSetOf<CalendarDay>()
    lateinit var eventDecorator: EventDecorator
    val data = mutableMapOf<CalendarDay, MutableList<String>>()
    val adapter: ArrayAdapter<String> by lazy { ArrayAdapter(this, android.R.layout.simple_list_item_1) }
    private lateinit var viewModel: CalendarViewModel
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.list)
        listView.adapter = adapter

        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)

        calendarView = findViewById(R.id.calendarView)

        calendarView.addDecorator(SundayDecorator()) // 일요일은 빨간색
        calendarView.addDecorator(SaturdayDecorator()) // 토요일은 파란색
        eventDecorator = EventDecorator(this)
        calendarView.addDecorator(eventDecorator)
        val button = findViewById<Button>(R.id.addplan)
        button.setOnClickListener {
            showAddEventDialog()
        }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            if (selected) {
                updateListView(date)
            }
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position) // 클릭한 항목 가져오기
            showAlertDialog(selectedItem)
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position) // 길게 클릭한 항목 가져오기
            showEditDialog(selectedItem, position)
            true // 이벤트 처리를 완료했음을 알림
        }
        //탭 클릭시 화면 전환을 위한 함수입니다. 지우지 말아주세요!
        bottomTabClick(binding.bottomTapBarOuter, this)
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
    private fun updateListView(selectedDate: CalendarDay) {
        val events = data[selectedDate] ?: mutableListOf()
        adapter.clear()
        adapter.addAll(events)
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
                val events = data[selectedDate] ?: mutableListOf()
                events.add(eventText) // 해당 날짜에 이벤트 추가
                data[selectedDate] = events
                adapter.clear()

                // datesWithEvents를 업데이트하고 해당 날짜가 포함되어 있는지 확인
                datesWithEvents.add(selectedDate)

                data.entries.forEach { entry ->
                    adapter.addAll(entry.value)
                }
                adapter.notifyDataSetChanged() // ListView 업데이트

                // EventDecorator를 업데이트된 datesWithEvents로 설정
                eventDecorator.setDatesWithEvents(datesWithEvents)
                calendarView.invalidateDecorators()

                Toast.makeText(this, "저장되었습니다!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소") { _, _ ->
                Toast.makeText(this, "취소했습니다!", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }
}
