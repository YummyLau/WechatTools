package com.effective.android.wxrp.data.sp

interface ConfigChangeListener {
    fun onSupportFloat(support: Boolean)

    fun onSupportPlugin(support: Boolean)

    fun onSupportGetSelfPacket(support: Boolean)

    fun onSupportFilterPacket(support: Boolean)

    fun onSupportFilterConversation(support: Boolean)
}