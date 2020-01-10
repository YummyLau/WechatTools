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
        getSelf_select.setOnClickListener {
            val selectStatus = getSelf_select.isSelected
            LocalizationHelper.supportGettingSelfPacket(!selectStatus)
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
            LocalizationHelper.supportFilterTag(!filterStatus)
            filter_select.isSelected = !filterStatus
        }

        tag_commit.setOnClickListener {
            tagDialg?.show()
        }

        tag_container.setOnTagDeleteListener { p0, p1, p2 ->
            tag_container.remove(p2)
            LocalizationHelper.getFilterTag().remove(p1?.text)
        }

        delay_none.setOnClickListener {
            LocalizationHelper.setDelayModel(Constants.VALUE_DELAY_CLOSE)
            initDelayState(Constants.VALUE_DELAY_CLOSE)
        }

        delay_random.setOnClickListener {
            LocalizationHelper.setDelayModel(Constants.VALUE_DELAY_RANDOM)
            initDelayState(Constants.VALUE_DELAY_RANDOM)
        }

        delay_fixation.setOnClickListener {
            LocalizationHelper.setDelayModel(Constants.VALUE_DELAY_FIXATION)
            initDelayState(Constants.VALUE_DELAY_FIXATION)
        }

        delay_num.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    delay_commit.text = this@SettingActivity.getString(R.string.delay_back)
                } else {
                    currentDelayNum = s.toString()
                    if (currentDelayNum.toInt() > 0 && currentDelayNum.toInt() != LocalizationHelper.getDelayTime(true)) {
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
                val time = currentDelayNum.toLong()
                LocalizationHelper.setDelayTime(time)
                Logger.i(logTag, "提交当前修改，是否是固定延迟 ：$isFixationDelay delayTime : $time")
                ToolUtil.toast(this, "已更新延迟时间")
            } else {
                Logger.i(logTag, "撤销当前时间修改")
                delay_num.setText(LocalizationHelper.getDelayTime(true).toString())
            }
        }
    }

    /**
     * 切换延迟
     */
    private fun initDelayState(delayModel: Int) {
        when (delayModel) {
            Constants.VALUE_DELAY_FIXATION -> {
                delay_none.isSelected = false
                delay_container.visibility = View.VISIBLE
                delay_random.isSelected = false
                delay_fixation.isSelected = true
                delay_message.text = this.getString(R.string.delay_fixation_message)
                currentDelayNum = LocalizationHelper.getDelayTime(true).toString()
                delay_num.setText(currentDelayNum)
                delay_commit.isEnabled = true

            }
            Constants.VALUE_DELAY_RANDOM -> {
                delay_none.isSelected = false
                delay_container.visibility = View.VISIBLE
                delay_random.isSelected = true
                delay_fixation.isSelected = false
                delay_message.text = this.getString(R.string.delay_random_message)
                currentDelayNum = LocalizationHelper.getDelayTime(true).toString()
                delay_num.setText(currentDelayNum)
                delay_commit.isEnabled = true

            }
            else -> {
                delay_none.isSelected = true
                delay_random.isSelected = false
                delay_fixation.isSelected = false
                delay_container.visibility = View.GONE
            }
        }
    }


    private fun initState() {
        getSelf_select.isSelected = LocalizationHelper.isSupportGettingSelfPacket()
        filter_select.isSelected = LocalizationHelper.isSupportFilter()
        filer_tag_container.visibility = if (filter_select.isSelected) View.VISIBLE else View.GONE
        if (filter_select.isSelected) {
            tag_container.addTags(currentTag)
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


}