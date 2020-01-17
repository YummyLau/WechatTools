package com.effective.android.wxrp.utils

import android.graphics.Rect
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.version.VersionManager
import java.util.ArrayList

object NodeUtil {

    private const val TAG = "NodeUtil"

    @JvmStatic
    fun containNode(node: AccessibilityNodeInfo,
                    nodes: ArrayList<AccessibilityNodeInfo>): Boolean {
        var result = false
        for (i in nodes.indices) {
            if (node == nodes[i]) {
                result = true
                break
            }
        }
        Logger.i(TAG, "isHasSameNodeInfo result = $result")
        return result
    }

    @JvmStatic
    fun getRectFromNodeInfo(nodeInfo: AccessibilityNodeInfo): Rect {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        return rect
    }

    /**
     * 是否是可点击的会话
     * 当且仅当在第一个tab "微信" 的时候，会话才支持点击
     */
    @JvmStatic
    fun isClickableConversationPage(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            return false
        }
        val tabTitle = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.homeTabTitleId())
        if (tabTitle.isEmpty()) {
            return false
        }
        val actionText = tabTitle[0].text
        val result = !TextUtils.isEmpty(actionText) && actionText.startsWith(Constants.tabTitleWeChat)
        Logger.i(TAG, "isClickableConversation ： $result, 当前tab($actionText)")
        return result
    }

    /**
     * 是否是微信首页第四个tab "我"
     * 如果是则尝试获取用户昵称用户信息
     */
    @JvmStatic
    fun isWeChatUserInfoPage(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            return false
        }
        val tabTitle = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.homeUserPagerNickId())               //会话item
        if (tabTitle.isEmpty()) {
            return false
        }
        val actionText = tabTitle[0].text
        val replaceable = LocalizationHelper.setConfigName(actionText.toString())
        if (replaceable) {
            ToastUtil.toast(RpApplication.instance(), "已获取昵称 $actionText")
        }
        Logger.i(TAG, "tryGetUserNamePage ： true, 当前userName($actionText)")
        return replaceable
    }

    /**
     * 判断当前页面是否是 "非微信首页"
     */
    @JvmStatic
    fun isNotWeChatHomePage(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            return false
        }
        val tabTitle = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.homeTabTitleId())
        if (tabTitle.isEmpty()) {
            return true
        }
        return false
    }
}