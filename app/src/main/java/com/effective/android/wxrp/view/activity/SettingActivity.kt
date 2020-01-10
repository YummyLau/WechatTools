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
import com.cunoraz.tagview.TagView
import com.effective.android.wxrp.Constants.logTag
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.utils.Logger
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.utils.systemui.QMUIStatusBarHelper
import com.effective.android.wxrp.utils.systemui.StatusbarHelper
import com.effective.android.wxrp.view.dialog.TagEditDialog
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {


    val tagCache = LruCache<String, Tag>(99)
    val currentTag = ArrayList<Tag>()
    var tagDialg: TagEditDialog? = null
    var currentDelayNum: String = "-1"
    var currentUserName: String = ""
    var isFixationDelay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        initData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        initState()
    }

    private fun initData() {
        //编辑tag对话框
        tagDialg = TagEditDialog(this, object : TagEditDialog.CommitListener {
            override fun commit(tag: String) {
                if (TextUtils.isEmpty(tag)) {
                    ToolUtil.toast(this@SettingActivity, this@SettingActivity.getString(R.string.tag_empty_tip))
                    return
                }
                tag_container.addTag(getTag(tag))
                Config.filterTags.add(tag)
                Config.onSave()
            }
        })
        //tag容器
        val tagStrings = Config.filterTags
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
        getSelf_select.setOnClickListener {
            val selectStatus = getSelf_select.isSelected
            Config.openGetSelfPacket(!selectStatus)
            getSelf_select.isSelected = !selectStatus
        }

        filter_select.setOnClickListener {
            val filterStatus = filter_select.isSelected
            filer_tag_container.visibility = if (!filterStatus) View.VISIBLE else View.GONE
            if (!filterStatus) {
                filer_tag_container.post {
                    tag_container.addTags(currentTag)
                }
            }
            Config.openFilterTag(!filterStatus)
            filter_select.isSelected = !filterStatus
        }

        tag_commit.setOnClickListener {
            tagDialg?.show()
        }

        tag_container.setOnTagDeleteListener(object : TagView.OnTagDeleteListener {

            override fun onTagDeleted(p0: TagView?, p1: Tag?, p2: Int) {
                tag_container.remove(p2)
                Config.filterTags.remove(p1?.text)
            }
        })

        delay_none.setOnClickListener {
            Config.openDelay(true)
            initDelayState(Config.isOpenDelay(), Config.isFixationDelay())
        }

        delay_random.setOnClickListener {
            Config.openDelay(false)
            Config.openFixationDelay(false)
            initDelayState(Config.isOpenDelay(), Config.isFixationDelay())
        }

        delay_fixation.setOnClickListener {
            Config.openDelay(false)
            Config.openFixationDelay(true)
            initDelayState(Config.isOpenDelay(), Config.isFixationDelay())
        }

        delay_num.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    delay_commit.text = this@SettingActivity.getString(R.string.delay_back)
                } else {
                    currentDelayNum = s.toString()
                    if (currentDelayNum.toInt() > 0 && currentDelayNum.toInt() != Config.getDelayTime(true)) {
                        delay_commit.text = this@SettingActivity.getString(R.string.delay_edit)
                    } else {
                        delay_commit.text = this@SettingActivity.getString(R.string.delay_back)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        delay_commit.setOnClickListener {
            if (delay_commit.text == this.getString(R.string.delay_edit)) {
                val time = currentDelayNum.toInt()
                Config.setDelayTime(time)
                Logger.i(logTag, "提交当前修改，是否是固定延迟 ：$isFixationDelay delayTime : $time")
                ToolUtil.toast(this, "已更新延迟时间")
            } else {
                Logger.i(logTag, "撤销当前时间修改")
                delay_num.setText(Config.getDelayTime(true).toString())
            }
        }
    }

    /**
     * 切换延迟
     */
    private fun initDelayState(openDelay: Boolean, isFixation: Boolean) {
        if (openDelay) {
            delay_none.isSelected = true
            delay_random.isSelected = false
            delay_fixation.isSelected = false
            delay_container.visibility = View.GONE
        } else {
            delay_none.isSelected = false
            delay_container.visibility = View.VISIBLE
            if (isFixation) {
                delay_random.isSelected = false
                delay_fixation.isSelected = true
                delay_message.text = this.getString(R.string.delay_fixation_message)
                currentDelayNum = Config.getDelayTime(true).toString()
                delay_num.setText(currentDelayNum)
                delay_commit.isEnabled = true
            } else {
                delay_random.isSelected = true
                delay_fixation.isSelected = false
                delay_message.text = this.getString(R.string.delay_random_message)
                currentDelayNum = Config.getDelayTime(true).toString()
                delay_num.setText(currentDelayNum)
                delay_commit.isEnabled = true
            }
        }
    }


    private fun initState() {
//        notification_select.isSelected = ToolUtil.isServiceRunning(this, VersionManager.applicationName + "." + VersionManager.CLASS_NOTIFICATION)
        getSelf_select.isSelected = Config.isOpenGetSelfPacket()
        filter_select.isSelected = Config.isOpenFilterTag()
        filer_tag_container.visibility = if (filter_select.isSelected) View.VISIBLE else View.GONE
        if (filter_select.isSelected) {
            tag_container.addTags(currentTag)
        }
        initDelayState(Config.isOpenDelay(), Config.isFixationDelay())
        currentUserName = Config.getUserWxName()
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


}