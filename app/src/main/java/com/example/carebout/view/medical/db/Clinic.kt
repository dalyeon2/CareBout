package com.example.carebout.view.medical.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_clinic")
data class Clinic(
    @PrimaryKey(autoGenerate = true)
    var clinicId: Int?,

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

    @ColumnInfo(name = "tag")
    var tag: String?,

    @ColumnInfo(name = "date")
    var date: String?,

    @ColumnInfo(name = "hospital")
    var hospital: String?,

    @ColumnInfo(name = "etc")
    var etc: String?
)