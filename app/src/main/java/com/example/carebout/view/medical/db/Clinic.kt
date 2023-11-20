package com.example.carebout.view.medical.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.carebout.view.home.db.PersonalInfo

@Entity(
    tableName = "table_clinic",
    foreignKeys = [
        ForeignKey(
            entity = PersonalInfo::class,
            parentColumns = ["pid"],
            childColumns = ["pid"]
        )
    ]
)
data class Clinic(
    @PrimaryKey(autoGenerate = true)
    var clinicId: Int?,

    @ColumnInfo(name = "pid")
    var pid: Int?, // PersonalInfo의 외래 키

    @ColumnInfo(name = "tag_blood")
    var tag_blood: Boolean?,

    @ColumnInfo(name = "tag_xray")
    var tag_xray: Boolean?,

    @ColumnInfo(name = "tag_ultrasonic")
    var tag_ultrasonic: Boolean?,

    @ColumnInfo(name = "tag_ct")
    var tag_ct: Boolean?,

    @ColumnInfo(name = "tag_mri")
    var tag_mri: Boolean?,

    @ColumnInfo(name = "tag_checkup")
    var tag_checkup: Boolean?,

    @ColumnInfo(name = "date")
    var date: String?,

    @ColumnInfo(name = "hospital")
    var hospital: String?,

    @ColumnInfo(name = "etc")
    var etc: String?
)