package com.example.carebout.view.home.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonalInfoDao {
    @Insert
    fun insertInfo(personalInfo: PersonalInfo): Long

    @Delete
    fun deleteInfo(personalInfo: PersonalInfo)

    @Update
    fun updateInfo(personalInfo: PersonalInfo)

    @Query("SELECT * FROM personalinfo")
    fun getAllInfo(): List<PersonalInfo>

    @Query("SELECT * FROM personalinfo WHERE pid = :pid")
    fun getInfoById(pid: Int): PersonalInfo?   // pid로 모든 정보 가져오기

    @Query("SELECT name FROM personalinfo")
    fun getAllNames(): List<String>
}