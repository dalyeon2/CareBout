package com.example.carebout.view.calendar.database

import androidx.lifecycle.LiveData

class CalendarEventRepository(private val calendarEventDao: CalendarEventDao) {
    suspend fun insertEvent(event: CalendarEventEntity) {
        calendarEventDao.insertEvent(event)
    }

    suspend fun getEventsForDate(date: Long): List<CalendarEventEntity> {
        return calendarEventDao.getEventsForDate(date)
    }

    fun getAllEvents(): LiveData<List<CalendarEventEntity>> {
        return calendarEventDao.getAllEvents()
    }
}