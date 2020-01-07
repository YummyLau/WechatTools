package com.effective.android.wxrp.view.fragment

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.version.VersionManager
import com.effective.android.wxrp.view.fragment.base.BaseVmFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.GetWeChatNickVm
import kotlinx.android.synthetic.main.fragment_get_wx_nick.*

/**
 * 微信昵称获取页面
 * created by yummylau on 2020/01/07
 */
class GetWeChatNickFragment : BaseVmFragment<GetWeChatNickVm>() {

    override fun getViewModel(): Class<GetWeChatNickVm> = GetWeChatNickVm::class.java

    override fun getLayoutRes(): Int = R.layout.fragment_get_wx_nick

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
        val userName = Config.getUserWxName()
        if (!TextUtils.isEmpty(userName)) {
            get_user_name.text = getString(R.string.setting_get_again_action)
        } else {
            get_user_name.text = getString(R.string.setting_get_action)
        }
    }

    private fun initListener() {
        get_user_name.setOnClickListener {
            val hasOpenAccessibility = ToolUtil.isServiceRunning(this@GetWeChatNickFragment.context!!, Constants.applicationName + "." + Constants.accessibilityClassName)
            if (!hasOpenAccessibility) {
                ToolUtil.toast(this@GetWeChatNickFragment.context!!, "请先开启自动模拟点击服务")
                return@setOnClickListener
            }
            if (ToolUtil.isWeixinAvilible(this@GetWeChatNickFragment.context!!)) {
                if (ToolUtil.supportWeChatVersion(ToolUtil.getWeChatVersion(this@GetWeChatNickFragment.context!!))) {
                    val intent = Intent()
                    val cmp = ComponentName(Constants.weChatPackageName, VersionManager.launcherClass())
                    intent.action = Intent.ACTION_MAIN
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.component = cmp
                    startActivity(intent)
                } else {
                    ToolUtil.toast(this@GetWeChatNickFragment.context!!, "当前微信版本不支持！")
                }

            } else {
                ToolUtil.toast(this@GetWeChatNickFragment.context!!, "当前手机未安装微信，请下载 7.0.0/7.0.3/7.0.10 版本微信")
            }
        }
    }
}
