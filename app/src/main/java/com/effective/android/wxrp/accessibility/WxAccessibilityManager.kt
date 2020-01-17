package com.effective.android.wxrp.accessibility

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.ConversationMessage
import com.effective.android.wxrp.data.DetailPageMessage
import com.effective.android.wxrp.data.db.PacketRecord
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.*
import com.effective.android.wxrp.version.VersionManager
import java.util.*

class WxAccessibilityManager(string: String) : HandlerThread(string) {

    private val getPacketList: ArrayList<AccessibilityNodeInfo> = ArrayList()
    private val openPacketList: ArrayList<AccessibilityNodeInfo> = ArrayList()
    private var checkMsgHandler: Handler? = null
    private var isGotPacket = false
    private val tag = "无障碍服务管理者"


    override fun start() {
        super.start()
        checkMsgHandler = object : Handler(this.looper) {
            override fun handleMessage(msg: Message) {
                Logger.i(tag, "handleMessage msg.what = " + msg.what)
                when (msg.what) {
                    Constants.MSG_ADD_PACKET -> {
                        val node = msg.obj
                        if (node != null && node is AccessibilityNodeInfo) {
                            if (getPacketList.isEmpty()) {
                                Logger.i(tag, "sendGetPacketMsg $node")
                                getPacketList.add(node)
                                AccessibilityUtil.performClick(getPacketList.last())
                                getPacketList.removeAt(getPacketList.lastIndex)
                            } else {
                                if (!NodeUtil.containNode(node, getPacketList)) {
                                    Logger.i(tag, "sendGetPacketMsg $node")
                                    getPacketList.add(node)
                                    sortGetPacketList()
                                    AccessibilityUtil.performClick(getPacketList.last())
                                    getPacketList.removeAt(getPacketList.lastIndex)
                                }
                            }
                        }
                    }

                    Constants.MSG_OPEN_PACKET -> {
                        val node = msg.obj
                        if (node != null && node is AccessibilityNodeInfo) {
                            if (openPacketList.isEmpty()) {
                                openPacketList.add(node)
                                AccessibilityUtil.performClick(openPacketList.last())
                                openPacketList.removeAt(openPacketList.lastIndex)
                            } else {
                                if (!NodeUtil.containNode(node, openPacketList)) {
                                    openPacketList.add(node)
                                    AccessibilityUtil.performClick(openPacketList.last())
                                    openPacketList.removeAt(openPacketList.lastIndex)
                                }
                            }
                        }
                    }
                    Constants.MSG_PACKET_DETAIL -> {
                        val node = msg.obj
                        if (node != null && node is PacketRecord) {
                            node.time = System.currentTimeMillis()
                            RpApplication.repository().insertPacket(node)
                        }
                    }

                    Constants.MSG_CLICK_NEW_MESSAGE -> {
                        VersionManager.isClickedNewMessageList = false
                    }

                    Constants.MSG_RESET_GOT_PACKET -> {
                        VersionManager.isGotPacket = false
                    }
                }
            }
        }
    }

    private fun sendHandlerMessage(what: Int, delayedTime: Int, obj: Any? = null) {
        if (checkMsgHandler != null) {
            val msg = checkMsgHandler!!.obtainMessage()
            msg.what = what
            msg!!.obj = obj
            checkMsgHandler!!.sendMessageDelayed(msg, delayedTime.toLong())
        }
    }

    private fun sendGetPacketMsg(nodeInfo: AccessibilityNodeInfo) {
        var delayedTime = LocalizationHelper.getDelayTime(false)
        if (delayedTime > 0) {
            delayedTime /= 2
        }
        sendHandlerMessage(Constants.MSG_ADD_PACKET, delayedTime, nodeInfo)
    }


    /**
     * 添加打开红包，用于点击开打开红包
     */
    private fun sendOpenPacketMsg(nodeInfo: AccessibilityNodeInfo) {
        var delayedTime = LocalizationHelper.getDelayTime(false)
        if (delayedTime > 0) {
            delayedTime /= 2
        }
        sendHandlerMessage(Constants.MSG_OPEN_PACKET, delayedTime, nodeInfo)
    }


    private fun sendPacketRecordMsg(packetRecord: PacketRecord?) {
        if (packetRecord == null) {
            return
        }
        sendHandlerMessage(Constants.MSG_PACKET_DETAIL, 0, packetRecord)
    }

    private fun sendClickedNewMessageMsg() {
        sendHandlerMessage(Constants.MSG_CLICK_NEW_MESSAGE, 500)
    }

    private fun sendResetGotPacketMsg() {
        sendHandlerMessage(Constants.MSG_RESET_GOT_PACKET, 500)
    }


