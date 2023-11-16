package com.example.carebout.view.calendar.decorator

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.carebout.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(private val context: Context) : DayViewDecorator {

    private val drawable: Drawable = ContextCompat.getDrawable(context, R.drawable.red_circle)!!
    private var datesWithEvents: Set<CalendarDay> = emptySet()

    fun setDatesWithEvents(datesWithEvents: Set<CalendarDay>) {
        this.datesWithEvents = datesWithEvents
    }

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        Log.d("EventDecorator", "shouldDecorate: day=$day, datesWithEvents=$datesWithEvents")
        val result = datesWithEvents.any { it == day }
        Log.d("EventDecorator", "shouldDecorate result: $result")
        return result
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(drawable)
    }
}