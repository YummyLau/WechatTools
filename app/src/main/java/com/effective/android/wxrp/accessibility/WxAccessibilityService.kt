package com.effective.android.wxrp.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.data.sp.SettingConfig
import com.effective.android.wxrp.version.VersionManager
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.ToolUtil
import com.lzf.easyfloat.EasyFloat

/**
 * 每一次打开插件设置，都是一条新的service
 */
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

        if (!LocalizationHelper.isSupportPlugin()) {
            return
        }

        val eventType = accessibilityEvent.eventType
        val className = accessibilityEvent.className.toString()
        val rootNode = rootInActiveWindow

        Logger.i(TAG, "onAccessibilityEvent eventType =  $eventType  className = $className")

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
        ToolUtil.toast(this, R.string.accessibility_service_interrupt)
        EasyFloat.hideAppFloat(getString(R.string.float_tag))
    }

    override fun onServiceConnected() {
        Logger.i(TAG, "onServiceConnected")
        ToolUtil.toast(this, R.string.accessibility_service_connected)
        service = this
        if (accessibilityManager == null) {
            accessibilityManager = WxAccessibilityManager("accessibility-handler-thread")
            accessibilityManager?.start()
        }
        super.onServiceConnected()
    }

    override fun onDestroy() {
        service = null
        super.onDestroy()
    }
}