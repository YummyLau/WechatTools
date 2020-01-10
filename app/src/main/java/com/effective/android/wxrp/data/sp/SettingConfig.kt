package com.effective.android.wxrp.data.sp

import android.graphics.drawable.Drawable
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.RpApplication
import java.lang.StringBuilder
import java.util.*

class SettingConfig {
    var supportGettingSelfPacket: Boolean = true

    var openPlugin = true

    var supportFilter = false
    val filterTags = mutableListOf<String>()

    var delayModel: Int = Constants.VALUE_DELAY_CLOSE
    var fixationTime: Long = Constants.VALUE_DEFAULT_FIXATION
    var randomDelayTime: Long = Constants.VALUE_DEFAULT_FIXATION
}