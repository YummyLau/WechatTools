package com.effective.android.wxrp.data

import android.text.TextUtils
import com.effective.android.wxrp.data.sp.LocalizationHelper.getConfigName
import com.effective.android.wxrp.utils.Logger.i
import com.effective.android.wxrp.utils.ToolUtil

/**
 * 红包消息，用于处理会话或者通知栏
 */
class DetailPageMessage(private val conversationName: String,
                        private val sender: String,
                        private val packetFlag: String,
                        private val packetMsg: String,
                        private val packetTip: String) {

    /**
     * 一般而言，如果发送者不等于会话名，则当做该消息来自群聊
     * =。= 除非用户的昵称改成群聊的名字，极少，暂不兼容
     *
     * @return
     */
    private val isGroupChat: Boolean
        get() = TextUtils.equals(conversationName, sender)

    /**
     * 是否是自己的消息
     * @return
     */
    private val isSelfMessage: Boolean
        get() = TextUtils.equals(sender, getConfigName())

    fun isClickMessage(): Boolean {
        i("DetailPageMessage  ： conversationName($conversationName) sender($sender) packetFlag($packetFlag) packetMsg($packetMsg) packetTip($packetTip)")
        if (ToolUtil.hasPacketTipWords(packetFlag, false)) {
            if (!TextUtils.isEmpty(packetTip)) {
                return false
            }
            if (!ToolUtil.hasConversationKeyWords(conversationName)) {
                if (ToolUtil.hasPassByGettingSetting(isSelfMessage, isGroupChat)) {
                    if (!ToolUtil.hasPacketKeyWords(packetMsg)) {
                        return true
                    }
                }
            }
        }
        return false
    }


}