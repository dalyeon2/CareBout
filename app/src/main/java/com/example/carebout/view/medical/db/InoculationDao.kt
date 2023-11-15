package com.example.carebout.view.medical.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface InoculationDao {
    @Query("SELECT * FROM table_inoculation")
    fun getInoculationAll() : List<Inoculation>

    @Insert
    fun insertInoculation(inoc: Inoculation)

    @Update
    fun updateInoculation(inoc: Inoculation)

    @Delete
    fun deleteInoculation(inoc: Inoculation)

    @Query("SELECT * FROM table_inoculation WHERE inocId = :id")
    fun getInoculationById(id: Int): Inoculation?

    @Query("SELECT * FROM table_inoculation ORDER BY date DESC")
    fun getInocDateAsc(): List<Inoculation>
    //ASC - 오름

    @Query("SELECT * FROM table_inoculation WHERE tag_DHPPL = 1")
    fun getInocWithTagDHPPL(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_Corona = 1")
    fun getInocWithTagC(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_KC = 1")
    fun getInocWithTagKC(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_CVRP = 1")
    fun getInocWithTagCVRP(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_FL = 1")
    fun getInocWithTagFL(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_FID = 1")
    fun getInocWithTagFID(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_Rabies = 1")
    fun getInocWithTagR(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_Heartworm = 1")
    fun getInocWithTagH(): List<Inoculation>

    @Query("SELECT * FROM table_inoculation")
    fun getAllInoculation(): LiveData<List<Inoculation>>


    //@Query("DELETE FROM User WHERE name = :name") // 'name'에 해당하는 유저를 삭제해라
    //    fun deleteUserByName(name: String)
}