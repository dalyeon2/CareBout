package com.example.carebout.view.medical.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClinicDao {
    @Query("SELECT * FROM table_clinic")
    fun getClinicAll() : List<Clinic>

    @Insert
    fun insertClinic(clinic: Clinic)

    @Update
    fun updateClinic(clinic: Clinic)

    @Delete
    fun deleteClinic(clinic: Clinic)

    @Query("SELECT * FROM table_clinic WHERE clinicId = :id")
    fun getClinicById(id: Int): Clinic?

    @Query("SELECT * FROM table_clinic ORDER BY date DESC")
    fun getClinicDateAsc(): List<Clinic>
    //ASC - 오름

    @Query("SELECT * FROM table_clinic WHERE tag_blood = 1")
    fun getClinicWithTagB(): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_xray = 1")
    fun getClinicWithTagX(): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_ultrasonic = 1")
    fun getClinicWithTagU(): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_ct = 1")
    fun getClinicWithTagC(): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_mri = 1")
    fun getClinicWithTagM(): List<Clinic>

    @Query("SELECT * FROM table_clinic WHERE tag_checkup = 1")
    fun getClinicWithTagCheckup(): List<Clinic>

    //@Query("DELETE FROM User WHERE name = :name") // 'name'에 해당하는 유저를 삭제해라
    //    fun deleteUserByName(name: String)
}