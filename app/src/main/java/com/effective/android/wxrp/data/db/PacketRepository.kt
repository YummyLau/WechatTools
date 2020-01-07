package com.effective.android.wxrp.data.db

import androidx.annotation.WorkerThread

class PacketRepository(private val packetRecordDao: PacketRecordDao) {


    @WorkerThread
    fun insertPacket(packetRecord: PacketRecord): PacketRecord {
        packetRecordDao.insert(packetRecord)
        return packetRecord
    }


    @WorkerThread
    fun getPacketList(): List<PacketRecord> {
        return packetRecordDao.getAll()
    }

}