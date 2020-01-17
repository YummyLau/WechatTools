package com.effective.android.wxrp.data

import android.text.TextUtils
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.Constants.weChatPacketTip
import com.effective.android.wxrp.data.sp.LocalizationHelper.getConfigName
import com.effective.android.wxrp.utils.Logger.i
import com.effective.android.wxrp.utils.ToolUtil

/**
 * 红包消息，用于处理会话或者通知栏
 */
class ConversationMessage(private val conversationName: String, parseContent: String) {

    private var sender //发送者
            : String = ""
    private var packet //微信红包标识，一般为 "[微信红包]"
            : String = ""
    private var message //如果存在 packet，则message为红包寄语，否则则为普通消息
            : String = ""

    /**
     * 一般而言，如果发送者不等于会话名，则当做该消息来自群聊
     * =。= 除非用户的昵称改成群聊的名字，极少，暂不兼容
     *
     * @return
     */
    private val isGroupChat: Boolean
        get() = !(!TextUtils.isEmpty(sender) && sender.endsWith(conversationName))

    /**
     * 是否是自己的消息
     * @return
     */
    private val isSelfMessage: Boolean
        get() = TextUtils.equals(sender, getConfigName())

    fun isClickMessage(): Boolean {
        i("MessageInfo  ： conversationName($conversationName) sender($sender) packet($packet) message($message) isGroupChat($isGroupChat)")
        if (ToolUtil.hasPacketTipWords(packet)) {
            if (!ToolUtil.hasConversationKeyWords(conversationName)) {
                if (ToolUtil.hasPassByGettingSetting(isSelfMessage, isGroupChat)) {
                    if (!ToolUtil.hasPacketKeyWords(message)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    init {
        if (!TextUtils.isEmpty(parseContent)) {
            val splits = parseContent.split(":").toTypedArray()
            var others: String
            if (splits.size == 1) {
                sender = conversationName
                others = parseContent
            } else {
                sender = splits[0].trim { it <= ' ' }
                others = splits[1].trim { it <= ' ' }
            }
            if (others.startsWith(weChatPacketTip)) {
                packet = weChatPacketTip
                message = if (others.length > packet!!.length) {
                    others.substring(packet!!.length)
                } else {
                    ""
                }
            } else {
                packet = ""
                message = others
            }
        }
    }
}