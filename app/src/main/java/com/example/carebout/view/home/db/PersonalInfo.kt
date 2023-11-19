package com.example.carebout.view.home.db

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonalInfo (
    @ColumnInfo(name = "name") var name: String,    // 이름
    @ColumnInfo(name = "sex") var sex: String,  // 성별
    @ColumnInfo(name = "birth") var birth: String,  // 생일
    @ColumnInfo(name = "breed") var breed: String,   // (동물의) 품종
    @ColumnInfo(name = "animal") var animal: String, // 동물 종류 = 강아지 or 고양이
    @ColumnInfo(name = "image") var image: String   // 프로필 사진
){

    @PrimaryKey(autoGenerate = true) var pid: Int = 0   // id
}

