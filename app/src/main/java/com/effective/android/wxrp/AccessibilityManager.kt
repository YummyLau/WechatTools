package com.effective.android.wxrp

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import android.view.accessibility.AccessibilityNodeInfo
import com.effective.android.wxrp.services.WXAccessibilityService
import com.effective.android.wxrp.store.Config
import com.effective.android.wxrp.store.db.PacketRecord
import com.effective.android.wxrp.utils.AccessibilityUtil
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.NodeUtil
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.version.VersionManager
import java.util.ArrayList

class AccessibilityManager(string: String) : HandlerThread(string) {

    private val getPacketList: ArrayList<AccessibilityNodeInfo> = ArrayList()
    private val openPacketList: ArrayList<AccessibilityNodeInfo> = ArrayList()
    private var checkMsgHandler: Handler? = null
    private var isGotPacket = false

    companion object {
        private const val TAG = "AccessibilityManager"
        private const val MSG_ADD_PACKET = 0
        private const val MSG_OPEN_PACKET = 1
        private const val MSG_PACKET_DETAIL = 2
        private const val MSG_CLICK_NEW_MESSAGE = 3
        private const val MSG_RESET_GOT_PACKET = 4
    }

    override fun start() {
        super.start()
        checkMsgHandler = object : Handler(this.looper) {
            override fun handleMessage(msg: Message) {
                Logger.i(TAG, "handleMessage msg.what = " + msg.what)
                when (msg.what) {
                    MSG_ADD_PACKET -> {
                        val node = msg.obj
                        if (node != null && node is AccessibilityNodeInfo) {
                            if (getPacketList.isEmpty()) {
                                Logger.i(TAG, "sendGetPacketMsg " + node.toString())
                                getPacketList.add(node)
                                AccessibilityUtil.performClick(getPacketList.last())
                                getPacketList.removeAt(getPacketList.lastIndex)
                            } else {
                                if (!NodeUtil.containNode(node, getPacketList)) {
                                    Logger.i(TAG, "sendGetPacketMsg " + node.toString())
                                    getPacketList.add(node)
                                    sortGetPacketList()
                                    AccessibilityUtil.performClick(getPacketList.last())
                                    getPacketList.removeAt(getPacketList.lastIndex)
                                }
                            }
                        }
                    }

                    MSG_OPEN_PACKET -> {
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
                    MSG_PACKET_DETAIL -> {
                        val node = msg.obj
                        if (node != null && node is PacketRecord) {
                            node.time = System.currentTimeMillis()
                            RpApplication.PACKET_REPOSITORY().insertPacket(node)
                        }
                    }

                    MSG_CLICK_NEW_MESSAGE -> {
                        VersionManager.isClickedNewMessageList = false
                    }

                    MSG_RESET_GOT_PACKET -> {
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
        var delayedTime = Config.getDelayTime(false)
        if (delayedTime > 0) {
            delayedTime /= 2
        }
        sendHandlerMessage(MSG_ADD_PACKET, delayedTime, nodeInfo)
    }


    /**
     * 添加打开红包，用于点击开打开红包
     */
    private fun sendOpenPacketMsg(nodeInfo: AccessibilityNodeInfo) {
        var delayedTime = Config.getDelayTime(false)
        if (delayedTime > 0) {
            delayedTime = delayedTime / 2
        }
        sendHandlerMessage(MSG_OPEN_PACKET, delayedTime, nodeInfo)
    }


    private fun sendPacketRecordMsg(packetRecord: PacketRecord?) {
        if (packetRecord == null) {
            return
        }
        sendHandlerMessage(MSG_PACKET_DETAIL, 0, packetRecord)
    }

    private fun sendClickedNewMessageMsg() {
        sendHandlerMessage(MSG_CLICK_NEW_MESSAGE, 500)
    }

    private fun sendResetGotPacketMsg() {
        sendHandlerMessage(MSG_RESET_GOT_PACKET, 500)
    }


    /**
     * 排序列表
     */
    fun sortGetPacketList() {
        if (getPacketList.size == 1) {
            return
        }
        val tempGetPacketList = ArrayList<AccessibilityNodeInfo>()
        val nodeInfosBottom = IntArray(getPacketList.size)
        val nodeInfosIndex = IntArray(getPacketList.size)
        for (i in getPacketList.indices) {
            nodeInfosBottom[i] = NodeUtil.getRectFromNodeInfo(getPacketList[i]).bottom
            nodeInfosIndex[i] = i
            tempGetPacketList.add(getPacketList[i])
        }
        getPacketList.clear()
        ToolUtil.insertSort(nodeInfosBottom, nodeInfosIndex)
        for (i in tempGetPacketList.indices) {
            getPacketList.add(tempGetPacketList[nodeInfosIndex[i]])
            Logger.i(TAG, "sortGetPacketList nodeInfoBottom[" + i + "] = "
                    + NodeUtil.getRectFromNodeInfo(getPacketList[i]).bottom)
        }
    }


    fun dealWindowStateChanged(className: String, rootNode: AccessibilityNodeInfo?) {
        Logger.i(TAG, "dealWindowStateChanged")
        if (rootNode == null) {
            Logger.i(TAG, "dealWindowStateChanged-rootNode : null")
            return
        }
        when (className) {
            //如果当前是聊天窗口
            VersionManager.launcherClass() -> {
                Logger.i(TAG, "dealWindowStateChanged : 当前在首页")

                if (tryGetUserNamePage(rootNode)) {
                    return
                }

                if (filterHomeTabPage(rootNode)) {
                    if (Config.isOpenGetSelfPacket() && VersionManager.currentSelfPacketStatus == VersionManager.W_openedPayStatus) {
                        VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_intoChatDialogStatus)
                        getPacket(rootNode, true)
                    } else {
                        getPacket(rootNode, false)
                    }
                }
            }

            //红包页面
            VersionManager.packetReceiveClass() -> {
                Logger.i(TAG, "dealWindowStateChanged className: 当前已打开红包")
                if (Config.isOpenGetSelfPacket() && VersionManager.currentSelfPacketStatus == VersionManager.W_intoChatDialogStatus) {
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
                Logger.i(TAG, "dealWindowStateChanged : 当前在红包发送页面")
                if (VersionManager.currentSelfPacketStatus <= VersionManager.W_otherStatus) {
                    VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_openedPacketSendStatus)
                }
            }


            //红包支付页面
            VersionManager.packetPayClass() -> {
                Logger.i(TAG, "dealWindowStateChanged : 当前在红包支付页面")
                if (VersionManager.currentSelfPacketStatus == VersionManager.W_openedPacketSendStatus) {
                    VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_openedPayStatus)
                }
            }


            //红包详情页
            VersionManager.packetDetailClass() -> {
                Logger.i(TAG, "dealWindowStateChanged : 当前在红包详情页")
                if (VersionManager.currentSelfPacketStatus != VersionManager.W_otherStatus) {
                    AccessibilityUtil.performBack(WXAccessibilityService.getService())
                    VersionManager.setCurrentSelfPacketStatusData(VersionManager.W_otherStatus)
                }
                if (isGotPacket) {
                    sendPacketRecordMsg(getPacketRecord(rootNode))
                    //写入所抢的服务
                    AccessibilityUtil.performBack(WXAccessibilityService.getService())
                    isGotPacket = false
                }
            }

        }
    }

    private fun getPacketRecord(rootNode: AccessibilityNodeInfo?): PacketRecord? {
        Logger.i(TAG, "getPacketRecord")
        if (rootNode == null) {
            Logger.i(TAG, "getPakcetRecord-rootNode : null")
        } else {
            val postUser = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.packetDetailPostUserId())
            val number = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.packetDetailPostNumId())
            if (!postUser.isEmpty() && !number.isEmpty()) {
                val record = PacketRecord()
                record.num = number[0]?.text.toString().toFloat()
                record.postUser = postUser[0]?.text.toString()
                return record
            }
        }
        return null
    }

    /**
     * 如果在聊天会话列表，则判断当前是否需要点击消息
     * 如果在聊天页面，则判断是否需要获取红包
     */
    fun dealWindowContentChanged(rootNode: AccessibilityNodeInfo?) {
        Logger.i(TAG, "dealWindowContentChanged")
        if (rootNode == null) {
            Logger.i(TAG, "dealWindowStateChanged-rootNode : null")
            return
        }

        if (tryGetUserNamePage(rootNode)) {
            return
        }

        if (openPacket(rootNode)) {
            isGotPacket = true
            VersionManager.isClickedNewMessageList = false
            VersionManager.isGotPacket = false
        }

        //如果不是首页，则默认不需要点击会话或者获取红包
        //只有首页第一个tab-微信才需要检测点击
        if (isClickableConversation(rootNode)) {
            if (clickConversation(rootNode)) {
                VersionManager.isClickedNewMessageList = true
                sendClickedNewMessageMsg()
                return
            }
        }

        //只有聊天详情页才需要查询红包
        if (filterHomeTabPage(rootNode)) {
            if (getPacket(rootNode, false)) {
                VersionManager.isGotPacket = true
                sendResetGotPacketMsg()
                return
            }
        }
    }

    fun tryGetUserNamePage(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            return false
        }
        val tabTitle = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.homeUserPagerNickId())               //会话item
        if (tabTitle.isEmpty()) {
            return false
        }
        val actionText = tabTitle[0].text
        val replaceable = Config.setUserWxName(actionText.toString())
        if (replaceable) {
            ToolUtil.toast(RpApplication.INSTANCE(), "已获取昵称 $actionText")
        }
        Logger.i(TAG, "tryGetUserNamePage ： true, 当前userName($actionText)")
        return replaceable
    }

    fun filterHomeTabPage(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            return false
        }
        val tabTitle = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.homeTabTitleId())               //会话item
        if (tabTitle.isEmpty()) {
            return true
        }
        return false
    }

    fun isClickableConversation(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) {
            return false
        }
        val tabTitle = rootNode.findAccessibilityNodeInfosByViewId(VersionManager.homeTabTitleId())               //会话item
        if (tabTitle.isEmpty()) {
            return false
        }
        val actionText = tabTitle[0].text
        val result = !TextUtils.isEmpty(actionText) && actionText.startsWith(VersionManager.TEXT_TAB_TITLE_WEIXIN)
        Logger.i(TAG, "isClickableConversation ： $result, 当前tab($actionText)")
        return result
    }


    fun hasGotPacketTip(tipList: List<AccessibilityNodeInfo>, currentIndex: Int): Boolean {
        if (tipList.isEmpty() || tipList.size <= currentIndex) {
            return false
        }
        val actionText = tipList[currentIndex].text
        var result = !TextUtils.isEmpty(actionText) && !actionText.isEmpty()
        Logger.i(TAG, "hasGotPacketTip($currentIndex) ： $result, 当前红包提示($actionText)")
        return result
    }


    /**
     * 是否是群节点
     */
    fun isGroupNode(pageTitle: List<AccessibilityNodeInfo>): Boolean {
        if (pageTitle.isEmpty()) {
            return false
        }
        val title = pageTitle[0].text.toString()
        val result = !TextUtils.isEmpty(title) && title.contains("(") && title.contains(")")
        Logger.i(TAG, "isGroupNode ： $result , 会话名称为$title")
        return result
    }

    /**
     * 适用于聊天会话，聊天对话
     */
    fun isSelfNode(avatarList: List<AccessibilityNodeInfo>, currentIndex: Int): Boolean {
        if (Config.getUserWxName().isEmpty() || avatarList.isEmpty() || avatarList.size <= currentIndex) {
            return false
        }
        val contentDescription = avatarList[currentIndex].contentDescription
        val result = contentDescription.contains(Config.getUserWxName())
        Logger.i(TAG, "isSelfNode($currentIndex) ： $result , 节点名称为$contentDescription")
        return result
    }


    /**
     * 适用于聊天会话，聊天对话
     * 过滤特定节点，如果包含关键字的话
     */
    fun handleKeyWords(nodes: List<AccessibilityNodeInfo>, currentIndex: Int): Boolean {
        if (nodes.isEmpty() || nodes.size <= currentIndex) {
            return false
        }
        val packetText = nodes[currentIndex].text.toString()
        val result = Config.isOpenFilterTag() && isContainKeyWords(Config.filterTags, packetText)
        Logger.i(TAG, "handleKeyWords($currentIndex)  ： $result  当前节点包含（$packetText)")
        return result
    }


    /**
     * 是否包含某些关键字
     */
    private fun isContainKeyWords(keyWords: List<String>, content: String): Boolean {
        var result = false
        keyWords.map {
            if (content.contains(it)) {
                result = true
            }
        }
        Logger.i(TAG, "isContainKeyWords result = $result")
        return result
    }

    private fun isRedPacketNode(messageList: List<AccessibilityNodeInfo>, currentIndex: Int, flag: String = VersionManager.TEXT_WX_PACKET): Boolean {
        if (messageList.isEmpty() || messageList.size <= currentIndex) {
            return false
        }
        val text = messageList[currentIndex].text.toString()
        val result = !TextUtils.isEmpty(text) && text.contains(flag)
        Logger.i(TAG, "isRedPacketNode result = $result text($text)")
        return result
    }


    /**
     * 点击会话
     */
    private fun clickConversation(nodeInfo: AccessibilityNodeInfo?): Boolean {
        Logger.i(TAG, "clickConversation")
        if (nodeInfo == null) {
            Logger.i(TAG, "clickConversation ： 点击消息为 null")
            return false
        }
        var result = false
        val dialogList = nodeInfo.findAccessibilityNodeInfosByViewId(VersionManager.homeChatListItemId())               //会话item
        val messageTextList = nodeInfo.findAccessibilityNodeInfosByViewId(VersionManager.homeChatListItemMessageId())  //会话内容
//        val TitleList = nodeInfo.findAccessibilityNodeInfosByViewId(VersionManager.homeChatListItemTextId)          //会话名字
        if (!dialogList.isEmpty()) {
            for (i in dialogList.indices.reversed()) {

                if (isRedPacketNode(messageTextList, i)) {

                    //是否需要过滤关键字
                    if (handleKeyWords(messageTextList, i)) {
                        continue
                    }

                    dialogList[i].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    result = true
                }
            }
        }
        Logger.i(TAG, "clickConversation ： 是否模拟点击进入聊天页面（$result)")
        return result
    }

    /**
     * 获取红包列表
     * 兼容是否抢自己的红包，兼容是否有关键字
     */
    private fun getPacket(rootNote: AccessibilityNodeInfo?, isSelfPacket: Boolean): Boolean {
        Logger.i(TAG, "getPacket")
        if (rootNote == null) {
            Logger.i(TAG, "getPacket rootNode == null")
            return false
        }
        var result = false
        val avatarList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemAvatatId())
        val tipList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketTipId())
        val messageList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketMessageId())
        val pageTitle = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerTitleId())
        val packetList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketId())
        val flagList = rootNote.findAccessibilityNodeInfosByViewId(VersionManager.chatPagerItemPacketFlagId())

        if (!packetList.isEmpty()) {
            for (i in packetList.indices.reversed()) {

                //过滤不是红包的
                if (!isRedPacketNode(flagList, i, VersionManager.TEXT_WX_PACKET_WITHOUT_SPL)) {
                    continue
                }

                //如果是自己的节点
                //如果当前节点不是群聊，则过滤
                //如果当前节点不没有开启强群的红包，则过滤
                if (isSelfNode(avatarList, i) && (!isGroupNode(pageTitle) || !Config.isOpenGetSelfPacket())) {
                    continue
                }

                //过滤已经抢过的，已过期等等
                if (hasGotPacketTip(tipList, i)) {
                    continue
                }

                //过滤关键词
                if (handleKeyWords(messageList, i)) {
                    continue
                }

                sendGetPacketMsg(packetList[i])
                result = true
            }
        }
        Logger.i(TAG, "getPacket result = $result")
        return result
    }

    /**
     * 打开红包，当前已经显示了一个红包窗口
     */
    private fun openPacket(rootNode: AccessibilityNodeInfo?): Boolean {
        Logger.i(TAG, "openPacket")

        //如果当前节点存在红包，则遍历寻找"开"
        var result = false
        val packetList = rootNode!!.findAccessibilityNodeInfosByViewId(VersionManager.packetDialogOpenId())
        if (!packetList.isEmpty()) {
            val item = packetList[0]
            if (item.isClickable) {
                sendOpenPacketMsg(item)
                result = true
            }
        }
        Logger.i(TAG, "openPacket result = $result")
        return result
    }
}