package com.effective.android.wxrp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.data.db.PacketRecordDataBase
import com.effective.android.wxrp.data.db.PacketRepository
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.version.VersionManager
import com.effective.android.wxrp.version.Version700
import com.effective.android.wxrp.version.Version7010
import com.effective.android.wxrp.version.Version703

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
        Config.init()
    }


    override fun onTerminate() {
        super.onTerminate()
        Config.onSave()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Config.onSave()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Config.onSave()
    }
}