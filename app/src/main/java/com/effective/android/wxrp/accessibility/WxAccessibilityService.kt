package com.effective.android.wxrp.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.version.VersionManager
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.ToolUtil

class WxAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "WxAccessibilityService"
        private var service: WxAccessibilityService? = null
        fun getService(): AccessibilityService? {
            return service
        }
    }

    private var accessibilityManager: WxAccessibilityManager? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {

        if (!VersionManager.runningPlus()) {
            return
        }

        if (!Config.switcher) {
            return
        }


        val eventType = accessibilityEvent.eventType
        val className = accessibilityEvent.className.toString()
        val rootNode = rootInActiveWindow

        Logger.i(TAG, "onAccessibilityEvent eventType = " + eventType + "className = " + className)
        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Logger.i(TAG, "窗口状态改变 className = $className")
                accessibilityManager?.dealWindowStateChanged(className, rootNode)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Logger.i(TAG, "窗口内容变化")
                accessibilityManager?.dealWindowContentChanged(rootNode)
            }
            else -> {
            }
        }
        rootNode?.recycle()
    }

    override fun onInterrupt() {
        Logger.i(TAG, "onInterrupt")
        ToolUtil.toast(this, "模拟操作 服务被中断")
    }

    override fun onServiceConnected() {
        Logger.i(TAG, "onServiceConnected")
        ToolUtil.toast(this, "模拟操作 服务已连接")
        service = this
        if (accessibilityManager == null) {
            accessibilityManager = WxAccessibilityManager("accessbility-handler-thread")
            accessibilityManager?.start()
        }
        super.onServiceConnected()
    }

    override fun onDestroy() {
        service = null
        super.onDestroy()
    }
}