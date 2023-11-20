package com.example.carebout.view.medical.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClinicDao {
    @Query("SELECT * FROM table_clinic WHERE pid = :pid")
    fun getClinicAll(pid: Int) : List<Clinic>

    @Insert
    fun insertClinic(clinic: Clinic)

    @Update
    fun updateClinic(clinic: Clinic)

    @Delete
    fun deleteClinic(clinic: Clinic)

    @Query("SELECT * FROM table_clinic WHERE pid = :pid")
    fun getClinicByPid(pid: Int): Clinic?

    @Query("DELETE FROM table_clinic WHERE pid = :pid")
    fun deletePidClinic(pid: Int)

    @Query("SELECT * FROM table_clinic WHERE clinicId = :id AND pid = :pid ORDER BY date DESC")
    fun getClinicById(id: Int, pid: Int): Clinic?

    @Query("SELECT * FROM table_clinic WHERE pid = :pid ORDER BY date DESC")
    fun getClinicDateAsc(pid: Int): List<Clinic>
    //ASC - 오름

    @Query("SELECT * FROM table_clinic WHERE tag_blood = 1 AND pid = :pid ORDER BY date DESC")
    fun getClinicWithTagB(pid: Int): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_xray = 1 AND pid = :pid ORDER BY date DESC")
    fun getClinicWithTagX(pid: Int): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_ultrasonic = 1 AND pid = :pid ORDER BY date DESC")
    fun getClinicWithTagU(pid: Int): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_ct = 1 AND pid = :pid ORDER BY date DESC")
    fun getClinicWithTagC(pid: Int): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_mri = 1 AND pid = :pid ORDER BY date DESC")
    fun getClinicWithTagM(pid: Int): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_checkup = 1 AND pid = :pid ORDER BY date DESC")
    fun getClinicWithTagCheckup(pid: Int): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE pid = :pid ORDER BY date DESC")
    fun getAllClinic(pid: Int): LiveData<List<Clinic>>

    //@Query("DELETE FROM User WHERE name = :name") // 'name'에 해당하는 유저를 삭제해라
    //    fun deleteUserByName(name: String)
}