    /**
     * 排序列表
     */
    fun sortGetPacketList() {
        if (getPacketList.size == 1) {
            return
        }
        val tempGetPacketList = ArrayList<AccessibilityNodeInfo>()
        val nodeInfoBottom = IntArray(getPacketList.size)
        val nodeInfoIndex = IntArray(getPacketList.size)
        for (i in getPacketList.indices) {
            nodeInfoBottom[i] = NodeUtil.getRectFromNodeInfo(getPacketList[i]).bottom
            nodeInfoIndex[i] = i
            tempGetPacketList.add(getPacketList[i])
        }
        getPacketList.clear()
        ToolUtil.insertSort(nodeInfoBottom, nodeInfoIndex)
        for (i in tempGetPacketList.indices) {
            getPacketList.add(tempGetPacketList[nodeInfoIndex[i]])
            Logger.i(tag, "sortGetPacketList nodeInfoBottom[" + i + "] = "
                    + NodeUtil.getRectFromNodeInfo(getPacketList[i]).bottom)
        }
    }


    fun dealWindowStateChanged(className: String, rootNode: AccessibilityNodeInfo?) {
        Logger.i(tag, "dealWindowStateChanged")
        if (rootNode == null) {
            Logger.i(tag, "dealWindowStateChanged-rootNode : null")
            return
        }
        when (className) {
            //如果当前是首页
            VersionManager.launcherClass() -> {
                Logger.i(tag, "dealWindowStateChanged : 当前在首页")

                //如果是首页 "微信" tab
                if (NodeUtil.isNotWeChatHomePage(rootNode)) {
                    if (LocalizationHelper.isSupportGettingSelfPacket() && VersionManager.currentSelfPacketStatus == VersionManager.W_openedPayStatus) {
                        VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_intoChatDialogStatus)
                        getPacket(rootNode, true)
                    } else {
                        getPacket(rootNode, false)
                    }
                    return
                }

                //如果首页 "我"tab，尝试更新微信昵称
                if (NodeUtil.isWeChatUserInfoPage(rootNode)) {
                    return
                }
            }

            //红包页面
            VersionManager.packetReceiveClass() -> {
                Logger.i(tag, "dealWindowStateChanged className: 当前已打开红包")
                if (LocalizationHelper.isSupportGettingSelfPacket() && VersionManager.currentSelfPacketStatus == VersionManager.W_intoChatDialogStatus) {
                    if (openPacket(rootNode)) {
                        VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_gotSelfPacketStatus)
                    }
                } else {
                    if (openPacket(rootNode)) {
                        isGotPacket = true
                    }
                }
                VersionManager.isClickedNewMessageList = false
                VersionManager.isGotPacket = false
            }

