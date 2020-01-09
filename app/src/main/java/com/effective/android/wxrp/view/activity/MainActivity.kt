package com.effective.android.wxrp.view.activity

import android.os.Bundle
import com.effective.android.wxrp.R
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Color
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.vm.MainVm
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.utils.systemui.QMUIStatusBarHelper
import com.effective.android.wxrp.utils.systemui.StatusbarHelper
import com.effective.android.wxrp.view.fragment.StepTwoCheckAccessibilityFragment
import com.effective.android.wxrp.view.fragment.StepOneCheckWeChatFragment
import com.effective.android.wxrp.view.fragment.StepThreeGetWeChatNickFragment
import com.effective.android.wxrp.view.fragment.RunningFragment


class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainVm
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        initView()
    }

    private fun initView() {
        val fragments = mutableListOf<Fragment>()
        fragments.add(StepOneCheckWeChatFragment())
        fragments.add(StepTwoCheckAccessibilityFragment())
        fragments.add(StepThreeGetWeChatNickFragment())
        fragments.add(RunningFragment())
        mainViewModel = ViewModelProviders.of(this, MainVm.factory(RpApplication.repository())).get(MainVm::class.java)
        supportFragmentManager.beginTransaction().replace(R.id.container, fragments[index], "").commit()

        mainViewModel.getStepLiveData().observe(this, Observer<Int> {
            var toIndex = it - 1
            if (toIndex > fragments.size - 1) {
                toIndex = fragments.size - 1
            }
            if (index != toIndex) {
                index = toIndex
                when (index) {
                    0 -> stateView.onStep1()
                    1 -> stateView.onStep2()
                    2 -> stateView.onStep3()
                    else -> stateView.onRunning()
                }
                supportFragmentManager.beginTransaction().replace(R.id.container, fragments[index], "").commit()
            }
        })
    }
}
