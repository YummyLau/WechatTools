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
        initVersion()
    }

    private fun initVersion() {
        val hasWeChat = ToolUtil.isWeixinAvilible(this)
        if (hasWeChat) {
            val version: String? = ToolUtil.getWeChatVersion(this)
            if (TextUtils.isEmpty(version)) {
                ToolUtil.toast(this, "读取微信版本失败, 请联系开发人员！")
            } else {
                if (ToolUtil.supportWeChatVersion(version)) {
                    VersionManager.setWeChatVersionInfo(
                            when (version) {
                                Version700.VERSION -> Version700()
                                Version703.VERSION  -> Version703()
                                Version7010.VERSION  -> Version7010()
                                else -> null
                            }
                    )
                } else {
                    ToolUtil.toast(this, "请安装或者升级微信到对应支持的版本！")
                }
            }
        } else {
            ToolUtil.toast(this, "插件检测不到微信软件, 请安装微信！")
        }
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