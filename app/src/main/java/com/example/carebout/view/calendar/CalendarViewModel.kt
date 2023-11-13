package com.example.carebout.view.calendar
import androidx.lifecycle.ViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalendarViewModel : ViewModel() {
    val data = mutableMapOf<CalendarDay, MutableList<String>>()
}