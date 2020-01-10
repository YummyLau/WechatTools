package com.effective.android.wxrp.data.sp

import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import java.util.*


object LocalizationHelper {

    lateinit var config: SettingConfig
    var userWxName: String = ""
    var userWxAvatar: Drawable? = null

    /**
     * 必须先行调用
     */
    @JvmStatic
    fun init() {

        //config信息
        config = SettingConfig()
        //群聊后去自己
        config.supportGettingSelfPacket = RpApplication.sp().getBoolean(Constants.KEY_OPEN_GET_SELF_PACKET, true)

        //支持过滤
        config.supportFilter = RpApplication.sp().getBoolean(Constants.KEY_FILTER, false)
        if (config.supportFilter) {
            val list = RpApplication.sp().getString(Constants.KEY_FILTER_DATA, Constants.VALUE_FILTER_DATA)!!.split(Constants.SPLIT_POINT).toList()
            config.filterTags.addAll(list)
        }

        //延迟选项
        config.delayModel = RpApplication.sp().getInt(Constants.KEY_DELAY_MODEL, Constants.VALUE_DELAY_CLOSE)
        config.fixationTime = RpApplication.sp().getLong(Constants.KEY_DELAY_MODEL_FIXATION, Constants.VALUE_DEFAULT_FIXATION)
        config.randomDelayTime = RpApplication.sp().getLong(Constants.KEY_DELAY_MODEL_RANDOM, Constants.VALUE_DEFAULT_RANDOM)

        //默认打开
        config.openPlugin = true
    }

    @JvmStatic
    fun isSupportPlugin() = config.openPlugin

    @JvmStatic
    fun supportPlugin(support: Boolean) {
        config.openPlugin = support
    }

    @JvmStatic
    fun getConfigName() = userWxName

    @JvmStatic
    fun setConfigName(userName: String): Boolean {
        if (!TextUtils.equals(userWxName, userName)) {
            userWxName = userName
            return RpApplication.sp().edit().putString(Constants.KEY_USER_WX_NAME, userName).commit()
        }
        return false
    }

    @JvmStatic
    fun getHistoryConfigName() = RpApplication.sp().getString(Constants.KEY_USER_WX_NAME, "")!!

    @JvmStatic
    fun getConfigAvatar() = ContextCompat.getDrawable(RpApplication.instance().applicationContext, R.drawable.ic_wx_defalut_avatar)

    @JvmStatic
    fun isSupportGettingSelfPacket() = config.supportGettingSelfPacket

    @JvmStatic
    fun supportGettingSelfPacket(support: Boolean): Boolean {
        if (support != config.supportGettingSelfPacket) {
            config.supportGettingSelfPacket = support
            return RpApplication.sp().edit().putBoolean(Constants.KEY_OPEN_GET_SELF_PACKET, support).commit()
        }
        return false
    }


    @JvmStatic
    fun setDelayTime(delayTime: Long): Boolean {
        return when (config.delayModel) {
            Constants.VALUE_DELAY_FIXATION -> {
                if (delayTime != config.fixationTime) {
                    config.fixationTime = delayTime
                    return RpApplication.sp().edit().putLong(Constants.KEY_DELAY_MODEL_FIXATION, delayTime).commit()
                }
                return false
            }
            Constants.VALUE_DELAY_RANDOM -> {
                if (delayTime != config.randomDelayTime) {
                    config.randomDelayTime = delayTime
                    return RpApplication.sp().edit().putLong(Constants.KEY_DELAY_MODEL_RANDOM, delayTime).commit()
                }
                return false
            }
            else -> {
                false
            }
        }

    }

    @JvmStatic
    fun getDelayTime(configValue: Boolean): Int {
        return when (config.delayModel) {
            Constants.VALUE_DELAY_FIXATION -> {
                config.fixationTime.toInt()
            }
            Constants.VALUE_DELAY_RANDOM -> {
                return if (configValue) {
                    config.randomDelayTime.toInt()
                } else {
                    val rand = Random()
                    return rand.nextInt(config.randomDelayTime.toInt() + 1)
                }

            }
            else -> {
                0
            }
        }
    }

    @JvmStatic
    fun getDelayModel() = config.delayModel

    @JvmStatic
    fun setDelayModel(model: Int): Boolean {
        if (model >= Constants.VALUE_DELAY_CLOSE && model <= Constants.VALUE_DELAY_RANDOM) {
            if (model != config.delayModel) {
                config.delayModel = model
                return RpApplication.sp().edit().putInt(Constants.KEY_DELAY_MODEL, model).commit()
            }
            return false
        }
        return false
    }

    fun supportFilterTag(supportFilter: Boolean): Boolean {
        if (config.supportFilter != supportFilter) {
            config.supportFilter = supportFilter
            config.filterTags.clear()
            if (supportFilter) {
                val list = RpApplication.sp().getString(Constants.KEY_FILTER_DATA, Constants.VALUE_FILTER_DATA)!!.split(Constants.SPLIT_POINT).toList()
                config.filterTags.addAll(list)
            }
            return RpApplication.sp().edit().putBoolean(Constants.KEY_FILTER, supportFilter).commit()
        }
        return false
    }

    @JvmStatic
    fun isSupportFilter() = config.supportFilter

    @JvmStatic
    fun getFilterTag() = config.filterTags
}
