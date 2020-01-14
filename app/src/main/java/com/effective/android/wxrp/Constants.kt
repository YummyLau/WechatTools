package com.effective.android.wxrp

object Constants {

    @JvmStatic
    val logTag = "红包插件"

    @JvmStatic
    val weChatPackageName = "com.tencent.mm"                               // 微信包名

    @JvmStatic
    val weChat = "微信"                                                     // 微信

    @JvmStatic
    val applicationName = "com.effective.android.wxrp"                      // 本程序包名

    @JvmStatic
    val accessibilityClassName = "accessibility.WxAccessibilityService"          // 辅助服务类名

    @JvmStatic
    val weChatPacketTip = "[微信红包]"

    @JvmStatic
    val weChatPacket = "微信红包"

    @JvmStatic
    val tabTitleWeChat = "微信"

    @JvmStatic
    val tabTitleChatList = "通讯录"

    @JvmStatic
    val tabTitleDiscover = "发现"


    @JvmStatic
    val KEY_USER_WX_NAME = "key_user_wx_nick"
    @JvmStatic
    val KEY_USER_WX_AVATAR = "key_user_wx_avatar"
    @JvmStatic
    val KEY_OPEN_GET_SELF_PACKET = "key_open_get_self_packet"                 //是否打开自己红包
    @JvmStatic
    val KEY_FILTER_PACKET = "key_filter_packet"                               //是否需要过滤红包
    @JvmStatic
    val KEY_FILTER_PACKET_DATA = "key_filter_packet_data"
    @JvmStatic
    val VALUE_FILTER_PACKET_DATA: String = "测_&_挂_&_专属_&_生日_&_踢"
    @JvmStatic
    val SPLIT_POINT = "_&_"
    @JvmStatic
    val KEY_FILTER_CONVERSATION = "key_filter_conversation"                   //是否需要过滤会话
    @JvmStatic
    val KEY_FILTER_CONVERSATION_DATA = "key_filter_conversation_data"

    @JvmStatic
    val KEY_DELAY_MODEL = "key_delay_model"                                    //延迟模式
    @JvmStatic
    val VALUE_DELAY_CLOSE = 0                                                 //无延迟
    @JvmStatic
    val VALUE_DELAY_FIXATION = 1                                              //固定延迟
    @JvmStatic
    val VALUE_DELAY_RANDOM = 2                                                //随机延迟
    @JvmStatic
    val KEY_DELAY_MODEL_FIXATION = "key_delay_model_fixation"
    @JvmStatic
    val VALUE_DEFAULT_FIXATION = 1000L
    @JvmStatic
    val KEY_DELAY_MODEL_RANDOM = "key_delay_model_random"
    @JvmStatic
    val VALUE_DEFAULT_RANDOM = 1000L


}
