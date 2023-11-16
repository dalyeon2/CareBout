package com.example.carebout.view.medical.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_inoculation")
data class Inoculation (
    @PrimaryKey(autoGenerate = true)
    var inocId: Int?,

    @ColumnInfo(name = "tag_DHPPL")
    var tag_DHPPL: Boolean?,

    @ColumnInfo(name = "tag_Corona")
    var tag_Corona: Boolean?,

    @ColumnInfo(name = "tag_KC")
    var tag_KC: Boolean?,
//    Kennel Cough

    @ColumnInfo(name = "tag_CVRP")
    var tag_CVRP: Boolean?,

    @ColumnInfo(name = "tag_FL")
    var tag_FL: Boolean?,
    //Feline Leukemia

    @ColumnInfo(name = "tag_FID")
    var tag_FID: Boolean?,

    @ColumnInfo(name = "tag_Rabies")
    var tag_Rabies: Boolean?,

    @ColumnInfo(name = "tag_Heartworm")
    var tag_Heartworm: Boolean?,

    @ColumnInfo(name = "tag")
    var tag: String?,

    @ColumnInfo(name = "date")
    var date: String?,

    @ColumnInfo(name = "due")
    var due : String?,

    @ColumnInfo(name = "hospital")
    var hospital: String?,

    @ColumnInfo(name = "etc")
    var etc: String?
)