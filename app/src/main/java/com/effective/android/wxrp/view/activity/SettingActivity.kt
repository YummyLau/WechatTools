package com.effective.android.wxrp.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.LruCache
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cunoraz.tagview.Tag
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.ConfigChangeListener
import com.effective.android.wxrp.data.sp.ConfigHelper
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.NotificationUtil
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.utils.systemui.QMUIStatusBarHelper
import com.effective.android.wxrp.utils.systemui.StatusbarHelper
import com.effective.android.wxrp.view.dialog.TagEditDialog
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.permission.PermissionUtils
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.float_app.view.*

class SettingActivity : AppCompatActivity() {

    private val currentFilterPacket = mutableListOf<Tag>()
    private lateinit var packetFilterAddDialog: TagEditDialog
    private val tagPacketCache = LruCache<String, Tag>(50)

    private val currentFilterConversation = mutableListOf<Tag>()
    private lateinit var conversationFilterAddDialog: TagEditDialog
    private val tagConversationCache = LruCache<String, Tag>(50)
    private var currentDelayNum: String = "-1"
    private val configUpdate = ConfigHelper.updater
    private lateinit var configChangeListener: ConfigChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        initListener()
        initData()
    }


    private fun initData() {

        if(NotificationUtil.isNotificationListenersEnabled(RpApplication.instance())){
            configUpdate.supportNotification(true)
        }
        pluginAction.isSelected = LocalizationHelper.isSupportPlugin()
        notificationAction.isSelected = LocalizationHelper.isSupportNotification()
        floatAction.isSelected = LocalizationHelper.isSupportFloat()
        getSelfPacketAction.isSelected = LocalizationHelper.isSupportGettingSelfPacket()

        //过滤会话
        conversationFilterAddDialog = TagEditDialog(this, object : TagEditDialog.CommitListener {
            override fun commit(tag: String) {
                if (TextUtils.isEmpty(tag)) {
                    ToolUtil.toast(this@SettingActivity, this@SettingActivity.getString(R.string.tag_empty_tip))
                    return
                }
                val item = getConversationTag(tag)
                conversationContainer.addTag(item)
            }
        })
        val tagConversationStrings = LocalizationHelper.getFilterConversationTag()
        tagConversationStrings.map {
            var tag = tagConversationCache[it]
            if (tag == null) {
                tag = Tag(it)
                tag.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
                tag.isDeletable = true
                tagConversationCache.put(it, tag)
            }
            currentFilterConversation.add(tag)
        }

        filterConversationAction.isSelected = LocalizationHelper.isSupportFilterConversation()
        filerConversationContainer.visibility =
                if (filterConversationAction.isSelected) {
                    conversationContainer.addTags(currentFilterConversation)
                    View.VISIBLE
                } else View.GONE


        //过滤红包关键字
        packetFilterAddDialog = TagEditDialog(this, object : TagEditDialog.CommitListener {
            override fun commit(tag: String) {
                if (TextUtils.isEmpty(tag)) {
                    ToolUtil.toast(this@SettingActivity, this@SettingActivity.getString(R.string.tag_empty_tip))
                    return
                }
                val item = getPacketTag(tag)
                packetContainer.addTag(item)
            }
        })

        val tagPacketStrings = LocalizationHelper.getFilterPacketTag()
        tagPacketStrings.map {
            var tag = tagPacketCache[it]
            if (tag == null) {
                tag = Tag(it)
                tag.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
                tag.isDeletable = true
                tagPacketCache.put(it, tag)
            }
            currentFilterPacket.add(tag)
        }

        filterPacketAction.isSelected = LocalizationHelper.isSupportFilterPacket()
        filerPacketContainer.visibility =
                if (filterPacketAction.isSelected) {
                    packetContainer.addTags(currentFilterPacket)
                    View.VISIBLE
                } else View.GONE

        initDelayState(LocalizationHelper.getDelayModel())
    }


    private fun initListener() {

        configChangeListener = object : ConfigChangeListener {

            override fun onSupportNotification(support: Boolean) {
                notificationAction.isSelected = support
            }

            override fun onSupportFloat(support: Boolean) {
                floatAction.isSelected = support
            }

            override fun onSupportPlugin(support: Boolean) {
                pluginAction.isSelected = support
            }

            override fun onSupportGetSelfPacket(support: Boolean) {
                getSelfPacketAction.isSelected = support
            }

            override fun onSupportFilterPacket(support: Boolean) {
                filerPacketContainer.visibility = if (support) View.VISIBLE else View.GONE
                if (support) {
                    filerPacketContainer.post {
                        packetContainer.addTags(currentFilterPacket)
                    }
                }
                filterPacketAction.isSelected = support
            }

            override fun onSupportFilterConversation(support: Boolean) {
                filerConversationContainer.visibility = if (support) View.VISIBLE else View.GONE
                if (support) {
                    filerConversationContainer.post {
                        conversationContainer.addTags(currentFilterConversation)
                    }
                }
                filterConversationAction.isSelected = support
            }
        }
        ConfigHelper.addListener(configChangeListener)

        //返回
        back.setOnClickListener {
            finish()
        }

        //暂停插件
        pluginAction.setOnClickListener {
            configUpdate.supportPlugin(!it.isSelected)
        }

        //通知栏
        notificationAction.setOnClickListener {
            if (notificationAction.isSelected) {
                configUpdate.supportNotification(false)
            } else {
                checkNotificationPermission()
            }
        }

        //是否打开悬浮窗
        floatAction.setOnClickListener {
            if (floatAction.isSelected) {
                EasyFloat.dismissAppFloat(getString(R.string.float_tag))
            } else {
                checkFloatPermission()
            }
        }

        //是否抢自己的红包
        getSelfPacketAction.setOnClickListener {
            configUpdate.supportGetSelfPacket(!it.isSelected)
        }

        //过滤会话
        filterConversationAction.setOnClickListener {
            configUpdate.supportFilterConversation(!filterConversationAction.isSelected)
        }

        conversationCommit.setOnClickListener {
            conversationFilterAddDialog.show()
        }

        conversationContainer.setOnTagDeleteListener { p0, p1, p2 ->
            conversationContainer.remove(p2)
            LocalizationHelper.getFilterConversationTag().remove(p1?.text)
        }

        //过滤红包关键字
        filterPacketAction.setOnClickListener {
            configUpdate.supportFilterPacket(!filterPacketAction.isSelected)
        }

        packetCommit.setOnClickListener {
            packetFilterAddDialog.show()
        }

        packetContainer.setOnTagDeleteListener { p0, p1, p2 ->
            packetContainer.remove(p2)
            LocalizationHelper.getFilterPacketTag().remove(p1?.text)
        }

        //延迟服务
        delayNone.setOnClickListener {
            LocalizationHelper.setDelayModel(Constants.VALUE_DELAY_CLOSE)
            initDelayState(Constants.VALUE_DELAY_CLOSE)
        }

        delayRandom.setOnClickListener {
            LocalizationHelper.setDelayModel(Constants.VALUE_DELAY_RANDOM)
            initDelayState(Constants.VALUE_DELAY_RANDOM)
        }

        delayFixation.setOnClickListener {
            LocalizationHelper.setDelayModel(Constants.VALUE_DELAY_FIXATION)
            initDelayState(Constants.VALUE_DELAY_FIXATION)
        }

        delayNum.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    delayCommit.text = this@SettingActivity.getString(R.string.delay_back)
                } else {
                    currentDelayNum = s.toString()
                    try {
                        if (currentDelayNum.toInt() > 0 && currentDelayNum.toInt() != LocalizationHelper.getDelayTime(true)) {
                            delayCommit.text = this@SettingActivity.getString(R.string.delay_edit)
                        } else {
                            delayCommit.text = this@SettingActivity.getString(R.string.delay_back)
                        }
                    } catch (e: Exception) {
                        delayNum.setText("")
                        ToolUtil.toast(this@SettingActivity, "请输入正确的延迟时间")
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        delayCommit.setOnClickListener {
            if (delayCommit.text == this.getString(R.string.delay_edit)) {
                val time = currentDelayNum.toLong()
                LocalizationHelper.setDelayTime(time)
                ToolUtil.toast(this, "已更新延迟时间")
            } else {
                delayNum.setText(LocalizationHelper.getDelayTime(true).toString())
            }
        }
    }

    /**
     * 切换延迟
     */
    private fun initDelayState(delayModel: Int) {
        when (delayModel) {
            Constants.VALUE_DELAY_FIXATION -> {
                delayNone.isSelected = false
                delayContainer.visibility = View.VISIBLE
                delayRandom.isSelected = false
                delayFixation.isSelected = true
                delayMessage.text = this.getString(R.string.delay_fixation_message)
                currentDelayNum = LocalizationHelper.getDelayTime(true).toString()
                delayNum.setText(currentDelayNum)
                delayCommit.isEnabled = true

            }
            Constants.VALUE_DELAY_RANDOM -> {
                delayNone.isSelected = false
                delayContainer.visibility = View.VISIBLE
                delayRandom.isSelected = true
                delayFixation.isSelected = false
                delayMessage.text = this.getString(R.string.delay_random_message)
                currentDelayNum = LocalizationHelper.getDelayTime(true).toString()
                delayNum.setText(currentDelayNum)
                delayCommit.isEnabled = true

            }
            else -> {
                delayNone.isSelected = true
                delayRandom.isSelected = false
                delayFixation.isSelected = false
                delayContainer.visibility = View.GONE
            }
        }
    }

    private fun getConversationTag(key: String): Tag {
        var tag = tagConversationCache[key]
        if (tag == null) {
            tag = Tag(key)
            tag.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            tag.isDeletable = true
            tagConversationCache.put(key, tag)
        }
        return tag
    }


    private fun getPacketTag(key: String): Tag {
        var tag = tagPacketCache[key]
        if (tag == null) {
            tag = Tag(key)
            tag.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            tag.isDeletable = true
            tagPacketCache.put(key, tag)
        }
        return tag
    }


    private fun checkNotificationPermission() {
        if (NotificationUtil.isNotificationListenersEnabled(this)) {
            configUpdate.supportNotification(true)
            ToolUtil.toast(this, "请确保微信同时开启[接受新消息通知-显示消息详情页].")
        } else {
            AlertDialog.Builder(this)
                    .setMessage("使用通知栏功能，需要您授权通知栏权限。")
                    .setPositiveButton("去开启") { _, _ ->
                        NotificationUtil.gotoNotificationAccessSetting(this)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .show()
        }
    }


    /**
     * 检测浮窗权限是否开启，若没有给与申请提示框（非必须，申请依旧是EasyFloat内部内保进行）
     */
    private fun checkFloatPermission() {
        if (PermissionUtils.checkPermission(this)) {
            if(!this?.isFinishing) {
                showAppFloat()
            }
        } else {
            AlertDialog.Builder(this)
                    .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
                    .setPositiveButton("去开启") { _, _ ->
                        if(!this?.isFinishing) {
                            showAppFloat()
                        }
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .show()
        }
    }

    private fun showAppFloat() {
        this.let {
            EasyFloat.with(it)
                    .setShowPattern(ShowPattern.ALL_TIME)
                    .setTag(getString(R.string.float_tag))
                    .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                    .setGravity(Gravity.END, 0, 100)
                    .setLayout(R.layout.float_app, OnInvokeView {

                        val entrance = it.findViewById<View>(R.id.floatEntrance)
                        val chooseContent = it.findViewById<View>(R.id.floatContent)
                        val chooseContentBack = it.findViewById<View>(R.id.chooseContentBack)
                        val runningChoose = it.findViewById<View>(R.id.runningChoose)
                        val getSelfChoose = it.findViewById<View>(R.id.getSelfChoose)
                        val notificationChoose = it.findViewById<View>(R.id.notificationChoose)
                        val filterConversationChoose = it.findViewById<View>(R.id.filterConversationChoose)
                        val filterPacketChoose = it.findViewById<View>(R.id.filterPacketChoose)
                        val more = it.findViewById<View>(R.id.more)

                        entrance.setOnClickListener {
                            entrance.visibility = View.GONE
                            chooseContent.visibility = View.VISIBLE
                        }

                        chooseContentBack.setOnClickListener {
                            entrance.visibility = View.VISIBLE
                            chooseContent.visibility = View.GONE
                        }

                        notificationChoose.setOnClickListener {
                            ConfigHelper.updater.supportNotification(!it.isSelected)
                        }

                        runningChoose.setOnClickListener {
                            ConfigHelper.updater.supportPlugin(!it.isSelected)
                        }

                        getSelfChoose.setOnClickListener {
                            ConfigHelper.updater.supportGetSelfPacket(!it.isSelected)
                        }

                        filterConversationChoose.setOnClickListener {
                            ConfigHelper.updater.supportFilterConversation(!it.isSelected)
                        }

                        filterPacketChoose.setOnClickListener {
                            ConfigHelper.updater.supportFilterPacket(!it.isSelected)
                        }

                        more.setOnClickListener {
                            this@SettingActivity.startActivity(Intent(this@SettingActivity, SettingActivity::class.java))
                            entrance.visibility = View.VISIBLE
                            chooseContent.visibility = View.GONE
                        }
                    })
                    .registerCallback {

                        val changeListener = object : ConfigChangeListener {

                            override fun onSupportFloat(support: Boolean) {
                                //不需要处理
                            }

                            override fun onSupportNotification(support: Boolean) {
                                EasyFloat.getAppFloatView(getString(R.string.float_tag))?.findViewById<View>(R.id.notificationChoose)?.isSelected = support
                            }

                            override fun onSupportPlugin(support: Boolean) {
                                EasyFloat.getAppFloatView(getString(R.string.float_tag))?.findViewById<View>(R.id.runningChoose)?.isSelected = support
                            }

                            override fun onSupportGetSelfPacket(support: Boolean) {
                                EasyFloat.getAppFloatView(getString(R.string.float_tag))?.findViewById<View>(R.id.getSelfChoose)?.isSelected = support
                            }

                            override fun onSupportFilterPacket(support: Boolean) {
                                EasyFloat.getAppFloatView(getString(R.string.float_tag))?.findViewById<View>(R.id.filterPacketChoose)?.isSelected = support
                            }

                            override fun onSupportFilterConversation(support: Boolean) {
                                EasyFloat.getAppFloatView(getString(R.string.float_tag))?.findViewById<View>(R.id.filterConversationChoose)?.isSelected = support
                            }
                        }

                        createResult { b, s, view ->
                            if (b) {
                                if(NotificationUtil.isNotificationListenersEnabled(RpApplication.instance())){
                                    ConfigHelper.updater.supportNotification(true)
                                }
                                view?.notificationChoose?.isSelected = LocalizationHelper.isSupportNotification()
                                view?.runningChoose?.isSelected = LocalizationHelper.isSupportPlugin()
                                view?.getSelfChoose?.isSelected = LocalizationHelper.isSupportGettingSelfPacket()
                                view?.filterConversationChoose?.isSelected = LocalizationHelper.isSupportFilterConversation()
                                view?.filterPacketChoose?.isSelected = LocalizationHelper.isSupportFilterPacket()
                                ConfigHelper.updater.supportFloat(true)
                                ConfigHelper.addListener(changeListener)
                            }
                        }

                        dismiss {
                            ConfigHelper.updater.supportFloat(false)
                            ConfigHelper.removeListener(changeListener)
                        }
                    }
                    .show()
        }


        EasyFloat.appFloatIsShow()
    }

    override fun onStop() {
        super.onStop()
        val newFilterPacketTag = mutableListOf<String>()
        val packetTags = packetContainer.tags
        for (tag in packetTags) {
            newFilterPacketTag.add(tag.text)
        }
        LocalizationHelper.updateFilterPacketTag(newFilterPacketTag)

        val newConversationsTag = mutableListOf<String>()
        val conversationTags = conversationContainer.tags
        for (tag in conversationTags) {
            newConversationsTag.add(tag.text)
        }
        LocalizationHelper.updateFilterConversationTag(newConversationsTag)
    }

    override fun onDestroy() {
        super.onDestroy()
        ConfigHelper.removeListener(configChangeListener)
    }
}