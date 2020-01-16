package com.effective.android.wxrp.accessibility

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.NotificationUtil

/**
 * 4.3之后系统受到新的通知或者通知被删除时，会触发该service的回调方法
 * 4.4新增extras字段用于获取系统通知具体信息，之前的版本则需要通过反射区获取
 * 注意，需要在 "Settings > Security > Notification access"中，勾选NotificationTask
 */
class WxNotificationListenerService : NotificationListenerService() {

    private val TAG = "WxNotificationListenerService"

    /**
     * 系统通知被删掉后出发回调
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Logger.i(TAG, "onNotificationRemoved")
    }

    /**
     * 系统收到新的通知后出发回调
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Logger.i(TAG, "onNotificationPosted")
        val notification = sbn?.notification
        if (isWxNotification(sbn) && LocalizationHelper.isSupportNotification()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val extras = notification?.extras
                if (extras != null) {
                    val title = extras.getString(Notification.EXTRA_TITLE, "")
                    if (hasConversationKeyWords(title)) {
                        return
                    }

                    val content = extras.getString(Notification.EXTRA_TEXT, "")  //[6]yummylau:[微信红包]xxxxx
                    val message = content.split(":")
                    if (message.isNotEmpty() && message.size >= 2) {
                        if (message[1].contains(Constants.weChatPacketTip)) {
                            if (message[1].length > Constants.weChatPacketTip.length) {
                                val packetTip = message[1].substring(Constants.weChatPacketTip.length + 1)
                                if (!TextUtils.isEmpty(packetTip) && hasPacketKeyWords(packetTip)) {
                                    return
                                }
                                notification.contentIntent.send()
                            } else {
                                notification.contentIntent.send()
                            }
                        }
                    }
                }
            }
//            else {
//                val textList = NotificationUtil.getNotificationText(notification)
//                if (textList.isNotEmpty()) {
//                    for (content in textList) {
//                        if (!TextUtils.equals(content, Constants.weChatPacketTip)) {
//                            notification?.contentIntent?.send()
//                            break
//                        }
//                    }
//                }
//            }
        }
    }

    private fun hasPacketKeyWords(content: String): Boolean {
        val result = LocalizationHelper.isSupportFilterPacket() && isContainKeyWords(LocalizationHelper.getFilterPacketTag(), content)
        Logger.i(TAG, "hasPacketKeyWords  ： $result  当前通知（$content)")
        return result
    }

    private fun hasConversationKeyWords(content: String): Boolean {
        val result = LocalizationHelper.isSupportFilterConversation() && isContainKeyWords(LocalizationHelper.getFilterConversationTag(), content)
        Logger.i(TAG, "hasConversationKeyWords  ： $result  当前通知包含（$content)")
        return result
    }

    private fun isContainKeyWords(keyWords: List<String>, content: String): Boolean {
        var result = false
        keyWords.map {
            if (content.contains(it)) {
                result = true
            }
        }
        Logger.i(TAG, "isContainKeyWords result = $result")
        return result
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