package com.effective.android.wxrp.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.Constants.statusByPluginError
import com.effective.android.wxrp.Constants.statusBySettingDoNotSupportPlugin
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.ToastUtil
import com.effective.android.wxrp.version.VersionManager
import com.lzf.easyfloat.EasyFloat

/**
 * 每一次打开插件设置，都是一条新的service
 */
class WxAccessibilityService : AccessibilityService() {

    private val tag = "无障碍服务"

    companion object {
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
            Logger.i(tag, "onAccessibilityEvent - $statusByPluginError")
            return
        }

        if (!LocalizationHelper.isSupportPlugin()) {
            Logger.i(tag, "onAccessibilityEvent - $statusBySettingDoNotSupportPlugin")
            return
        }

        val eventType = accessibilityEvent.eventType
        val className = accessibilityEvent.className.toString()
        val rootNode = rootInActiveWindow

        Logger.i(tag, "onAccessibilityEvent eventType =  $eventType  className = $className")

        when (eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Logger.i(tag, "窗口状态改变 className = $className")
                accessibilityManager?.dealWindowStateChanged(className, rootNode)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Logger.i(tag, "窗口内容变化")
                accessibilityManager?.dealWindowContentChanged(rootNode)
            }
        }
        rootNode?.recycle()
    }

    override fun onInterrupt() {
        Logger.i(tag, "onInterrupt")
        ToastUtil.toast(this, R.string.accessibility_service_interrupt)
        EasyFloat.hideAppFloat(getString(R.string.float_tag))
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Logger.i(tag, "onServiceConnected")
        ToastUtil.toast(this, R.string.accessibility_service_connected)
        service = this
        if (accessibilityManager == null) {
            accessibilityManager = WxAccessibilityManager("accessibility-handler-thread")
            accessibilityManager?.start()
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
        performGlobalAction(GLOBAL_ACTION_BACK)
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    override fun onDestroy() {
        service = null
        super.onDestroy()
    }
}