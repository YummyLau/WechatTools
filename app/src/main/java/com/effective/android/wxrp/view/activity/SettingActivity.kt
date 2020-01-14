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
import com.effective.android.wxrp.Constants.logTag
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.utils.systemui.QMUIStatusBarHelper
import com.effective.android.wxrp.utils.systemui.StatusbarHelper
import com.effective.android.wxrp.view.dialog.TagEditDialog
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val tagCache = LruCache<String, Tag>(99)
    private val currentTag = mutableListOf<Tag>()
    private var tagDialg: TagEditDialog? = null
    private var currentDelayNum: String = "-1"
    private var currentUserName: String = ""
    private var isFixationDelay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        initListener()
        initFilterView()
        initState()
    }

    private fun initFilterView() {
        //编辑tag对话框
        tagDialg = TagEditDialog(this, object : TagEditDialog.CommitListener {
            override fun commit(tag: String) {
                if (TextUtils.isEmpty(tag)) {
                    ToolUtil.toast(this@SettingActivity, this@SettingActivity.getString(R.string.tag_empty_tip))
                    return
                }
                val item = getTag(tag)
                packetContainer.addTag(item)
            }
        })

        //tag容器
        val tagStrings = LocalizationHelper.getFilterTag()
        tagStrings.map {
            var tag = tagCache[it]
            if (tag == null) {
                tag = Tag(it)
                tag.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
                tag.isDeletable = true
                tagCache.put(it, tag)
            }
            currentTag.add(tag)
        }
    }


    private fun initListener() {

        back.setOnClickListener {
            finish()
        }
        getSelfPacketAction.setOnClickListener {
            val selectStatus = getSelfPacketAction.isSelected
            LocalizationHelper.supportGettingSelfPacket(!selectStatus)
            getSelfPacketAction.isSelected = !selectStatus
        }

        filterPacketAction.setOnClickListener {
            val filterStatus = filterPacketAction.isSelected
            filerPacketContainer.visibility = if (!filterStatus) View.VISIBLE else View.GONE
            if (!filterStatus) {
                filerPacketContainer.post {
                    packetContainer.addTags(currentTag)
                }
            }
            LocalizationHelper.supportFilterTag(!filterStatus)
            filterPacketAction.isSelected = !filterStatus
        }

        packetCommit.setOnClickListener {
            tagDialg?.show()
        }

        packetContainer.setOnTagDeleteListener { p0, p1, p2 ->
            packetContainer.remove(p2)
            LocalizationHelper.getFilterTag().remove(p1?.text)
        }

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
                    if (currentDelayNum.toInt() > 0 && currentDelayNum.toInt() != LocalizationHelper.getDelayTime(true)) {
                        delayCommit.text = this@SettingActivity.getString(R.string.delay_edit)
                    } else {
                        delayCommit.text = this@SettingActivity.getString(R.string.delay_back)
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
                Logger.i(logTag, "提交当前修改，是否是固定延迟 ：$isFixationDelay delayTime : $time")
                ToolUtil.toast(this, "已更新延迟时间")
            } else {
                Logger.i(logTag, "撤销当前时间修改")
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


    private fun initState() {
        getSelfPacketAction.isSelected = LocalizationHelper.isSupportGettingSelfPacket()
        filterPacketAction.isSelected = LocalizationHelper.isSupportFilter()
        filerPacketContainer.visibility = if (filterPacketAction.isSelected) View.VISIBLE else View.GONE
        if (filterPacketAction.isSelected) {
            packetContainer.addTags(currentTag)
        }
        initDelayState(LocalizationHelper.getDelayModel())
        currentUserName = LocalizationHelper.getConfigName()
    }

    private fun getTag(key: String): Tag {
        var tag = tagCache[key]
        if (tag == null) {
            tag = Tag(key)
            tag.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
            tag.isDeletable = true
            tagCache.put(key, tag)
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
        val newFilterTag = mutableListOf<String>()
        val tags = packetContainer.tags
        for (tag in tags) {
            newFilterTag.add(tag.text)
        }
        LocalizationHelper.updateFilterTag(newFilterTag)
    }
}