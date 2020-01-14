package com.effective.android.wxrp.data.sp

import com.effective.android.wxrp.Constants

class SettingConfig {
    var supportGettingSelfPacket: Boolean = true

    var openPlugin = true
    var openFloat = false

    var supportFilterPacket = false
    val filterPacketTags = mutableListOf<String>()

    var supportFilterConversation = false
    val filterConversationTags = mutableListOf<String>()

    var delayModel: Int = Constants.VALUE_DELAY_CLOSE
    var fixationTime: Long = Constants.VALUE_DEFAULT_FIXATION
    var randomDelayTime: Long = Constants.VALUE_DEFAULT_FIXATION
}