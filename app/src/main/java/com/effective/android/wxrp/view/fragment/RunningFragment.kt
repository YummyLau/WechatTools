package com.effective.android.wxrp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.LruCache
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cunoraz.tagview.Tag
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.view.activity.MainActivity
import com.effective.android.wxrp.view.activity.SettingActivity
import com.effective.android.wxrp.view.fragment.base.BaseFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.MainVm
import com.lzf.easyfloat.EasyFloat
import kotlinx.android.synthetic.main.fragment_result.*
import java.util.*

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
                    status.text = if(LocalizationHelper.isSupportPlugin()) context?.getString(R.string.plugin_running_status_open) else context?.getString(R.string.plugin_running_status_close)
                }
            }
        })
        vm.loadPacketList()
    }

    private fun initView() {
        nick.text = LocalizationHelper.getConfigName()
        avatar.background = LocalizationHelper.getConfigAvatar()
        setting.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
        vm.getPacketData().observe(this, Observer { value ->
            value?.let {
                packet_list.setPackets(it)
            }
        })
    }

}
