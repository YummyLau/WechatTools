package com.effective.android.wxrp.view.fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.ViewModelProviders
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.version.*
import com.effective.android.wxrp.view.activity.MainActivity
import com.effective.android.wxrp.view.fragment.base.BaseFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.MainVm
import kotlinx.android.synthetic.main.fragment_check_wx.*

class StepOneCheckWeChatFragment : BaseFragment() {

    lateinit var vm: MainVm

    override fun getLayoutRes(): Int = R.layout.fragment_check_wx

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProviders.of(activity as MainActivity,  MainVm.factory(RpApplication.repository())).get(MainVm::class.java)
        checkWeChatInfo()
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {
                if (visible) {
                    vm.finishStep(1)
                }
            }
        })
    }

    private fun checkWeChatInfo() {
        val hasWeChat = ToolUtil.installedWeChat(context!!)
        if (hasWeChat) {
            val version: String? = ToolUtil.getWeChatVersion(context!!)
            if (TextUtils.isEmpty(version)) {
                tip.text = context?.getString(R.string.step_one_get_we_chat_version_error)
            } else {
                if (ToolUtil.supportWeChatVersion(version)) {
                    VersionManager.versionInfo = when (version) {
                        Version700.VERSION -> Version700()
                        Version703.VERSION -> Version703()
                        Version7010.VERSION -> Version7010()
                        Version7011.VERSION -> Version7011()
                        Version7012.VERSION -> Version7012()
                        Version7013.VERSION -> Version7013()
                        Version7014.VERSION -> Version7014()
                        Version7015.VERSION -> Version7015()
                        Version7016.VERSION -> Version7016()
                        Version7017.VERSION -> Version7017()
                        Version7018.VERSION -> Version7018()
                        Version7019.VERSION -> Version7019()
                        Version7020.VERSION -> Version7020()
                        Version7021.VERSION -> Version7021()
                        Version7022.VERSION -> Version7022()
                        else -> Version800()
                    }
                } else {
                    tip.text = context?.getString(R.string.step_one_cant_support_chat_version_error)
                }
            }
        } else {
            tip.text = context?.getString(R.string.step_one_get_we_chat_error)
        }
        vm.finishStep(1)
    }
}