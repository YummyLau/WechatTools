package com.effective.android.wxrp.utils

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import java.util.ArrayList

object NodeUtil {

    private const val TAG = "NodeUtil"

    @JvmStatic
    fun containNode(nodeInfo: AccessibilityNodeInfo,
                    nodeInfos: ArrayList<AccessibilityNodeInfo>): Boolean {
        var result = false
        for (i in nodeInfos.indices) {
            if (nodeInfo == nodeInfos[i]) {
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

}