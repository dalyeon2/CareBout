package com.example.carebout.view.home.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weight",
    foreignKeys = [
        ForeignKey(
            entity = PersonalInfo::class,
            parentColumns = ["pid"],
            childColumns = ["pid"]
        )
    ]
)
data class Weight(
    @ColumnInfo(name = "pid") var pid: Int, // 외래키 (PersonalInfo의 id)
    @ColumnInfo(name = "weight") var weight: Float, // 체중
    @ColumnInfo(name = "date") var date: String // 날짜
){

    @PrimaryKey(autoGenerate = true) var weightId: Int = 0   // id
}