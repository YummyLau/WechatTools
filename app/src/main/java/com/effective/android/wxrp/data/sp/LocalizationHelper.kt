package com.effective.android.wxrp.data.sp

import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.utils.Logger
import java.lang.StringBuilder
import java.util.*


object LocalizationHelper {

    private const val TAG = "LocalizationHelper"
    lateinit var config: SettingConfig
    var userWxName: String = ""

    /**
     * 必须先行调用
     */
    @JvmStatic
    fun init() {

        //config信息
        config = SettingConfig()
        //群聊后去自己
        config.supportGettingSelfPacket = RpApplication.sp().getBoolean(Constants.KEY_OPEN_GET_SELF_PACKET, true)

        //支持过滤红包
        config.supportFilterPacket = RpApplication.sp().getBoolean(Constants.KEY_FILTER_PACKET, false)
        val filterPackets = RpApplication.sp().getString(Constants.KEY_FILTER_PACKET_DATA, Constants.VALUE_FILTER_PACKET_DATA)!!.split(Constants.SPLIT_POINT).toList()
        config.filterPacketTags.addAll(filterPackets.filter { !TextUtils.isEmpty(it) })

        //支持过滤会话
        config.supportFilterConversation = RpApplication.sp().getBoolean(Constants.KEY_FILTER_CONVERSATION, false)
        val filterConversations = RpApplication.sp().getString(Constants.KEY_FILTER_CONVERSATION_DATA, "")!!.split(Constants.SPLIT_POINT).toList()
        config.filterConversationTags.addAll(filterConversations.filter { !TextUtils.isEmpty(it) })

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
    fun isSupportFloat() = config.openFloat

    @JvmStatic
    fun supportFloat(support: Boolean) {
        config.openFloat = support
    }

    @JvmStatic
    fun getConfigName() = userWxName

    @JvmStatic
    fun setConfigName(userName: String): Boolean {
        Logger.d(TAG, "setConfigName: $userName")
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
        Logger.d(TAG, "supportGettingSelfPacket: $support")
        if (support != config.supportGettingSelfPacket) {
            config.supportGettingSelfPacket = support
            return RpApplication.sp().edit().putBoolean(Constants.KEY_OPEN_GET_SELF_PACKET, support).commit()
        }
        return false
    }


    @JvmStatic
    fun setDelayTime(delayTime: Long): Boolean {
        Logger.d(TAG, "setDelayTime: $delayTime")
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
        Logger.d(TAG, "setDelayModel: $model")
        if (model >= Constants.VALUE_DELAY_CLOSE && model <= Constants.VALUE_DELAY_RANDOM) {
            if (model != config.delayModel) {
                config.delayModel = model
                return RpApplication.sp().edit().putInt(Constants.KEY_DELAY_MODEL, model).commit()
            }
            return false
        }
        return false
    }

    @JvmStatic
    fun supportFilterConversationTag(supportFilter: Boolean): Boolean {
        Logger.d(TAG, "supportFilterConversationTag : $supportFilter")
        if (config.supportFilterConversation != supportFilter) {
            config.supportFilterConversation = supportFilter
            return RpApplication.sp().edit().putBoolean(Constants.KEY_FILTER_CONVERSATION, supportFilter).commit()
        }
        return false
    }

    @JvmStatic
    fun isSupportFilterConversation() = config.supportFilterConversation

    @JvmStatic
    fun getFilterConversationTag() = config.filterConversationTags

    @JvmStatic
    fun updateFilterConversationTag(mutableList: MutableList<String>): Boolean {
        if (mutableList.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            config.filterConversationTags.clear()
            for (filter in mutableList) {
                config.filterConversationTags.add(filter)
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(filter)
                } else {
                    stringBuilder.append(Constants.SPLIT_POINT)
                    stringBuilder.append(filter)
                }
            }
            Logger.d(TAG, "updateFilterConversationTag : $stringBuilder")
            return RpApplication.sp().edit().putString(Constants.KEY_FILTER_CONVERSATION_DATA, stringBuilder.toString()).commit()
        }
        return false
    }


    @JvmStatic
    fun supportFilterPacketTag(supportFilter: Boolean): Boolean {
        Logger.d(TAG, "supportFilterPacketTag : $supportFilter")
        if (config.supportFilterPacket != supportFilter) {
            config.supportFilterPacket = supportFilter
            return RpApplication.sp().edit().putBoolean(Constants.KEY_FILTER_PACKET, supportFilter).commit()
        }
        return false
    }

    @JvmStatic
    fun isSupportFilterPacket() = config.supportFilterPacket

    @JvmStatic
    fun getFilterPacketTag() = config.filterPacketTags

    @JvmStatic
    fun updateFilterPacketTag(mutableList: MutableList<String>): Boolean {
        if (mutableList.isNotEmpty()) {
            val stringBuilder = StringBuilder()
            config.filterPacketTags.clear()
            for (filter in mutableList) {
                config.filterPacketTags.add(filter)
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(filter)
                } else {
                    stringBuilder.append(Constants.SPLIT_POINT)
                    stringBuilder.append(filter)
                }
            }
            Logger.d(TAG, "updateFilterPacketTag : $stringBuilder")
            return RpApplication.sp().edit().putString(Constants.KEY_FILTER_PACKET_DATA, stringBuilder.toString()).commit()
        }
        return false
    }
}
