package com.effective.android.wxrp.view.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.R
import com.effective.android.wxrp.data.sp.Config
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.view.fragment.base.BaseVmFragment
import com.effective.android.wxrp.view.fragment.base.OnFragmentVisibilityChangeListener
import com.effective.android.wxrp.vm.ResultVm
import kotlinx.android.synthetic.main.fragment_check_accessibility.*
import kotlinx.android.synthetic.main.fragment_result.*

/**
 * 运行结果页面
 * created by yummylau on 2020/01/07
 */
class ResultFragment : BaseVmFragment<ResultVm>() {

    override fun getViewModel(): Class<ResultVm> = ResultVm::class.java

    override fun getLayoutRes(): Int = R.layout.fragment_result

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addOnVisibilityChangedListener(object : OnFragmentVisibilityChangeListener {
            override fun onFragmentVisibilityChanged(visible: Boolean) {

            }
        })
    }


}
