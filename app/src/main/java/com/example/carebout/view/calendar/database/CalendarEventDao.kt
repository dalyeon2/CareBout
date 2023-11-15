package com.example.carebout.view.calendar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
//어떻게 정보를 가져올것인가?
@Dao
interface CalendarEventDao {
    @Insert
    fun insertEvent(event: CalendarEventEntity)

    @Query("SELECT * FROM calendar_events WHERE date = :date")
    fun getEventsForDate(date: Long): List<CalendarEventEntity>

    @Query("SELECT * FROM calendar_events ORDER BY date ASC")
    fun getAllEvents(): LiveData<List<CalendarEventEntity>>

}