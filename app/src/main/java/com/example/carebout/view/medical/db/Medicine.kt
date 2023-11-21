package com.example.carebout.view.medical.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.carebout.view.home.db.PersonalInfo

@Entity(
    tableName = "table_medicine",
    foreignKeys = [
        ForeignKey(
            entity = PersonalInfo::class,
            parentColumns = ["pid"],
            childColumns = ["pid"]
        )
    ]
) //어노테이션
data class Medicine (
    @PrimaryKey(autoGenerate = true)
    var mediId: Int?,

    @ColumnInfo(name = "pid")
    var pid: Int?, // PersonalInfo의 외래 키

    @ColumnInfo(name = "title")
    var title: String?,

    @ColumnInfo(name = "start")
    var start: String?,

    @ColumnInfo(name = "end")
    var end: String?,

    @ColumnInfo(name = "checkBox")
    var checkBox: Boolean?,

    @ColumnInfo(name = "etc")
    var etc: String?
)