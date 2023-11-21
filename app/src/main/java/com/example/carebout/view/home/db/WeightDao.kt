package com.example.carebout.view.home.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WeightDao {
    @Insert
    fun insertInfo(weight: Weight): Long

    @Delete
    fun deleteInfo(weight: Weight)

    @Update
    fun updateInfo(weight: Weight)

    @Query("SELECT * FROM weight WHERE pid = :pid")
    fun getWeightById(pid: Int): List<Weight>   // pid로 모든 정보 가져오기

}