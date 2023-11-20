package com.example.carebout.view.medical.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.carebout.view.home.db.PersonalInfo

@Entity(
    tableName = "table_inoculation",
    foreignKeys = [
        ForeignKey(
            entity = PersonalInfo::class,
            parentColumns = ["pid"],
            childColumns = ["pid"]
        )
    ]
)
data class Inoculation (
    @PrimaryKey(autoGenerate = true)
    var inocId: Int?,

    @ColumnInfo(name = "pid")
    var pid: Int?, // PersonalInfo의 외래 키

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

    @ColumnInfo(name = "date")
    var date: String?,

    @ColumnInfo(name = "due")
    var due : String?,

    @ColumnInfo(name = "hospital")
    var hospital: String?,

    @ColumnInfo(name = "etc")
    var etc: String?
)