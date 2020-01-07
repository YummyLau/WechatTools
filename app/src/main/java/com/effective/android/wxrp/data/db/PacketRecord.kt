package com.effective.android.wxrp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packetRecord")
data class PacketRecord(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "post_user") var postUser: String,
        @ColumnInfo(name = "time") var time: Long,
        @ColumnInfo(name = "number") var num: Float) {

    constructor() : this(null, "", 0, 0.0f)
}