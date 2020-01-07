package com.effective.android.wxrp.view.activity

import android.os.Bundle
import com.effective.android.wxrp.R
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.effective.android.wxrp.vm.MainVm
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.utils.systemui.QMUIStatusBarHelper
import com.effective.android.wxrp.utils.systemui.StatusbarHelper
import com.effective.android.wxrp.view.fragment.CheckAccessibilityFragment
import com.effective.android.wxrp.view.fragment.GetWeChatNickFragment
import com.effective.android.wxrp.view.fragment.ResultFragment


class MainActivity : AppCompatActivity() {

    private var mainViewModel: MainVm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT)
        QMUIStatusBarHelper.setStatusBarLightMode(this)
        initView()
    }

    private fun initView() {
        val fragments = mutableListOf<Fragment>()
        fragments.add(CheckAccessibilityFragment())
        fragments.add(GetWeChatNickFragment())
        fragments.add(ResultFragment())
        val adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment = fragments[position]

            override fun getCount(): Int = fragments.size
        }
        pager.adapter = adapter
        pager.currentItem = 0
        mainViewModel = ViewModelProviders.of(this, MainVm.facotry(RpApplication.repository())).get(MainVm::class.java)
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> stateView.onStart()
                    1 -> stateView.onDoing()
                    2 -> stateView.onEnd()
                }
            }
        })
    }
}
