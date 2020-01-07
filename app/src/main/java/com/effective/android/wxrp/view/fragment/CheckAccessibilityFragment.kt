package com.effective.android.wxrp.view.fragment

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.version.VersionManager
import com.effective.android.wxrp.view.fragment.base.BaseVmFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.CheckAccessibilityVm
import kotlinx.android.synthetic.main.fragment_check_accessibility.*

/**
 * 服务设置页面
 * created by yummylau on 2020/01/07
 */
class CheckAccessibilityFragment : BaseVmFragment<CheckAccessibilityVm>() {

    override fun getLayoutRes(): Int = R.layout.fragment_check_accessibility

    override fun getViewModel(): Class<CheckAccessibilityVm> = CheckAccessibilityVm::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {
                initNecessaryState()
            }
        })
    }

    private fun initNecessaryState() {
        val hasOpenAccessibility = ToolUtil.isServiceRunning(this@CheckAccessibilityFragment.context!!, Constants.applicationName + "." + Constants.accessibilityClassName)
        if (hasOpenAccessibility) {
            open_accessibility.visibility = View.GONE
        } else {
            open_accessibility.visibility = View.VISIBLE
        }
//
//        val openSetting = hasOpenAccessibility && !TextUtils.isEmpty(userName)
//        more_setting_tip.visibility = setting.visibility
//        packet_list.visibility = setting.visibility
    }

    private fun initListener() {
        open_accessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}