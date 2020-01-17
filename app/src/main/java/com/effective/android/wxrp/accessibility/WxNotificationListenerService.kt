package com.effective.android.wxrp.accessibility

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.Constants.statusByPluginError
import com.effective.android.wxrp.Constants.statusBySettingDoNotSupportPlugin
import com.effective.android.wxrp.data.ConversationMessage
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.version.VersionManager

/**
 * 4.3之后系统受到新的通知或者通知被删除时，会触发该service的回调方法
 * 4.4新增extras字段用于获取系统通知具体信息，之前的版本则需要通过反射区获取
 * 注意，需要在 "Settings > Security > Notification access"中，勾选NotificationTask
 */
class WxNotificationListenerService : NotificationListenerService() {

    private val tag = "通知栏服务"

    /**
     * 系统通知被删掉后出发回调
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Logger.i(tag, "onNotificationRemoved")
    }

    /**
     * 系统收到新的通知后出发回调
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Logger.i(tag, "onNotificationPosted")

        if (!VersionManager.runningPlus()) {
            Logger.i(tag, "onNotificationPosted - $statusByPluginError")
            return
        }

        if (!LocalizationHelper.isSupportPlugin()) {
            Logger.i(tag, "onNotificationPosted - $statusBySettingDoNotSupportPlugin")
            return
        }

        val notification = sbn?.notification
        if (isWxNotification(sbn) && LocalizationHelper.isSupportNotification()) {

            //minsdk支持5.0及以上，extras api 正常使用
            val extras = notification?.extras
            if (extras != null) {
                val title = extras.getString(Notification.EXTRA_TITLE, "")
                val content = extras.getString(Notification.EXTRA_TEXT, "")  //[6]yummylau:[微信红包]xxxxx
                val messageInfo = ConversationMessage(title, content)
                Logger.i(tag, "onNotificationPosted - content : $content")
                Logger.i(tag, "onNotificationPosted - title : $title")
                if (messageInfo.isClickMessage()) {
                    if (VersionManager.isClickedNewMessageList || VersionManager.isGotPacket) {
                        return
                    }
                    notification.contentIntent.send()
                }
            }
        }
    }


    /**
     * 当 NotificationListenerService 是可用的并且和通知管理器连接成功时回调
     */
    override fun onListenerConnected() {
        super.onListenerConnected()
        LocalizationHelper.supportNotification(true)
    }

    /**
     * 断开连接
     */
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        LocalizationHelper.supportNotification(false)
    }

    private fun isWxNotification(sbn: StatusBarNotification?): Boolean {
        if (sbn != null) {
            return sbn.packageName.contains(Constants.weChatPackageName)
        }
        return false
    }
}