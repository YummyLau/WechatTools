package com.effective.android.wxrp.data.db


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface PacketRecordDao {

    @Query("SELECT * from packetRecord")
    fun getAll(): List<PacketRecord>

    @Insert(onConflict = REPLACE)
    fun insert(packetRecord: PacketRecord)
}