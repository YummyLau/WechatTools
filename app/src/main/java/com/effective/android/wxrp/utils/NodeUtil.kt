package com.effective.android.wxrp.utils

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
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

}