package com.effective.android.wxrp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.effective.android.wxrp.data.db.PacketRecordDataBase
import com.effective.android.wxrp.data.db.PacketRepository
import com.effective.android.wxrp.data.sp.LocalizationHelper

class RpApplication : Application() {

    companion object {

        private const val SP_FILE_NAME = "sp_name_wxrp"

        @Volatile
        private var instance: Application? = null
        var sharedPreferences: SharedPreferences? = null
        var packetRepository: PacketRepository? = null
        var database: PacketRecordDataBase? = null

        @Synchronized
        @JvmStatic
        fun instance(): Application {
            return instance!!
        }

        @JvmStatic
        fun sp(): SharedPreferences {
            return sharedPreferences!!
        }

        @JvmStatic
        fun repository(): PacketRepository {
            return packetRepository!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sharedPreferences = getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)
        database = PacketRecordDataBase.getInstance(this)
        packetRepository = PacketRepository(database!!.packetRecordDao())
        LocalizationHelper.init()
    }
}