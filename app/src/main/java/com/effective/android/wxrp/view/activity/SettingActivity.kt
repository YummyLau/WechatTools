package com.effective.android.wxrp.view.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.LruCache
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cunoraz.tagview.Tag
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.utils.systemui.QMUIStatusBarHelper
import com.effective.android.wxrp.utils.systemui.StatusbarHelper
import com.effective.android.wxrp.view.dialog.TagEditDialog
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val currentFilterPacket = mutableListOf<Tag>()
    private lateinit var packetFilterAddDialog: TagEditDialog
    private val tagPacketCache = LruCache<String, Tag>(50)

    private val currentFilterConversation = mutableListOf<Tag>()
    private lateinit var conversationFilterAddDialog: TagEditDialog
    private val tagConversationCache = LruCache<String, Tag>(50)

    private var currentDelayNum: String = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        initListener()
        initData()
    }


    private fun initData() {

        pluginAction.isSelected = LocalizationHelper.isSupportPlugin()
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
                    packetContainer.addTags(currentFilterPacket)
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

        //返回
        back.setOnClickListener {
            finish()
        }

        //暂停插件
        pluginAction.setOnClickListener {
            val selectStatus = it.isSelected
            LocalizationHelper.supportPlugin(!selectStatus)
            it.isSelected = !selectStatus
        }

        //是否打开悬浮窗
        floatAction.setOnClickListener {

        }

        //是否抢自己的红包
        getSelfPacketAction.setOnClickListener {
            val selectStatus = getSelfPacketAction.isSelected
            LocalizationHelper.supportGettingSelfPacket(!selectStatus)
            getSelfPacketAction.isSelected = !selectStatus
        }

        //过滤会话
        filterConversationAction.setOnClickListener {
            val filterStatus = filterConversationAction.isSelected
            filerConversationContainer.visibility = if (!filterStatus) View.VISIBLE else View.GONE
            if (!filterStatus) {
                filerConversationContainer.post {
                    conversationContainer.addTags(currentFilterConversation)
                }
            }
            LocalizationHelper.supportFilterConversationTag(!filterStatus)
            filterConversationAction.isSelected = !filterStatus
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
            val filterStatus = filterPacketAction.isSelected
            filerPacketContainer.visibility = if (!filterStatus) View.VISIBLE else View.GONE
            if (!filterStatus) {
                filerPacketContainer.post {
                    packetContainer.addTags(currentFilterPacket)
                }
            }
            LocalizationHelper.supportFilterPacketTag(!filterStatus)
            filterPacketAction.isSelected = !filterStatus
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


    //
//    /**
//     * 检测浮窗权限是否开启，若没有给与申请提示框（非必须，申请依旧是EasyFloat内部内保进行）
//     */
//    private fun checkPermission() {
//        context?.let {
//            if (PermissionUtils.checkPermission(it)) {
//                showAppFloat()
//            } else {
//                AlertDialog.Builder(context)
//                        .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
//                        .setPositiveButton("去开启") { _, _ ->
//                            showAppFloat()
//                        }
//                        .setNegativeButton("取消") { _, _ -> }
//                        .show()
//            }
//        }
//    }
//
//    private fun showAppFloat() {
//        activity?.let {
//            EasyFloat.with(it)
//                .setShowPattern(ShowPattern.ALL_TIME)
//                .setSidePattern(SidePattern.RESULT_SIDE)
//                .setGravity(Gravity.CENTER)
//                .setLayout(R.layout.float_app, OnInvokeView {
//                    it.findViewById<TextView>(R.id.main_activity).setOnClickListener {
//                        startActivity(Intent(context, MainActivity::class.java))
//                    }
//
//                    it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
//                        EasyFloat.dismissAppFloat()
//                    }
//
//                    it.findViewById<View>(R.id.getSelf_select).setOnClickListener {
//                        val selectStatus = it.isSelected
//                        Config.openGetSelfPacket(!selectStatus)
//                        it.isSelected = !selectStatus
//                    }
//
//                    it.findViewById<View>(R.id.filter_select).setOnClickListener {
//                        val filterStatus = it.isSelected
//                        Config.openFilterTag(!filterStatus)
//                        it.isSelected = !filterStatus
//                    }
//
//                    val status1 = it.findViewById<TextView>(R.id.status)
//                    it.findViewById<TextView>(R.id.status).text = context?.getString(R.string.running_to_stop)
//
//                    it.findViewById<View>(R.id.switcher).isSelected = switcher.isSelected
//                    it.findViewById<View>(R.id.switcher).setOnClickListener {
//                        if (it.isSelected) {
//                            Config.switcher = false
//                            status.text = context?.getString(R.string.running_to_start)
//                            status1.text = context?.getString(R.string.running_to_start)
//                            switcher.isSelected = false
//                            it.isSelected = false
//                        } else {
//                            status.text = context?.getString(R.string.running_to_stop)
//                            status1.text = context?.getString(R.string.running_to_stop)
//                            switcher.isSelected = true
//                            it.isSelected = true
//                            Config.switcher = true
//                        }
//                    }
//                })
//                .show()
//        }
//    }

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
}