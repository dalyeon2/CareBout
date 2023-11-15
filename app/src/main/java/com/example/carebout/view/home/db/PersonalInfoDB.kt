package com.example.carebout.view.home.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PersonalInfo::class, Weight::class], version = 1)
abstract class PersonalInfoDB : RoomDatabase() {
    abstract fun personalInfoDao() : PersonalInfoDao
    abstract fun weightDao() : WeightDao

    companion object {
        private var instance: PersonalInfoDB? = null

        @Synchronized
        fun getInstance(context: Context): PersonalInfoDB? {
            if(instance == null) {
                synchronized(PersonalInfoDB::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PersonalInfoDB::class.java,
                        "personalInfo-database"
                    ).allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()

                }
            }
           return instance
        }
    }
}