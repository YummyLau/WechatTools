package com.effective.android.wxrp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.ViewModelProviders
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.view.activity.MainActivity
import com.effective.android.wxrp.view.fragment.base.BaseFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.MainVm
import kotlinx.android.synthetic.main.fragment_check_accessibility.*

/**
 * 服务设置页面
 * created by yummylau on 2020/01/07
 */
class StepTwoCheckAccessibilityFragment : BaseFragment() {

    lateinit var vm: MainVm

    override fun getLayoutRes(): Int = R.layout.fragment_check_accessibility

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        vm = ViewModelProviders.of(activity as MainActivity,  MainVm.factory(RpApplication.repository())).get(MainVm::class.java)
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {
                if (visible) {
                    vm.finishStep(2)
                }
            }
        })
    }


    private fun initListener() {
        open_accessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}