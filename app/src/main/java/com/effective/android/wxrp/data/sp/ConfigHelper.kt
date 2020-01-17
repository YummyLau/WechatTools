package com.effective.android.wxrp.data.sp

import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.NotificationUtil
import com.effective.android.wxrp.utils.ToastUtil
import com.effective.android.wxrp.utils.ToolUtil

object ConfigHelper {

    private const val tag = "ConfigHelper"

    @JvmStatic
    val updater = object : ConfigUpdate {

        override fun supportNotification(support: Boolean) {
            var result = support
            if (support && !NotificationUtil.isNotificationListenersEnabled(RpApplication.instance())){
                result = false
                ToastUtil.toast(RpApplication.instance(),"开启通知栏功能失败，请确保所有权限都开启.")
            }
            LocalizationHelper.supportNotification(result)
            Logger.i(tag,"supportNotification : $support")
            for (listener in listeners) {
                listener.onSupportNotification(result)
            }
        }

        override fun supportFloat(support: Boolean) {
            LocalizationHelper.supportFloat(support)
            Logger.i(tag,"supportFloat : $support")
            for (listener in listeners) {
                listener.onSupportFloat(support)
            }
        }

        override fun supportPlugin(support: Boolean) {
            LocalizationHelper.supportPlugin(support)
            Logger.i(tag,"supportPlugin : $support")
            for (listener in listeners) {
                listener.onSupportPlugin(support)
            }
        }

        override fun supportGetSelfPacket(support: Boolean) {
            LocalizationHelper.supportGettingSelfPacket(support)
            Logger.i(tag,"supportGetSelfPacket : $support")
            for (listener in listeners) {
                listener.onSupportGetSelfPacket(support)
            }
        }

        override fun supportFilterPacket(support: Boolean) {
            LocalizationHelper.supportFilterPacketTag(support)
            Logger.i(tag,"supportFilterPacket : $support")
            for (listener in listeners) {
                listener.onSupportFilterPacket(support)
            }
        }

        override fun supportFilterConversation(support: Boolean) {
            LocalizationHelper.supportFilterConversationTag(support)
            Logger.i(tag,"supportFilterConversation : $support")
            for (listener in listeners) {
                listener.onSupportFilterConversation(support)
            }
        }
    }

    private val listeners: MutableList<ConfigChangeListener> = mutableListOf();

    @JvmStatic
    fun addListener(listener: ConfigChangeListener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ConfigChangeListener) {
        if (!listeners.contains(listener)) {
            return
        }
        listeners.remove(listener)
    }
}