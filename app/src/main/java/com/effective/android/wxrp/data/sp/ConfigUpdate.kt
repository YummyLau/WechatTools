package com.effective.android.wxrp.data.sp

interface ConfigUpdate {

    fun supportFloat(support: Boolean)

    fun supportPlugin(support: Boolean)

    fun supportGetSelfPacket(support: Boolean)

    fun supportFilterPacket(support: Boolean)

    fun supportFilterConversation(support: Boolean)
}