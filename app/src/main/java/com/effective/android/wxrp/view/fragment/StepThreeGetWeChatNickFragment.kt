package com.effective.android.wxrp.view.fragment

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.ToastUtil
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.version.VersionManager
import com.effective.android.wxrp.view.activity.MainActivity
import com.effective.android.wxrp.view.fragment.base.BaseFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.MainVm
import kotlinx.android.synthetic.main.fragment_get_wx_nick.*

/**
 * 微信昵称获取页面
 * created by yummylau on 2020/01/07
 */
class StepThreeGetWeChatNickFragment : BaseFragment() {

    lateinit var vm: MainVm

    override fun getLayoutRes(): Int = R.layout.fragment_get_wx_nick

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        initData()
        vm = ViewModelProviders.of(activity as MainActivity, MainVm.factory(RpApplication.repository())).get(MainVm::class.java)
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {
                if (visible) {
                    initData();
                    vm.finishStep(3)
                }
            }
        })
    }

    private fun initData() {
        val historyName = LocalizationHelper.getHistoryConfigName()
        if (!TextUtils.isEmpty(historyName)) {
            historyLayout.visibility = View.VISIBLE
            historyNameTip.text = String.format(context?.getString(R.string.setting_get_user_name_by_history)!!, historyName)
            useHistoryName.setOnClickListener {
                LocalizationHelper.setConfigName(historyName)
                vm.finishStep(3)
            }
        } else {
            historyLayout.visibility = View.GONE
        }
    }

    private fun initListener() {
        getUserName.setOnClickListener {
            try {
                val intent = Intent()
                val cmp = ComponentName(Constants.weChatPackageName, VersionManager.launcherClass())
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.component = cmp
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                ToastUtil.toast(context!!, context!!.getString(R.string.step_one_cant_support_chat_version_error))
            }
        }
    }
}
