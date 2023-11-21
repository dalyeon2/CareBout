package com.example.carebout.view.medical.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface InoculationDao {
    @Query("SELECT * FROM table_inoculation WHERE pid = :pid")
    fun getInoculationAll(pid: Int) : List<Inoculation>

    @Insert
    fun insertInoculation(inoc: Inoculation)

    @Update
    fun updateInoculation(inoc: Inoculation)

    @Delete
    fun deleteInoculation(inoc: Inoculation)

    @Query("SELECT * FROM table_inoculation WHERE pid = :pid")
    fun getInoculationByPid(pid: Int): Inoculation?

    @Query("DELETE FROM table_inoculation WHERE pid = :pid")
    fun deletePidInoc(pid: Int)

    @Query("SELECT * FROM table_inoculation WHERE inocId = :id AND pid = :pid ORDER BY date DESC")
    fun getInoculationById(id: Int, pid: Int): Inoculation?

    @Query("SELECT * FROM table_inoculation WHERE pid = :pid ORDER BY date DESC")
    fun getInocDateAsc(pid: Int): List<Inoculation>
    //ASC - 오름

    @Query("SELECT * FROM table_inoculation WHERE tag_DHPPL = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagDHPPL(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_Corona = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagC(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_KC = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagKC(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_CVRP = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagCVRP(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_FL = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagFL(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_FID = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagFID(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_Rabies = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagR(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE tag_Heartworm = 1 AND pid = :pid ORDER BY date DESC")
    fun getInocWithTagH(pid: Int): List<Inoculation>

    @Query("SELECT * FROM table_inoculation WHERE pid = :pid ORDER BY date DESC")
    fun getAllInoculation(pid: Int): LiveData<List<Inoculation>>


    //@Query("DELETE FROM User WHERE name = :name") // 'name'에 해당하는 유저를 삭제해라
    //    fun deleteUserByName(name: String)
}