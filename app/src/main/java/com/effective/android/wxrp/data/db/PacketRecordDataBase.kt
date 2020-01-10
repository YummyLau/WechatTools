package com.effective.android.wxrp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PacketRecord::class], version = 1)
abstract class PacketRecordDataBase : RoomDatabase() {

    abstract fun packetRecordDao(): PacketRecordDao

    companion object {
        private var INSTANCE: PacketRecordDataBase? = null

        fun getInstance(context: Context): PacketRecordDataBase {
            if (INSTANCE == null) {
                synchronized(PacketRecordDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            PacketRecordDataBase::class.java, "packetRecord.db").build()
                }
            }
            return INSTANCE!!
        }

        fun destory() {
            INSTANCE = null
        }
    }
}