package com.example.carebout.view.calendar.decorator

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.carebout.view.calendar.CalendarActivity
import com.example.carebout.view.calendar.database.AppDatabase
import com.example.carebout.view.calendar.database.CalendarEventEntity
import com.example.carebout.view.calendar.database.CalendarEventRepository
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.launch

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CalendarEventRepository = CalendarEventRepository(AppDatabase.getDatabase(application).calendarEventDao())
    private val _data = MutableLiveData<MutableMap<CalendarDay, MutableList<String>>>()
    val data: LiveData<MutableMap<CalendarDay, MutableList<String>>> get() = _data

    private val _datesWithEvents = MutableLiveData<Set<CalendarDay>>()
    val datesWithEvents: LiveData<Set<CalendarDay>> get() = _datesWithEvents
    val allEvents: LiveData<List<CalendarEventEntity>> = repository.getAllEvents()
    private lateinit var calendarView: MaterialCalendarView // CalendarView를 저장할 변수
    private lateinit var calendarActivity: CalendarActivity
    private val _eventsUpdated = MutableLiveData<Unit>()
    val eventsUpdated: LiveData<Unit> get() = _eventsUpdated
    private val selectedDate = MutableLiveData<CalendarDay>()
    private val _observerCount = MutableLiveData<Int>()
    val observerCount: LiveData<Int> get() = _observerCount

    init {
        _observerCount.value = 0
    }

    fun incrementObserverCount() {
        _observerCount.value = (_observerCount.value ?: 0) + 1
    }

    fun decrementObserverCount() {
        _observerCount.value = (_observerCount.value ?: 0).coerceAtLeast(0) - 1
    }

    fun updateEvents() {
        _eventsUpdated.value = Unit
    }
    fun setCalendarActivity(activity: CalendarActivity) {
        calendarActivity = activity
    }

    private fun updateListView(selectedDate: CalendarDay) {
        calendarActivity.updateListView(selectedDate)
    }
    fun setCalendarView(view: MaterialCalendarView) {
        calendarView = view
    }
    suspend fun insertEvent(event: CalendarEventEntity) {
        repository.insertEvent(event)
    }

    suspend fun getEventsForDate(date: Long): List<CalendarEventEntity> {
        return repository.getEventsForDate(date)
    }
    fun setSelectedDate(date: CalendarDay) {
        selectedDate.value = date
    }

    fun initData() {
        selectedDate.observeForever { selectedDate ->
            Log.d("CalendarViewModel", "Observer added for selectedDate: $selectedDate")
            selectedDate?.let {
                viewModelScope.launch {
                    _observerCount.value?.let {
                        if (it == 0) {
                            data.observeForever {
                                Log.d("CalendarViewModel", "Observer added for data")
                                _eventsUpdated.value = Unit
                            }
                            incrementObserverCount()
                        }
                    }

                    // 추가된 로그: 해당 메서드가 호출되었을 때 로그
                    Log.d("CalendarViewModel", "initData: getEventsForDate called for date=${it.date}")

                    // Room Database에서 데이터를 가져오는 메서드
                    val events = getEventsForDate(it.date.time)

                    // 추가된 로그: 가져온 데이터 출력
                    Log.d("CalendarViewModel", "initData: Events for $it: $events")

                    _data.postValue((_data.value ?: mutableMapOf()).apply {
                        put(it, events.map { event -> event.eventText }.toMutableList())
                    })
                    Log.d("CalendarViewModel", "LiveData updated for selectedDate=$selectedDate, data=$_data")

                    // UI 및 데코레이터 업데이트
                    updateListView(selectedDate)
                }
            }
        }

        _observerCount.value?.let {
            if (it == 0) {
                // observer가 등록되지 않은 경우에만 등록
                data.removeObserver(dataObserver)
                data.observeForever(dataObserver)
                incrementObserverCount()
            }
        }
    }

    private val dataObserver = Observer<MutableMap<CalendarDay, MutableList<String>>> { updatedData ->
        // LiveData가 업데이트되면 ListView를 업데이트
        _eventsUpdated.value = Unit
    }
}