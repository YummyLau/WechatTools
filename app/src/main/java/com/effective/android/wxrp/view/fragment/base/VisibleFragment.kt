package com.effective.android.wxrp.view.fragment.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.effective.android.wxrp.BuildConfig
import java.util.ArrayList

/**
 * 可监听fragment可视化变动的fragment
 *  参考 http://www.paincker.com/fragment-visibility
 * Created by yummyLau on 2019/08/21.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
abstract class VisibleFragment : Fragment(), View.OnAttachStateChangeListener, OnFragmentVisibilityChangeListener {

    private var mParentActivityVisible = false
    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    private var mVisible = false
    private var mParentFragment: VisibleFragment? = null
    private val mListener = ArrayList<OnFragmentVisibilityChangeListener>()

    fun addOnVisibilityChangedListener(listener: OnFragmentVisibilityChangeListener?) {
        if (listener != null && !mListener.contains(listener)) {
            mListener.add(listener)
        }
    }

    fun removeOnVisibilityChangedListener(listener: OnFragmentVisibilityChangeListener?) {
        if (listener != null && mListener.contains(listener)) {
            mListener.remove(listener)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parentFragment = parentFragment
        if (parentFragment is VisibleFragment) {
            mParentFragment = parentFragment as VisibleFragment
            mParentFragment?.addOnVisibilityChangedListener(this)
        }
        checkVisibility(true)
    }

    override fun onDetach() {
        mParentFragment?.removeOnVisibilityChangedListener(this)
        super.onDetach()
        checkVisibility(false)
        mParentFragment = null
    }

    override fun onResume() {
        super.onResume()
        onActivityVisibilityChanged(true)
    }

    override fun onPause() {
        super.onPause()
        onActivityVisibilityChanged(false)
    }

    internal fun onActivityVisibilityChanged(visible: Boolean) {
        mParentActivityVisible = visible
        checkVisibility(visible)
    }

    override fun onFragmentVisibilityChanged(visible: Boolean) {
        checkVisibility(visible)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.addOnAttachStateChangeListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        checkVisibility(hidden)
    }

    /**
     * Tab切换时会回调此方法。对于没有Tab的页面，[Fragment.getUserVisibleHint]默认为true。
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        checkVisibility(isVisibleToUser)
    }

    override fun onViewAttachedToWindow(v: View) {
        checkVisibility(true)
    }

    override fun onViewDetachedFromWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        checkVisibility(false)
    }

    /**
     * 检查可见性是否变化
     *
     * @param expected 可见性期望的值。只有当前值和expected不同，才需要做判断
     */
    private fun checkVisibility(expected: Boolean) {
        if (expected == mVisible) return
        val parentVisible = if (mParentFragment == null) mParentActivityVisible else mParentFragment?.isFragmentVisible()
        val superVisible = super.isVisible()
        val hintVisible = userVisibleHint
        val visible = parentVisible != null && parentVisible && superVisible && hintVisible
        if (BuildConfig.DEBUG && mParentFragment != null)
            Log.d("LogFragment", String.format("==> checkVisibility = %s  ( parent:%s = %s, super: = %s, hint:%s = %s )",
                    visible, mParentFragment?.javaClass?.simpleName, parentVisible, superVisible, javaClass.simpleName, hintVisible))
        if (visible != mVisible) {
            mVisible = visible
            onVisibilityChanged(mVisible)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mListener.clear()
    }

    internal fun onVisibilityChanged(visible: Boolean) {
        if (mListener != null) {
            for (fragmentVisibilityChangedListener in mListener) {
                fragmentVisibilityChangedListener.onFragmentVisibilityChanged(visible)
            }
        }
    }

    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    fun isFragmentVisible(): Boolean {
        return mVisible
    }


}