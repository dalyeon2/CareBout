package com.example.carebout.view.home.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class PersonalInfo (
    @PrimaryKey var pid: Int,   // id
    @ColumnInfo(name = "name") var name: String,    // 이름
    @ColumnInfo(name = "sex") var sex: String,  // 성별
    @ColumnInfo(name = "birth") var birth: String,  // 생일
    @ColumnInfo(name = "breed") var breed: String,   // (동물의) 품종
)