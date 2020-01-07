package com.effective.android.wxrp.version

interface VersionInfo {

    fun weChatVersion(): String

    /**
     * class
     */

    fun launcherClass(): String

    fun packetReceiveClass(): String

    fun packetSendClass(): String

    fun packetPayClass(): String

    fun packetDetailClass(): String

    /**
     * id
     */

    fun packetDialogOpenId(): String             // 聊天页面 - 红包对话框 - 开


    fun packetDetailPostUserId(): String           // 红包详情页 - 发送者昵称
    fun packetDetailPostNumId(): String           // 红包详情页 - 红包金额

    fun chatPagerItemId(): String                //聊天界面 - 聊天列表 - 对话item
    fun chatPagerItemAvatatId(): String          // 聊天页面 - 聊天列表 - 红包item - 头像
    fun chatPagerItemPacketId(): String          // 聊天页面 - 聊天列表 - 红包item - 包括下面
    fun chatPagerItemPacketMessageId(): String   // 聊天页面 - 聊天列表 - 红包item - 祝福语比如说恭喜发财
    fun chatPagerItemPacketFlagId(): String       // 聊天页面 - 聊天列表 - 红包item - 底部微信红包
    fun chatPagerItemPacketTipId(): String       // 聊天页面 - 聊天列表 - 红包item - 红包状态比如说领取之后显示已领取
    fun chatPagerTitleId(): String               // 聊天页面 - 聊天列表 - 个人聊天则是昵称，群聊天则是群昵称

    fun homeChatListItemId(): String              // 首页列表 - 聊天会话 - item id
    fun homeChatListItemMessageId(): String        // 微信列表 每一个item中的文本id
    fun homeChatListItemTextId(): String         // 微信列表 每一个item中的会话名字

    fun homeUserPagerNickId():String            //我的页面，微信昵称
    fun homeUserPagerWeChatNumId():String          //我的页面，微信号

    fun homeTabTitleId():String                 //首页4个tab的标题，存在与微信tab，通讯录tab，发现tab，用于过滤没有必要的轮训

}