            //红包发送页面
            VersionManager.packetSendClass() -> {
                Logger.i(tag, "dealWindowStateChanged : 当前在红包发送页面")
                if (VersionManager.currentSelfPacketStatus <= VersionManager.W_otherStatus) {
                    VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_openedPacketSendStatus)
                }
            }


            //红包支付页面
            VersionManager.packetPayClass() -> {
                Logger.i(tag, "dealWindowStateChanged : 当前在红包支付页面")
                if (VersionManager.currentSelfPacketStatus == VersionManager.W_openedPacketSendStatus) {
                    VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_openedPayStatus)
                }
            }


            //红包详情页
            VersionManager.packetDetailClass() -> {
                Logger.i(tag, "dealWindowStateChanged : 当前在红包详情页")
                if (VersionManager.currentSelfPacketStatus != VersionManager.W_otherStatus) {
                    AccessibilityUtil.performBack(WxAccessibilityService.getService())
                    VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_otherStatus)
                }
                if (isGotPacket) {
                    sendPacketRecordMsg(getPacketRecord(rootNode))
                    //写入所抢的服务
                    AccessibilityUtil.performBack(WxAccessibilityService.getService())
                    isGotPacket = false
                }
            }

        }
    }


    /**
     * 页面内容变动回调
     */
    fun dealWindowContentChanged(rootNode: AccessibilityNodeInfo?) {
        Logger.i(tag, "dealWindowContentChanged")
        if (rootNode == null) {
            Logger.i(tag, "dealWindowStateChanged-rootNode : null")
            return
        }

        //只有聊天详情页才需要查询红包
        if (NodeUtil.isNotWeChatHomePage(rootNode)) {

            //如果是红白页面，则尝试点击 "开"
            if (openPacket(rootNode)) {
                isGotPacket = true
                VersionManager.isClickedNewMessageList = false
                VersionManager.isGotPacket = false
                return
            }


            //如果是聊天详情页，则尝试点击 "红包" item
            if (getPacket(rootNode, false)) {
                VersionManager.isGotPacket = true
                sendResetGotPacketMsg()
                return
            }

        } else {

            //如果是首页 "微信" tab，则尝试获取有效红包会话
            if (NodeUtil.isClickableConversationPage(rootNode)) {
                if (tryClickConversation(rootNode)) {
                    VersionManager.isClickedNewMessageList = true
                    sendClickedNewMessageMsg()
                    return
                }
            }

            //如果首页 "我"tab，尝试更新微信昵称
            if (NodeUtil.isWeChatUserInfoPage(rootNode)) {
                return
            }
        }
    }


    private fun getPacketRecord(rootNode: AccessibilityNodeInfo?): PacketRecord? {
        Logger.i(tag, "getPacketRecord")
        if (rootNode == null) {
            Logger.i(tag, "getPakcetRecord-rootNode : null")
        } else {
            val postUser = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.packetDetailPostUserId())
            val number = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.packetDetailPostNumId())
            if (postUser.isNotEmpty() && number.isNotEmpty()) {
                val record = PacketRecord()
                record.num = number[0]?.text.toString().toFloat()
                record.postUser = postUser[0]?.text.toString()
                return record
            }
        }
        return null
    }



    /**
     * 首页第一个tab "微信"
     * 判断一个会话是否需要被自动点击
     * 满足条件：  红包节点 && 不被会话过滤 && 不被关键字过滤
     */
    private fun tryClickConversation(nodeInfo: AccessibilityNodeInfo?): Boolean {
        Logger.i(tag, "clickConversation")
        if (nodeInfo == null) {
            Logger.i(tag, "clickConversation ： 点击消息为 null")
            return false
        }
        var result = false
        val dialogList = nodeInfo.findAccessibilityNodeInfosByViewId(VersionManager.homeChatListItemId())               //检索会话列表

        if (dialogList.isNotEmpty()) {
            for (i in dialogList.indices.reversed()) {

                //会话内容
                val messageTextList = dialogList[i].findAccessibilityNodeInfosByViewId(VersionManager.homeChatListItemMessageId())
                var messageTextString = ""
                if (messageTextList.isNotEmpty()) {
                    messageTextString = messageTextList[0].text.toString()
                }

                //会话名字
                val titleList = dialogList[i].findAccessibilityNodeInfosByViewId(VersionManager.homeChatListItemTextId())
                var titleString = ""
                if (titleList.isNotEmpty()) {
                    titleString = titleList[0].text.toString()
                }

                val messageInfo = ConversationMessage(titleString, messageTextString);
                if (messageInfo.isClickMessage()) {
                    dialogList[i].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    result = true
                } else {
                    continue
                }
            }
        }
        Logger.i(tag, "clickConversation ： 是否模拟点击进入聊天页面（$result)")
        return result
    }

    /**
     * 获取红包列表
     * 兼容是否抢自己的红包，兼容是否有关键字
     */
    private fun getPacket(rootNote: AccessibilityNodeInfo?, isSelfPacket: Boolean): Boolean {
        Logger.i(tag, "getPacket")
        if (rootNote == null) {
            Logger.i(tag, "getPacket rootNode == null")
            return false
        }
        var result = false
        val avatarList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemAvatatId())

        val pageTitle = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerTitleId())
        var title = ""
        if (title.isNotEmpty()) {
            title = pageTitle[0].text.toString()
        }

        val packetList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketId())


        if (packetList.isNotEmpty()) {
            for (i in packetList.indices.reversed()) {

                var sender = ""
                if (avatarList.isNotEmpty() && i < avatarList.size) {
                    sender = avatarList[i].contentDescription.toString()
                    if (sender.isNotEmpty() && sender.endsWith("头像")) {
                        sender = sender.substring(0, sender.length - 2)
                    }
                }

                val flagList = packetList[i].findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketFlagId())
                var flag = ""
                if (flagList.isNotEmpty()) {
                    flag = flagList[0].text.toString()
                }

                val tipList = packetList[i].findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketTipId())
                var tipText = ""
                if (tipList.isNotEmpty()) {
                    tipText = tipList[0].text.toString()
                }

                val messageList = packetList[i].findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketMessageId())
                var message = ""
                if (messageList.isNotEmpty()) {
                    message = messageList[0].text.toString()
                }

                val detailPageMessage = DetailPageMessage(title, sender, flag, message, tipText)

                if (detailPageMessage.isClickMessage()) {
                    sendGetPacketMsg(packetList[i])
                    result = true
                } else {
                    continue
                }
            }
        }
        Logger.i(tag, "getPacket result = $result")
        return result
    }

    /**
     * 打开红包，当前已经显示了一个红包窗口
     */
    private fun openPacket(rootNode: AccessibilityNodeInfo?): Boolean {
        Logger.i(tag, "openPacket")

        //如果当前节点存在红包，则遍历寻找"开"
        var result = false
        val packetList = rootNode!!.findAccessibilityNodeInfosByViewId(VersionManager.packetDialogOpenId())
        if (packetList.isNotEmpty()) {
            val item = packetList[0]
            if (item.isClickable) {
                sendOpenPacketMsg(item)
                result = true
            }
        }
        Logger.i(tag, "openPacket result = $result")
        return result
    }
}