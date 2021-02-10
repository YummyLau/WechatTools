package com.effective.android.wxrp.version

/**
 * 微信 8.0版本以上
 * created by yummylau on 2020/09/29
 */
class Version800 : VersionInfo {

    companion object {

        const val VERSION = "8.0.0"

        /**
         * 类名
         */
        const val CLASS_LAUNCHER = "com.tencent.mm.ui.LauncherUI"                                             // 微信 聊天列表、聊天窗口（单聊私聊都是）
        const val CLASS_PACKET_RECEIVE = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI"     // 微信 红包“開”的窗口
        const val CLASS_PACKET_SEND = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI"               // 微信 自己发红包的窗口
        const val CLASS_PACKET_PAY = "com.tencent.mm.plugin.wallet_core.ui.l"                                 // 微信 自己发红包输入密码的界面
        const val CLASS_PACKET_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI"              // 微信 红包详情


        /**
         * 红包“開”的窗口 id
         */
        const val ID_PACKET_DIALOG_OPEN = "com.tencent.mm:id/f4f"               // 聊天页面 - 红包对话框 - 开

        /**
         * 红包详情页 id
         */
        const val ID_PACKET_DETAIL_POST_USER = "com.tencent.mm:id/f03"          // 红包详情页 - 发送者信息 - "xxxx的红包"
        const val ID_PACKET_DETAIL_POST_NUM = "com.tencent.mm:id/eyq"           // 红包详情页 - 红包金额


        /**
         * 聊天页面 id
         */
        const val ID_CHAT_PAGER_ITEM = "com.tencent.mm:id/aui"                     // 聊天界面 - 聊天列表 - item
        const val ID_CHAT_PAGER_ITEM_AVATAR = "com.tencent.mm:id/au2"              // 聊天页面 - 聊天列表 - item - 头像
        const val ID_CHAT_PAGER_ITEM_PACKET = "com.tencent.mm:id/auf"             // 聊天页面 - 聊天列表 - 红包布局
        const val ID_CHAT_PAGER_ITEM_PACKET_MESSAGE = "com.tencent.mm:id/u1"     // 聊天页面 - 聊天列表 - 红包布局 - 祝福语 "恭喜发财" 等，用户自己编辑的
        const val ID_CHAT_PAGER_ITEM_PACKET_FLAG = "com.tencent.mm:id/u5"        // 聊天页面 - 聊天列表 - 红包布局 - 底部 "微信红包"
        const val ID_CHAT_PAGER_ITEM_PACKET_TIP = "com.tencent.mm:id/tt"         // 聊天页面 - 聊天列表 - 红包布局 - 红包状态 "已领取"
        const val ID_CHAT_PAGER_TITLE = "com.tencent.mm:id/ipt"                    // 聊天页面 - 聊天列表 - 个人聊天则是昵称，群聊天则是群昵称

        /**
         * 首页-微信tab 聊天会话 id
         */
        const val ID_HOME_CHAT_LIST_ITEM = "com.tencent.mm:id/bg1"                // 首页列表 - 聊天会话 - item id
        const val ID_HOME_CHAT_LIST_ITEM_MESSAGE = "com.tencent.mm:id/e7t"        // 微信列表 每一个item中的文本id
        const val ID_WID_CHAT_LIST_TITLE_TEXT = "com.tencent.mm:id/fzg"           // 微信列表 每一个item中的会话名字

        /**
         * 首页-我tab
         */
        const val ID_HOME_USER_PAGER_AVATAT = "com.tencent.mm:id/x1"                 // 我的页面，微信头像
        const val ID_HOME_USER_PAGER_NICK = "com.tencent.mm:id/fz_"                  // 我的页面，微信昵称
        const val ID_HOME_USER_PAGER_WEICHATNUM = "com.tencent.mm:id/j1l"            // 我的页面，微信号
        const val ID_HOME_TAB_TITLE = "android:id/text1"                                // 首页4个tab的标题，存在与微信tab，通讯录tab，发现tab，用于过滤没有必要的轮训,8.0.0整体结构大概，改成底部LauncherUIBottomTabView

    }


    override fun weChatVersion(): String = VERSION

    override fun launcherClass(): String = CLASS_LAUNCHER

    override fun packetReceiveClass(): String = CLASS_PACKET_RECEIVE

    override fun packetSendClass(): String = CLASS_PACKET_SEND

    override fun packetPayClass(): String = CLASS_PACKET_PAY

    override fun packetDetailClass(): String = CLASS_PACKET_DETAIL

    override fun packetDialogOpenId(): String = ID_PACKET_DIALOG_OPEN

    override fun packetDetailPostUserId(): String = ID_PACKET_DETAIL_POST_USER

    override fun packetDetailPostNumId(): String = ID_PACKET_DETAIL_POST_NUM

    override fun chatPagerItemId(): String = ID_CHAT_PAGER_ITEM

    override fun chatPagerItemAvatatId(): String = ID_CHAT_PAGER_ITEM_AVATAR

    override fun chatPagerItemPacketId(): String = ID_CHAT_PAGER_ITEM_PACKET

    override fun chatPagerItemPacketMessageId(): String = ID_CHAT_PAGER_ITEM_PACKET_MESSAGE

    override fun chatPagerItemPacketFlagId(): String = ID_CHAT_PAGER_ITEM_PACKET_FLAG

    override fun chatPagerItemPacketTipId(): String = ID_CHAT_PAGER_ITEM_PACKET_TIP

    override fun chatPagerTitleId(): String = ID_CHAT_PAGER_TITLE

    override fun homeChatListItemId(): String = ID_HOME_CHAT_LIST_ITEM

    override fun homeChatListItemMessageId(): String = ID_HOME_CHAT_LIST_ITEM_MESSAGE

    override fun homeChatListItemTextId(): String = ID_WID_CHAT_LIST_TITLE_TEXT

    override fun homeUserPagerAvatarId(): String  = ID_HOME_USER_PAGER_AVATAT

    override fun homeUserPagerNickId(): String = ID_HOME_USER_PAGER_NICK

    override fun homeUserPagerWeChatNumId(): String = ID_HOME_USER_PAGER_WEICHATNUM

    override fun homeTabTitleId(): String = ID_HOME_TAB_TITLE
}