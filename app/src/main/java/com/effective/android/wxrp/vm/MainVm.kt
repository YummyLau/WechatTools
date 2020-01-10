package com.effective.android.wxrp.vm

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.effective.android.wxrp.Constants
import com.effective.android.wxrp.RpApplication
import com.effective.android.wxrp.data.db.PacketRecord
import com.effective.android.wxrp.data.db.PacketRepository
import com.effective.android.wxrp.data.sp.LocalizationHelper
import com.effective.android.wxrp.utils.ToolUtil
import com.effective.android.wxrp.utils.singleArgViewModelFactory
import com.effective.android.wxrp.version.VersionManager
import kotlinx.coroutines.*


class MainVm(private val repository: PacketRepository) : ViewModel() {

    companion object {
        val factory = singleArgViewModelFactory(::MainVm)
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val packets = MutableLiveData<List<PacketRecord>>()
    private val toStep = MutableLiveData<Int>()

    fun getStepLiveData() = toStep

    fun getPacketData() = packets

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun toCheckoutStepBeforeStatus(position: Int): Int {
        val stepOneFinish = VersionManager.versionInfo != null
        val stepTwoFinish = ToolUtil.isServiceRunning(RpApplication.instance(), Constants.applicationName + "." + Constants.accessibilityClassName)
        val stepThreeFinish = !TextUtils.isEmpty(LocalizationHelper.getConfigName())
        return when (position) {
            1 -> {
                if (stepOneFinish) 2 else 1
            }
            2 -> {
                if (stepOneFinish) {
                    if (stepTwoFinish) 3 else 2
                } else 1
            }
            3 -> {
                if (stepOneFinish) {
                    if (stepTwoFinish) {
                        if (stepThreeFinish) 4 else 3
                    } else 2
                } else 1
            }
            else -> 1
        }
    }

    fun checkAllStep() {
        var current = toCheckoutStepBeforeStatus(3)
        if (current == 4) {
            return
        }
        toStep.value = current
    }

    fun finishStep(step: Int) {
        toStep.value = toCheckoutStepBeforeStatus(step)
    }

    fun loadPacketList() {
        uiScope.launch(Dispatchers.Main + viewModelJob) {
            val packetRecords = async(Dispatchers.IO) {
                return@async repository.getPacketList()
            }.await()

            packets.value = packetRecords
        }
    }
}