package com.example.carebout.view.home.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonalInfoDao {
//    @Query("SELECT * FROM personalinfo")
//    fun getAll(): List<PersonalInfo>
//
//    @Query("SELECT * FROM personalinfo WHERE pid IN (:PersonalInfoIds)")
//    fun loadAllByIds(PersonalInfoIds: IntArray): List<PersonalInfo>
//
//    @Query("SELECT * FROM PersonalInfo WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): PersonalInfo
//
//    @Insert
//    fun insertAll(vararg PersonalInfos: PersonalInfo)
//
//    @Delete
//    fun delete(PersonalInfo: PersonalInfo)
}