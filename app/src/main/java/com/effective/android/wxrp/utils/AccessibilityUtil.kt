package com.effective.android.wxrp.utils

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.PendingIntent
import android.service.notification.StatusBarNotification
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object AccessibilityUtil {

    private const val TAG = "AccessibilityHelper"

    @JvmStatic
    fun performClick(nodeInfo: AccessibilityNodeInfo?): Boolean {
        Logger.i(TAG, "performClick")
        if (nodeInfo == null) {
            Logger.i(TAG, "performClick noInfo == null.")
            return false
        }
        if (nodeInfo.isClickable) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Logger.i(TAG, "performClick return true.")
            return true
        } else {
            performClick(nodeInfo.parent)
        }
        Logger.i(TAG, "performClick return false.")
        return false
    }

    @JvmStatic
    fun openNotification(accessibilityEvent: AccessibilityEvent, textFound: String): Boolean {
        Logger.i(TAG, "openNotification AccessibilityEvent")
        val texts = accessibilityEvent.text
        if (!texts.isEmpty()) {
            for (text in texts) {
                val content = text.toString()
                Logger.i(TAG, "onAccessibilityEvent text = $content")
                if (content.contains(textFound)) {
                    // 模拟打开通知栏消息
                    if (accessibilityEvent.parcelableData != null && accessibilityEvent.parcelableData is Notification) {
                        val notification = accessibilityEvent.parcelableData as Notification
                        val pendingIntent = notification.contentIntent
                        try {
                            pendingIntent.send()
                            return true
                        } catch (e: PendingIntent.CanceledException) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        }
        return false
    }

    @JvmStatic
    fun openNotification(sbn: StatusBarNotification, packageFound: String, textFound: String): Boolean {
        Logger.i(TAG, "openNotification  StatusBarNotification")
        val packageName = sbn.packageName
        if (packageName == packageFound) {
            val content = sbn.notification.extras.getString(Notification.EXTRA_TEXT)
            if (content != null) {
                if (content.contains(textFound)) {
                    val pendingIntent = sbn.notification.contentIntent
                    try {
                        pendingIntent.send()
                        return true
                    } catch (e: PendingIntent.CanceledException) {
                        e.printStackTrace()
                    }

                }
            }
        }
        return false
    }

    @JvmStatic
    fun clickView(nodeInfo: AccessibilityNodeInfo, viewID: String) {
        Logger.i(TAG, "clickView ")
        val nodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId(viewID)
        if (!nodeInfoList.isEmpty()) {
            if (nodeInfoList[0].isClickable) {
                nodeInfoList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Logger.i(TAG, "clickView success!")
                return
            }
        }
        Logger.i(TAG, "clickView failed!")
    }

    /** 返回主界面事件 */
    @JvmStatic
    fun performHome(service: AccessibilityService?) {
        if (service == null) {
            Logger.i(TAG, "WxAccessibilityService was killed!")
            return
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }

    /** 返回事件 */
    @JvmStatic
    internal fun performBack(service: AccessibilityService?) {
        if (service == null) {
            Logger.i(TAG, "WxAccessibilityService was killed!")
            return
        }
        Logger.i(TAG, "performBack")
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}
