package com.effective.android.wxrp.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.effective.android.wxrp.R
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.view.activity.MainActivity
import com.effective.android.wxrp.view.activity.SettingActivity
import com.effective.android.wxrp.view.fragment.base.BaseVmFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.MainVm
import com.effective.android.wxrp.vm.ResultVm
import kotlinx.android.synthetic.main.fragment_result.*

/**
 * 运行结果页面
 * created by yummylau on 2020/01/07
 */
class RunningFragment : BaseVmFragment<ResultVm>() {

    lateinit var vm: MainVm


    override fun getViewModel(): Class<ResultVm> = ResultVm::class.java

    override fun getLayoutRes(): Int = R.layout.fragment_result

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        vm = ViewModelProviders.of(activity as MainActivity, MainVm.factory(RpApplication.repository())).get(MainVm::class.java)
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {
                if (visible) {
                    vm.checkAllStep()
                }
            }
        })
    }

    private fun initView() {
        nick.text = Config.getUserWxName()
        setting.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }

    }
}
