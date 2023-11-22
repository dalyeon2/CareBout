package com.example.carebout.view.medical.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao

interface MedicineDao {
    @Query("SELECT * FROM table_medicine WHERE pid = :pid")
    fun getMediAll(pid: Int) : List<Medicine>

//    @Query("SELECT id, title, start, `end`, etc FROM table_medicine")
//    fun getMediList() : List<MedicineInfo>

    @Insert
    fun insertMedi(medi: Medicine)

    @Update
    fun updateMedi(medi: Medicine)

    @Delete
    fun deleteMedi(medi: Medicine)

    @Query("SELECT * FROM table_medicine WHERE pid = :pid")
    fun getMediByPid(pid: Int): Medicine?

    @Query("DELETE FROM table_medicine WHERE pid = :pid")
    fun deletePidMedi(pid: Int)

    @Query("SELECT * FROM table_medicine WHERE pid = :pid ORDER BY start DESC")
    fun getMediDateAsc(pid: Int): List<Medicine>

    @Query("SELECT * FROM table_medicine WHERE mediId = :id AND pid = :pid ORDER BY start DESC")
    fun getMediById(id: Int, pid: Int): Medicine?

    @Query("SELECT * FROM table_medicine WHERE checkBox = 1 AND pid = :pid ORDER BY start DESC")
    fun getMediWithCheck(pid: Int): List<Medicine>

    @Query("SELECT * FROM table_medicine WHERE pid = :pid ORDER BY start DESC")
    fun getAllMedicine(pid: Int): LiveData<List<Medicine>>

    //@Query("DELETE FROM User WHERE name = :name") // 'name'에 해당하는 유저를 삭제해라
    //    fun deleteUserByName(name: String)
}