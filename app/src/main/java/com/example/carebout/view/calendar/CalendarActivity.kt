package com.example.carebout.view.calendar

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.carebout.R
import com.example.carebout.base.bottomTabClick
import com.example.carebout.databinding.ActivityCalendarBinding
import com.example.carebout.view.calendar.decorator.SaturdayDecorator
import com.example.carebout.view.calendar.decorator.SundayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class CalendarActivity : AppCompatActivity() {
    var userID: String = "userID"

    lateinit var binding: ActivityCalendarBinding
    lateinit var calendarView: MaterialCalendarView
    lateinit var listView: ListView
    val data = mutableMapOf<CalendarDay, MutableList<String>>()
    val adapter: ArrayAdapter<String> by lazy { ArrayAdapter(this, android.R.layout.simple_list_item_1) }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.list)
        listView.adapter = adapter

        calendarView = findViewById(R.id.calendarView)

        calendarView.addDecorator(SundayDecorator()) // 일요일은 빨간색
        calendarView.addDecorator(SaturdayDecorator()) // 토요일은 파란색

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
        //탭 클릭시 화면 전환을 위한 함수입니다. 지우지 말아주세요!
        bottomTabClick(binding.bottomTapBarOuter, this)

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
                data.entries.forEach { entry ->
                    if (calendarView.selectedDate == entry.key) {
                        adapter.addAll(entry.value)
                    }
                }
                adapter.notifyDataSetChanged() // ListView 업데이트
                Toast.makeText(this, "저장되었습니다!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("취소") { _, _ ->
                Toast.makeText(this, "취소했습니다!", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }
}
