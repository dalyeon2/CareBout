package com.example.carebout.view.medical.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.carebout.view.home.db.PersonalInfo
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.home.db.WeightDao

@Database(entities = [DailyTodo::class, Medicine::class, Clinic::class, Inoculation::class,
    PersonalInfo::class, Weight::class]
    , version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTodoDao() : TodoDao
    abstract fun getMedicineDao() : MedicineDao
    abstract fun getClinicDao() : ClinicDao
    abstract fun getInocDao() : InoculationDao

    abstract fun personalInfoDao() : PersonalInfoDao
    abstract fun weightDao() : WeightDao

    companion object{
        val databaseName = "carebout_db"
        var appDatabase : AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase? {
            if(appDatabase == null) {
                appDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    databaseName)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return appDatabase
        }
    }

}