package com.example.carebout.view.home.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WeightDao {
    @Insert
    fun insertInfo(weight: Weight)

    @Delete
    fun deleteInfo(weight: Weight)

    @Update
    fun updateInfo(weight: Weight)

    @Query("SELECT * FROM weight WHERE pid = :pid")
    fun getInfoById(pid: Int): List<Weight>   // pid로 모든 정보 가져오기


    @Query("SELECT * FROM weight")
    fun TEMP(): List<Weight>   // pid로 모든 정보 가져오기
}