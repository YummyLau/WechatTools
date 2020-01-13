package com.effective.android.wxrp.view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.LruCache
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cunoraz.tagview.Tag
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.view.activity.MainActivity
import com.effective.android.wxrp.view.activity.SettingActivity
import com.effective.android.wxrp.view.fragment.base.BaseFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.MainVm
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.permission.PermissionUtils
import kotlinx.android.synthetic.main.fragment_result.*

/**
 * 运行结果页面
 * created by yummylau on 2020/01/07
 */
class RunningFragment : BaseFragment() {

    lateinit var vm: MainVm
    val currentTag = ArrayList<Tag>()
    val tagCache = LruCache<String, Tag>(99)

    override fun getLayoutRes(): Int = R.layout.fragment_result

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProviders.of(activity as MainActivity, MainVm.factory(RpApplication.repository())).get(MainVm::class.java)
        initView()
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {
                if (visible) {
                    vm.checkAllStep()
                    vm.loadPacketList()
                }
            }
        })
        vm.loadPacketList()
    }

    private fun initView() {
        nick.text = Config.getUserWxName()
        avatar.background = Config.getUserWxAvatar()
        status.text = context?.getString(R.string.running_to_stop)
        switcher.isSelected = true

        switcher.setOnClickListener {
            if (switcher.isSelected) {
                Config.switcher = false
                status.text = context?.getString(R.string.running_to_start)
                switcher.isSelected = false
            } else {
                status.text = context?.getString(R.string.running_to_stop)
                switcher.isSelected = true
                Config.switcher = true
            }
        }

        setting.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        vm.getPacketData().observe(this, Observer { value ->
            value?.let {
                packet_list.setPackets(it)
            }
        })

        open1.setOnClickListener {
            checkPermission()
        }
        dismiss1.setOnClickListener {
            EasyFloat.dismissAppFloat()
        }
    }

    private fun initData() {
        //tag容器
        val tagStrings = Config.filterTags
        tagStrings.map {
            var tag = tagCache[it]
            if (tag == null) {
                tag = Tag(it)
                tag.background = context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorPrimary) }?.let { it2 -> ColorDrawable(it2) }
                tag.isDeletable = true
                tagCache.put(it, tag)
            }
            currentTag.add(tag)
        }
    }

    /**
     * 检测浮窗权限是否开启，若没有给与申请提示框（非必须，申请依旧是EasyFloat内部内保进行）
     */
    private fun checkPermission() {
        context?.let {
            if (PermissionUtils.checkPermission(it)) {
                showAppFloat()
            } else {
                AlertDialog.Builder(context)
                        .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
                        .setPositiveButton("去开启") { _, _ ->
                            showAppFloat()
                        }
                        .setNegativeButton("取消") { _, _ -> }
                        .show()
            }
        }
    }

    private fun showAppFloat() {
        activity?.let {
            EasyFloat.with(it)
                .setShowPattern(ShowPattern.ALL_TIME)
                .setSidePattern(SidePattern.RESULT_SIDE)
                .setGravity(Gravity.CENTER)
                .setLayout(R.layout.float_app, OnInvokeView {
                    it.findViewById<TextView>(R.id.main_activity).setOnClickListener {
                        startActivity(Intent(context, MainActivity::class.java))
                    }

                    it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                        EasyFloat.dismissAppFloat()
                    }

                    it.findViewById<View>(R.id.getSelf_select).setOnClickListener {
                        val selectStatus = it.isSelected
                        Config.openGetSelfPacket(!selectStatus)
                        it.isSelected = !selectStatus
                    }

                    it.findViewById<View>(R.id.filter_select).setOnClickListener {
                        val filterStatus = it.isSelected
                        Config.openFilterTag(!filterStatus)
                        it.isSelected = !filterStatus
                    }

                    val status1 = it.findViewById<TextView>(R.id.status)
                    it.findViewById<TextView>(R.id.status).text = context?.getString(R.string.running_to_stop)

                    it.findViewById<View>(R.id.switcher).isSelected = switcher.isSelected
                    it.findViewById<View>(R.id.switcher).setOnClickListener {
                        if (it.isSelected) {
                            Config.switcher = false
                            status.text = context?.getString(R.string.running_to_start)
                            status1.text = context?.getString(R.string.running_to_start)
                            switcher.isSelected = false
                            it.isSelected = false
                        } else {
                            status.text = context?.getString(R.string.running_to_stop)
                            status1.text = context?.getString(R.string.running_to_stop)
                            switcher.isSelected = true
                            it.isSelected = true
                            Config.switcher = true
                        }
                    }
                })
                .show()
        }
    }
}
