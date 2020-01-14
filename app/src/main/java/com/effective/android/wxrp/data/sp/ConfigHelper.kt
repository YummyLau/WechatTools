package com.effective.android.wxrp.data.sp

object ConfigHelper {

    @JvmStatic
    val updater = object : ConfigUpdate {

        override fun supportFloat(support: Boolean) {
            LocalizationHelper.supportFloat(support)
            for (listener in listeners) {
                listener.onSupportFloat(support)
            }
        }

        override fun supportPlugin(support: Boolean) {
            LocalizationHelper.supportPlugin(support)
            for (listener in listeners) {
                listener.onSupportPlugin(support)
            }
        }

        override fun supportGetSelfPacket(support: Boolean) {
            LocalizationHelper.supportGettingSelfPacket(support)
            for (listener in listeners) {
                listener.onSupportGetSelfPacket(support)
            }
        }

        override fun supportFilterPacket(support: Boolean) {
            LocalizationHelper.supportFilterPacketTag(support)
            for (listener in listeners) {
                listener.onSupportFilterPacket(support)
            }
        }

        override fun supportFilterConversation(support: Boolean) {
            LocalizationHelper.supportFilterConversationTag(support)
            for (listener in listeners) {
                listener.onSupportFilterConversation(support)
            }
        }
    }

    private val listeners: MutableList<ConfigChangeListener> = mutableListOf();

    @JvmStatic
    fun addListener(listener: ConfigChangeListener) {
        if (listeners.contains(listener)) {
            return
        }
        listeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: ConfigChangeListener) {
        if (!listeners.contains(listener)) {
            return
        }
        listeners.remove(listener)
    }
}